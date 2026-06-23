package com.arduia.expense.data

import com.arduia.expense.domain.Money

interface BudgetRepository {
    suspend fun getMonthlyBudget(): Result<Money?>

    suspend fun setMonthlyBudget(money: Money?): Result<Unit>
}
