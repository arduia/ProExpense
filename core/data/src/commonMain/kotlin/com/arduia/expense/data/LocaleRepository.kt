package com.arduia.expense.data

/**
 * Storage-facing contract for the user's selected app language (BCP-47 tag, e.g. "en", "es").
 * Mirrors [CurrencySettingsRepository]'s shape — backed by the single `app_meta` row.
 */
interface LocaleRepository {
    suspend fun getLanguageTag(): Result<String>

    suspend fun setLanguageTag(languageTag: String): Result<Unit>
}
