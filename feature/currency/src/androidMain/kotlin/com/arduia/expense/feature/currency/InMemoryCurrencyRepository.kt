package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.storage.InMemoryDataStore

class InMemoryCurrencyRepository(
    private val store: InMemoryDataStore,
) : CurrencyRepository {
    override suspend fun getSettings(): Result<CurrencySettings> =
        Result.Success(CurrencySettings(homeCurrency = CurrencyCode(store.homeCurrencyCode)))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
        store.homeCurrencyCode = currency.code
        return Result.Success(Unit)
    }
}
