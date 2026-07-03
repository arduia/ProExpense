package com.arduia.expense.feature.debt

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink

/** Validates and creates a new debt record (design plan §DebtViewModel). */
class CreateDebtUseCase(
    private val debtRepository: DebtRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(
        personName: String,
        rawAmount: String,
        direction: DebtDirection,
        currencyCode: String = "USD",
        dueEpochMillis: Long? = null,
        note: String? = null,
    ): Boolean {
        val amount = Amount.parseOrNull(rawAmount)?.takeIf { it.valueInCents > 0 } ?: return false
        val debt = Debt(
            id = DebtId(newDebtId(personName, nowEpochMillis())),
            personName = personName,
            money = Money(amount, CurrencyCode(currencyCode)),
            direction = direction,
            dueEpochMillis = dueEpochMillis,
            note = note?.ifBlank { null },
        )
        debtRepository.upsert(debt)
        return true
    }

    private fun newDebtId(person: String, now: Long): String =
        person.trim().lowercase() + "-" + now
}

/** Updates an existing debt's editable fields, preserving its id/direction/settled status. */
class UpdateDebtUseCase(private val debtRepository: DebtRepository) {
    suspend operator fun invoke(
        existing: Debt,
        personName: String,
        rawAmount: String,
        dueEpochMillis: Long?,
        note: String? = null,
    ): Boolean {
        val amount = Amount.parseOrNull(rawAmount)?.takeIf { it.valueInCents > 0 } ?: return false
        debtRepository.upsert(
            existing.copy(
                personName = personName,
                money = existing.money.copy(amount = amount),
                dueEpochMillis = dueEpochMillis,
                note = note?.ifBlank { null },
            ),
        )
        return true
    }
}

/** Deleting a debt clears its dangling link on any expense still tagged to it (US-DEBT-3). */
class DeleteDebtUseCase(
    private val debtRepository: DebtRepository,
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(id: String) {
        val records = (financeRecordRepository.getAll() as? Result.Success)?.data.orEmpty()
        records
            .filter { (it.link as? RecordLink.ToDebt)?.debtId?.value == id }
            .forEach { record -> financeRecordRepository.upsert(record.copy(link = RecordLink.None)) }
        debtRepository.delete(DebtId(id))
    }
}

class SettleDebtUseCase(private val debtRepository: DebtRepository) {
    suspend operator fun invoke(debt: Debt) {
        debtRepository.upsert(debt.copy(isSettled = true))
    }
}

/** Portable active/settled/net breakdown for one debt direction. */
data class DebtAggregate(
    val netCents: Long,
    val active: List<Debt>,
    val settled: List<Debt>,
)

class AggregateDebtsUseCase {
    operator fun invoke(debts: List<Debt>, direction: DebtDirection): DebtAggregate {
        val matching = debts.filter { it.direction == direction }
        val active = matching.filter { !it.isSettled }
        val settled = matching.filter { it.isSettled }
        val netCents = active.sumOf { it.money.amount.valueInCents }
        return DebtAggregate(netCents, active, settled)
    }
}

/** Warns when a person already has an active record on the opposite side (design plan §US-DEBT-4). */
class CheckDebtConflictUseCase(private val debtRepository: DebtRepository) {
    suspend operator fun invoke(personName: String, direction: DebtDirection): Boolean {
        val opposite = direction.opposite()
        return when (val result = debtRepository.findByPersonName(personName)) {
            is Result.Success -> result.data.any { it.direction == opposite && !it.isSettled }
            is Result.Error -> false
        }
    }

    private fun DebtDirection.opposite(): DebtDirection = when (this) {
        DebtDirection.OWED_TO_ME -> DebtDirection.I_OWE
        DebtDirection.I_OWE -> DebtDirection.OWED_TO_ME
    }
}
