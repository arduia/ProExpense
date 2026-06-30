package com.arduia.expense.data

import com.arduia.expense.domain.CurrencyCode

/**
 * Storage-facing contract for the user's home currency (backed by the single `app_meta` row). The
 * higher-level `feature:currency` repository composes this — features never touch `core:storage`.
 */
interface CurrencySettingsRepository {
    suspend fun getHomeCurrency(): Result<CurrencyCode?>

    suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit>
}
