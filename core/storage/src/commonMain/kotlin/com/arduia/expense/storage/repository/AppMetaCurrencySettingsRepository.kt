package com.arduia.expense.storage.repository

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.storage.catchingResult

class AppMetaCurrencySettingsRepository(
    private val store: AppMetaLocalStore,
) : CurrencySettingsRepository {
    override suspend fun getHomeCurrency(): Result<CurrencyCode?> =
        catchingResult {
            CurrencyCode(store.read().homeCurrencyCode)
        }

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> =
        catchingResult {
            store.update { it.copy(homeCurrencyCode = currency.code) }
            Unit
        }
}
