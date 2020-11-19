package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt
import kotlinx.coroutines.flow.Flow

interface ExpenseRateCalculator {

    fun getRates(): Flow<Map<Int,Int>>

    suspend fun setWeekExpenses(list: List<ExpenseEnt>)

}
