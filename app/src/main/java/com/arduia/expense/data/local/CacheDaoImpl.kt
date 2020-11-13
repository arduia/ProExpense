package com.arduia.expense.data.local

import java.lang.IllegalStateException

object CacheDaoImpl : CacheDao{

    private var selectedCurrency: CurrencyDto? = null

    override suspend fun getSelectedCurrency(): CurrencyDto {
        return selectedCurrency?: throw IllegalStateException("selected Currency doesn't exist!")
    }

    override suspend fun setSelectedCurrency(currency: CurrencyDto) {
        selectedCurrency = currency
    }
}