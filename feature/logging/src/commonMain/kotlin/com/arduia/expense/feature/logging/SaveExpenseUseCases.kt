package com.arduia.expense.feature.logging

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType

/** Raw, platform-agnostic form values for creating or editing an expense record. */
data class SaveExpenseInput(
    val rawAmount: String,
    val currencyCode: String,
    val categoryId: String,
    val note: String,
    val recordedAtEpochMillis: Long,
    val linkTagId: String? = null,
    val linkTagKind: TagOptionKind? = null,
)

sealed class SaveExpenseOutcome {
    data class Saved(val record: FinanceRecord) : SaveExpenseOutcome()
    object InvalidAmount : SaveExpenseOutcome()
    data class Failed(val message: String) : SaveExpenseOutcome()
}

private fun SaveExpenseInput.toLink(): RecordLink = when (linkTagKind) {
    TagOptionKind.EVENT -> RecordLink.ToEvent(EventId(linkTagId.orEmpty()))
    TagOptionKind.DEBT -> RecordLink.ToDebt(DebtId(linkTagId.orEmpty()))
    null -> RecordLink.None
}

/** Validates and creates a new expense record (design plan §AddExpenseViewModel). */
class LogExpenseUseCase(
    private val loggingRepository: LoggingRepository,
) {
    suspend operator fun invoke(input: SaveExpenseInput): SaveExpenseOutcome {
        val amount = Amount.parseOrNull(input.rawAmount) ?: return SaveExpenseOutcome.InvalidAmount
        val money = Money(amount, CurrencyCode(input.currencyCode))
        val recordInput = LogRecordInput(
            money = money,
            homeCurrencyMoney = money,
            categoryId = CategoryId(input.categoryId),
            type = RecordType.EXPENSE,
            note = input.note.ifBlank { null },
            recordedAtEpochMillis = input.recordedAtEpochMillis,
            link = input.toLink(),
        )
        return when (val result = loggingRepository.createRecord(recordInput)) {
            is Result.Success -> SaveExpenseOutcome.Saved(result.data)
            is Result.Error -> SaveExpenseOutcome.Failed(result.message)
        }
    }
}

/** Validates and applies edits to an existing expense record. */
class UpdateExpenseUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(existing: FinanceRecord, input: SaveExpenseInput): SaveExpenseOutcome {
        val amount = Amount.parseOrNull(input.rawAmount) ?: return SaveExpenseOutcome.InvalidAmount
        val money = Money(amount, CurrencyCode(input.currencyCode))
        // Preserves a pre-existing shared-cost link when the user leaves the tag untouched, since
        // the tag picker only surfaces events and debts.
        val link = if (input.linkTagKind == null && existing.link is RecordLink.ToSharedCost) {
            existing.link
        } else {
            input.toLink()
        }
        val updated = existing.copy(
            money = money,
            homeCurrencyMoney = money,
            categoryId = CategoryId(input.categoryId),
            note = input.note.ifBlank { null },
            recordedAtEpochMillis = input.recordedAtEpochMillis,
            link = link,
        )
        return when (val result = financeRecordRepository.upsert(updated)) {
            is Result.Success -> SaveExpenseOutcome.Saved(updated)
            is Result.Error -> SaveExpenseOutcome.Failed(result.message)
        }
    }
}

/** Loads an existing record for the edit flow. */
class LoadExpenseForEditUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(recordId: String): FinanceRecord? {
        val result = financeRecordRepository.getById(com.arduia.expense.domain.RecordId(recordId))
        return (result as? Result.Success)?.data
    }
}
