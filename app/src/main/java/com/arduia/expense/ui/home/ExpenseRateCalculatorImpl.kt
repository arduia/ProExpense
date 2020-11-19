package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.*

class ExpenseRateCalculatorImpl : ExpenseRateCalculator {

    private val mCalendar = Calendar.getInstance()

    private val expenseListCH = ConflatedBroadcastChannel<List<ExpenseEnt>>()

    private val ratesMapCH = ConflatedBroadcastChannel<Map<Int, Int>>()

    override fun getRates(): Flow<Map<Int, Int>> = ratesMapCH.asFlow()

     init {
         observeExpenseList()
     }

    private fun observeExpenseList(){
        expenseListCH.asFlow()
            .flowOn(Dispatchers.IO)
            .onEach {
                val dailyCosts = it.getDailyCosts()
                val maxCost = dailyCosts.maxOfOrNull { cost -> cost.value }?:0f
                val result = mutableMapOf<Int, Int>()
                (1..7).forEach {count ->
                    val costOfDay = dailyCosts[count] ?: return@forEach
                    val rateOfDay = (costOfDay.toDouble() / maxCost) * 100
                    result[count] = rateOfDay.toInt()
                }
                ratesMapCH.offer(result)
            }
    }

    private fun List<ExpenseEnt>.getDailyCosts(): Map<Int, Float> {
        val amountOfWeek = mutableMapOf<Int, Float>()
        forEach {
            mCalendar.timeInMillis = it.createdDate
            val dayOfWeek = mCalendar[Calendar.DAY_OF_WEEK]
            amountOfWeek[dayOfWeek] = it.amount + (amountOfWeek[dayOfWeek] ?: 0f)
        }
        return amountOfWeek
    }

    override suspend fun setWeekExpenses(list: List<ExpenseEnt>) {
        expenseListCH.offer(list)
    }
}
