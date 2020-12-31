package com.arduia.currencystore

/**
 * Expense Store Conversions for every Amount
 * to improve performance in clculations
 */
interface Rate<I: Number> {
    fun getRate(): I
}