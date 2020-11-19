package com.arduia.expense.data.local

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

object CacheDaoImpl : CacheDao{

    private val currencyCH = ConflatedBroadcastChannel<CurrencyDto>()

    override fun getSelectedCurrency(): Flow<CurrencyDto> {
        return currencyCH.asFlow()
    }

    override suspend fun setSelectedCurrency(currency: CurrencyDto) {
       currencyCH.send(currency)
    }
}