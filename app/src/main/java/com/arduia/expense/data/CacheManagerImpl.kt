package com.arduia.expense.data

import com.arduia.expense.data.local.CacheDao
import kotlinx.coroutines.flow.first

class CacheManagerImpl(
    private val settingRepo: SettingsRepository,
    private val currencyRepository: CurrencyRepository,
    private val cacheDao: CacheDao
): CacheManager {
    override suspend fun updateCurrencyCache() {
        val num = settingRepo.getSelectedCurrencyNumber().first()
        val currency = currencyRepository.getCurrencies().find { it.number == num }
            ?:throw Exception("currency num ($num) no found!")
        cacheDao.setSelectedCurrency(currency)
    }
}