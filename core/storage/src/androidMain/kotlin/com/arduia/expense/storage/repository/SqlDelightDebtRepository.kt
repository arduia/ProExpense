package com.arduia.expense.storage.repository

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.DebtQueries
import com.arduia.expense.storage.mapping.toDomain
import com.arduia.expense.storage.mapping.toCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightDebtRepository(
    private val queries: DebtQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DebtRepository {

    override suspend fun getAll(): Result<List<Debt>> = withContext(dispatcher) {
        catchingResult { queries.selectAllDebts().executeAsList().map { it.toDomain() } }
    }

    override suspend fun getById(id: DebtId): Result<Debt?> = withContext(dispatcher) {
        catchingResult { queries.selectDebtById(id.value).executeAsOneOrNull()?.toDomain() }
    }

    override suspend fun upsert(debt: Debt): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            queries.insertDebt(
                id = debt.id.value,
                person_name = debt.personName,
                amount_cents = debt.money.amount.valueInCents,
                currency_code = debt.money.currency.code,
                direction = debt.direction.toCode(),
                due_epoch_millis = debt.dueEpochMillis,
                is_settled = if (debt.isSettled) 1L else 0L,
            )
            Unit
        }
    }

    override suspend fun delete(id: DebtId): Result<Unit> = withContext(dispatcher) {
        catchingResult { queries.deleteDebt(id.value); Unit }
    }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        withContext(dispatcher) {
            catchingResult {
                queries.selectDebtsByPerson(personName).executeAsList().map { it.toDomain() }
            }
        }
}
