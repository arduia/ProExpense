package com.arduia.expense.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull

object CacheDaoImpl : CacheDao{

    private val currencyFlow = MutableStateFlow<CurrencyDto?>(null)

    override fun getSelectedCurrency(): Flow<CurrencyDto> {
        return currencyFlow.asStateFlow().filterNotNull()
    }

    override suspend fun setSelectedCurrency(currency: CurrencyDto) {
       currencyFlow.value = currency
    }
}