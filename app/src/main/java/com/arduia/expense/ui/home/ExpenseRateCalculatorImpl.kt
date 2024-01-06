package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class ExpenseRateCalculatorImpl(private val scope: CoroutineScope) : ExpenseRateCalculator {

    private val mCalendar = Calendar.getInstance()

    private val expenseListCH = ConflatedBroadcastChannel<List<ExpenseEnt>>()

    private val ratesMapCH = ConflatedBroadcastChannel<Map<Int, Int>>()

    override fun getRates(): Flow<Map<Int, Int>> = ratesMapCH.asFlow()

    init {
        observeExpenseList()
    }

    private fun observeExpenseList() {
        expenseListCH.asFlow()
            .flowOn(Dispatchers.IO)
            .onEach {
                val dailyCosts = it.getDailyCosts()
                val maxCost = dailyCosts.maxOfOrNull { cost -> cost.value }?: 0f
                val result = mutableMapOf<Int, Int>()
                (1..7).forEach { count ->
                    val costOfDay = dailyCosts[count] ?: return@forEach
                    val rateOfDay = (costOfDay.toDouble() / maxCost) * 100
                    result[count] = rateOfDay.toInt()
                }
                ratesMapCH.trySend(result)
            }
            .launchIn(scope)
    }

    private fun List<ExpenseEnt>.getDailyCosts(): Map<Int, Float> {
        val amountOfWeek = mutableMapOf<Int, Float>()
        forEach {
            mCalendar.timeInMillis = it.modifiedDate
            val dayOfWeek = mCalendar[Calendar.DAY_OF_WEEK]
            amountOfWeek[dayOfWeek] = it.amount.getActual().toFloat() + (amountOfWeek[dayOfWeek] ?: 0f)
        }
        return amountOfWeek
    }

    override suspend fun setWeekExpenses(list: List<ExpenseEnt>) {
        expenseListCH.trySend(list)
    }

}

class ExpenseRateCalculatorFactory @Inject constructor(): ExpenseRateCalculator.Factory{
    override fun create(scope: CoroutineScope): ExpenseRateCalculator {
        return ExpenseRateCalculatorImpl(scope)
    }
}