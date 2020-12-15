package com.arduia.expense.domain

import com.arduia.currencystore.Rate

object DataStoreExchangeRate: Rate<Int> {
    override fun getRate(): Int = 100
}