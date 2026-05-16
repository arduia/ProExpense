package com.arduia.expense.ui.home

import com.arduia.graph.SpendGraph

class ExpenseGraphAdapter : SpendGraph.Adapter(){

    var expenseMap = mapOf<Int,Int>()
    set(value) {
        field = value
        notifyDataChanged()
    }

    override fun getRate(day: Int): Int {
        return expenseMap[day] ?: -1
    }
}
