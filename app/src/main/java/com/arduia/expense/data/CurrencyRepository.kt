package com.arduia.expense.data

import com.arduia.expense.data.local.CurrencyDto

interface CurrencyRepository {
    suspend fun getCurrencies(): List<CurrencyDto>

    suspend fun getSelectedCacheCurrency(): CurrencyDto

    suspend fun setSelectedCacheCurrency(num: String)
}