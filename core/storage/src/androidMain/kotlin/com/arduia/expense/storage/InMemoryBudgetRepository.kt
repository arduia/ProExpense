package com.arduia.expense.storage

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount

class InMemoryBudgetRepository(
    private val store: InMemoryDataStore,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Amount?> {
        val cents = store.monthlyBudgetCents
        return Result.Success(cents?.let { Amount(it) })
    }

    override suspend fun setMonthlyBudget(amount: Amount?): Result<Unit> {
        store.monthlyBudgetCents = amount?.valueInCents
        return Result.Success(Unit)
    }
}
