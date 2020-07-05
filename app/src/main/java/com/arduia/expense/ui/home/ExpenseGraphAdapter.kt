package com.arduia.expense.ui.home

import com.arduia.expense.ui.vto.ExpensePointVto
import com.arduia.graph.SpendGraph
import kotlin.random.Random
import kotlin.random.nextInt

class ExpenseGraphAdapter : SpendGraph.Adapter(){

    var points = ExpensePointVto()
    set(value) {
        field = value
        notifyDataChanged()
    }

    override fun getCostRate(day: Int): Int = points.rates[day] ?: -1

//    override fun getStartDay(): Int =  points.rates.keys.min()?: SpendGraph.SPEND_DAY_SUN
    override fun getStartDay(): Int = SpendGraph.SPEND_DAY_SUN

    override fun getEndDay(): Int =  points.rates.keys.max()?: SpendGraph.SPEND_DAY_SUN

}
