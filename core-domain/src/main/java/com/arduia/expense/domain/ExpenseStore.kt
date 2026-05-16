package com.arduia.expense.domain

import com.arduia.currencystore.Rate
import com.arduia.currencystore.Store
import timber.log.Timber
import java.lang.Exception
import java.math.BigDecimal

abstract class ExpenseStore(rate: Rate<Long>) : Store<BigDecimal, Long>(rate) {

    protected var storeValue:Long = 0

    override fun getActual(): BigDecimal {
        validateRateOrError(rate)
        return BigDecimal(storeValue.toDouble()/ rate.getRate())
    }

    override fun getStore() = storeValue

    override fun setStore(value: Long) {
        this.storeValue = value
    }

}

private fun validateRateOrError(rate: Rate<Long>) {
    if (rate.getRate() <= 0) throw Exception("Invalid Rate Value (${rate.getRate()}). It should be greater than 0")
}