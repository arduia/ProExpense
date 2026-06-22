package com.arduia.expense.storage.repository

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightBudgetRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), BudgetRepository {

    private val queries get() = database.appMetaQueries

    override suspend fun getMonthlyBudget(): Result<Amount?> = execute {
        queries.ensureMeta()
        queries.selectMeta().executeAsOne().monthly_budget_cents?.let { Amount(it) }
    }

    override suspend fun setMonthlyBudget(amount: Amount?): Result<Unit> = execute {
        queries.ensureMeta()
        queries.updateMonthlyBudget(amount?.valueInCents)
    }
}
