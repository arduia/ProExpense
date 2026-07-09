package com.arduia.expense.feature.currency

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode

/**
 * Thin composition over the [CurrencySettingsRepository] contract (design §3.1). Falls back to a
 * default home currency when none has been chosen yet (before onboarding).
 */
class DefaultCurrencyRepository(
    private val settings: CurrencySettingsRepository,
    private val defaultHomeCurrency: CurrencyCode = CurrencyCode("USD"),
) : CurrencyRepository {
    override suspend fun getSettings(): Result<CurrencySettings> =
        when (val result = settings.getHomeCurrency()) {
            is Result.Success -> Result.Success(CurrencySettings(result.data ?: defaultHomeCurrency))
            is Result.Error -> result
        }

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = settings.setHomeCurrency(currency)
}
