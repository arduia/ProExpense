package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt
import timber.log.Timber
import java.util.*

class ExpenseRateCalculatorImpl : ExpenseRateCalculator{

    private val calendar = Calendar.getInstance()
    private var list = listOf<ExpenseEnt>()

    override fun getRates(): Map<Int, Int> {

        val dailyCosts = getDailyCosts()

        val maxCost = dailyCosts.maxBy { it.value }?.value ?:0L

        val result = mutableMapOf<Int, Int>()
        (1..7).forEach {
            val costOfDay = dailyCosts[it]?: return@forEach
            val rateOfDay = (costOfDay.toDouble() / maxCost) * 100
            result[it] = rateOfDay.toInt()
        }

        return result
    }

    private fun getDailyCosts(): Map<Int, Long>{
        val amountOfWeek = mutableMapOf<Int, Long>()

        list.forEach {
            calendar.timeInMillis = it.created_date
            val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
            amountOfWeek[dayOfWeek]  = it.amount + (amountOfWeek[dayOfWeek]?:0)
        }
        return amountOfWeek
    }

    override fun setWeekExpenses(list: List<ExpenseEnt>) {
        this.list = list
    }

}
