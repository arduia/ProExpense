package com.arduia.expense.data.local

interface CurrencyDao{
    suspend fun getCurrencies(): List<CurrencyDto>
}