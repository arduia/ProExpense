package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.storage.EntityDataStore

class InMemoryCurrencyRepository(
    private val store: EntityDataStore,
) : CurrencyRepository {
    override suspend fun getSettings(): Result<CurrencySettings> =
        Result.Success(CurrencySettings(homeCurrency = CurrencyCode(store.homeCurrencyCode)))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
        store.homeCurrencyCode = currency.code
        store.persistSettings()
        return Result.Success(Unit)
    }
}
