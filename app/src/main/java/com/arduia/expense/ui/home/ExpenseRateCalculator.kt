package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt

interface ExpenseRateCalculator {

    fun getRates(): Map<Int,Int>

    fun setWeekExpenses(list: List<ExpenseEnt>)

}
