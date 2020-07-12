package com.arduia.expense.data.cache

import com.arduia.expense.data.local.ExpenseEnt

interface ExpenseCache {
    var expenseOfWeek: List<ExpenseEnt>
}
