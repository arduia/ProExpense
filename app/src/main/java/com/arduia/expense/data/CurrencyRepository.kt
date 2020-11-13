package com.arduia.expense.data

import com.arduia.expense.data.local.CurrencyDto

interface CurrencyRepository {
    suspend fun getCurrencies(): List<CurrencyDto>

    suspend fun getSelectedCurrency(): CurrencyDto
}