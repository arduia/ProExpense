package com.arduia.expense.data.local

import kotlinx.coroutines.flow.Flow

interface CacheDao {

    fun getSelectedCurrency(): Flow<CurrencyDto>

    suspend fun setSelectedCurrency(currency: CurrencyDto)

}