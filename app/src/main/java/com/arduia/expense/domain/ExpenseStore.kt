package com.arduia.expense.domain

import com.arduia.currencystore.Rate
import com.arduia.currencystore.Store
import java.lang.Exception

abstract class ExpenseStore(rate: Rate<Int>) : Store<Float, Int>(rate) {

    protected var storeValue: Int = 0

    override fun getActual(): Float {
        validateRateOrError(rate)
        return storeValue.toFloat() / rate.getRate()
    }

    override fun getStore() = storeValue

    override fun setStore(value: Int) {
        this.storeValue = value
    }

}

private fun validateRateOrError(rate: Rate<Int>) {
    if (rate.getRate() <= 0) throw Exception("Invalid Rate Value (${rate.getRate()}). It should be greater than 0")
}