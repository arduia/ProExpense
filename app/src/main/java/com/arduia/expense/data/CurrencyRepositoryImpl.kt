package com.arduia.expense.data

import com.arduia.expense.data.local.CacheDao
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDto

class CurrencyRepositoryImpl (private val dao: CurrencyDao, private val cache: CacheDao): CurrencyRepository{
    override suspend fun getCurrencies(): List<CurrencyDto> {
        return dao.getCurrencies()
    }

    override suspend fun getSelectedCurrency(): CurrencyDto {
        return cache.getSelectedCurrency()
    }
}