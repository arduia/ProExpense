package com.arduia.expense.ui.home

import com.arduia.expense.data.local.ExpenseEnt
import java.util.*

class ExpenseRateCalculatorImpl : ExpenseRateCalculator{

    private val mCalendar = Calendar.getInstance()

    private var mExpenseLists = listOf<ExpenseEnt>()

    override fun getRates(): Map<Int, Int> {

        val dailyCosts = getDailyCosts()
        val maxCost = dailyCosts.maxBy { it.value }?.value ?: return emptyMap()
        val result = mutableMapOf<Int, Int>()

        (1..7).forEach {
            val costOfDay = dailyCosts[it]?: return@forEach
            val rateOfDay = (costOfDay.toDouble() / maxCost) * 100
            result[it] = rateOfDay.toInt()
        }

        return result
    }

    private fun getDailyCosts(): Map<Int, Float>{

        val amountOfWeek = mutableMapOf<Int, Float>()

        mExpenseLists.forEach {
            mCalendar.timeInMillis = it.createdDate
            val dayOfWeek = mCalendar[Calendar.DAY_OF_WEEK]
            amountOfWeek[dayOfWeek]  = it.amount + (amountOfWeek[dayOfWeek]?:0f)
        }

        return amountOfWeek
    }

    override fun setWeekExpenses(list: List<ExpenseEnt>) {
        this.mExpenseLists = list
    }

}
