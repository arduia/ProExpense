package com.arduia.currencystore

/**
 * Expense Store Conversions for every Amount
 * to improve performance in calculations
 */
interface Rate<I: Number> {
    fun getRate(): I
}