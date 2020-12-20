package com.arduia.expense.ui.statistics

import com.arduia.expense.data.local.ExpenseEnt

interface CategoryAnalyzer {
    fun analyze(entities: List<ExpenseEnt>): List<CategoryStatisticVo>
}