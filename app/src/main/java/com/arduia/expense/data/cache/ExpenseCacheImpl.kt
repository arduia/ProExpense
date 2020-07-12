package com.arduia.expense.data.cache

import com.arduia.expense.data.local.ExpenseEnt

object ExpenseCacheImpl: ExpenseCache {
    override var expenseOfWeek = listOf<ExpenseEnt>()
}
