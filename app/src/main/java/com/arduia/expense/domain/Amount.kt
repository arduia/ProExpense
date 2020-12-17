package com.arduia.expense.domain

import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.roundToInt

class Amount: ExpenseStore(DataStoreExchangeRate){

    override fun toString(): String {
        return "Amount(storeValue = $storeValue)"
    }

    companion object{
        fun createFromActual(actual: BigDecimal) = Amount().apply {

            val storeValue = actual.multiply(BigDecimal(rate.getRate())).apply {
                setScale(0, RoundingMode.FLOOR)
            }
           setStore( storeValue.longValueExact())
        }

        fun createFromStore(store: Long) = Amount().apply {
            setStore(store)
        }
    }

}

operator fun Amount.times(multiplier: Amount): Amount{
    val left = this.getStore()
    val right = multiplier.getStore()
    val result = left * right
    this.setStore(result)
    return this
}

operator fun Amount.times(time: Number): Amount{
    val left = this.getStore()
    val result = (left * time.toInt())
    this.setStore(result)
    return this
}

operator fun Amount.plus(amount: Amount): Amount{
    val result = this.getStore() + amount.getStore()
    setStore(result)
    return this
}