package com.arduia.expense.data.local

interface CacheDao {

    suspend fun getSelectedCurrency(): CurrencyDto

    suspend fun setSelectedCurrency(currency: CurrencyDto)

}