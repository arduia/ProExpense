package com.arduia.expense.domain

import timber.log.Timber
import kotlin.math.roundToInt

class Amount: ExpenseStore(DataStoreExchangeRate){

    override fun toString(): String {
        return "Amount(storeValue = $storeValue)"
    }
    companion object{
        fun createFromActual(actual: Float) = Amount().apply {
            val storeValue = (actual * rate.getRate())
            setStore(storeValue.roundToInt())
        }

        fun createFromStore(store: Int) = Amount().apply {
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