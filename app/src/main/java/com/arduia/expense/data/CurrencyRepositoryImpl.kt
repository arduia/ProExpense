package com.arduia.expense.data

import com.arduia.expense.data.local.CacheDao
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDto

class CurrencyRepositoryImpl(
    private val dao: CurrencyDao,
    private val cache: CacheDao
) : CurrencyRepository {

    private var getCurrencyRequestCount = 0
    private val repoCacheCurrencies = mutableListOf<CurrencyDto>()

    override suspend fun getCurrencies(): List<CurrencyDto> {
        getCurrencyRequestCount++
        if (getCurrencyRequestCount > 3) {
            if(repoCacheCurrencies.isEmpty()){
                repoCacheCurrencies.addAll(dao.getCurrencies())
            }
            return repoCacheCurrencies
        }
        return dao.getCurrencies()
    }

    override suspend fun getSelectedCacheCurrency(): CurrencyDto {
        return cache.getSelectedCurrency()
    }

    override suspend fun setSelectedCacheCurrency(num: String) {
        val currency = getCurrencies().find { it.number == num }
            ?: throw Exception("Wrong currency number $num. no exist")
        cache.setSelectedCurrency(currency)
    }
}