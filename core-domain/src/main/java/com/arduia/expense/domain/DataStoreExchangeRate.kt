package com.arduia.expense.domain

import com.arduia.currencystore.Rate

object DataStoreExchangeRate: Rate<Long> {
    override fun getRate(): Long = 100L
}