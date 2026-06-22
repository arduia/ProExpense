package com.arduia.expense.storage.repository

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Debt
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toDb
import com.arduia.expense.storage.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightDebtRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), DebtRepository {

    private val queries get() = database.debtQueries

    override suspend fun getAll(): Result<List<Debt>> = execute {
        queries.selectAllDebts().executeAsList().map { it.toDomain() }
    }

    override suspend fun getById(id: String): Result<Debt?> = execute {
        queries.selectDebtById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsert(debt: Debt): Result<Unit> = execute {
        queries.insertDebt(
            id = debt.id,
            person_name = debt.personName,
            amount_cents = debt.amount.valueInCents,
            direction = debt.direction.name,
            due_epoch_millis = debt.dueEpochMillis,
            is_settled = debt.isSettled.toDb(),
        )
    }

    override suspend fun delete(id: String): Result<Unit> = execute {
        queries.deleteDebt(id)
    }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> = execute {
        queries.selectDebtsByPerson(personName).executeAsList().map { it.toDomain() }
    }
}
