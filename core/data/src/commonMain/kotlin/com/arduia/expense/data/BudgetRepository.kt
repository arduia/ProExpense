package com.arduia.expense.data

import com.arduia.expense.domain.Amount

interface BudgetRepository {
    suspend fun getMonthlyBudget(): Result<Amount?>

    suspend fun setMonthlyBudget(amount: Amount?): Result<Unit>
}
