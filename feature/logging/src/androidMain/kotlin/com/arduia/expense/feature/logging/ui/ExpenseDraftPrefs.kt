package com.arduia.expense.feature.logging.ui

import android.content.Context
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState

/**
 * Persists an in-progress, unsaved expense entry so it survives process death or an accidental
 * app close. Deliberately excludes tag linking (event/debt) — a resumed draft always re-lands on
 * the Amount step with no tag pre-selected, which is an acceptable simplification since re-tagging
 * takes one tap.
 */
object ExpenseDraftPrefs {
    private const val PREFS_NAME = "expense_draft"
    private const val KEY_RAW_AMOUNT = "raw_amount"
    private const val KEY_CATEGORY_ID = "category_id"
    private const val KEY_NOTE = "note"
    private const val KEY_CURRENCY_CODE = "currency_code"
    private const val KEY_HOME_CURRENCY_CODE = "home_currency_code"
    private const val KEY_EXCHANGE_RATE_RAW = "exchange_rate_raw"
    private const val KEY_RECORDED_AT = "recorded_at"

    fun save(
        context: Context,
        state: ExpenseEntryState,
    ) {
        if (state.rawAmount.isBlank()) {
            clear(context)
            return
        }
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_RAW_AMOUNT, state.rawAmount)
            .putString(KEY_CATEGORY_ID, state.selectedCategoryId)
            .putString(KEY_NOTE, state.note)
            .putString(KEY_CURRENCY_CODE, state.currencyCode)
            .putString(KEY_HOME_CURRENCY_CODE, state.homeCurrencyCode)
            .putString(KEY_EXCHANGE_RATE_RAW, state.exchangeRateRaw)
            .putLong(KEY_RECORDED_AT, state.recordedAtEpochMillis)
            .apply()
    }

    fun load(context: Context): ExpenseEntryState? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawAmount = prefs.getString(KEY_RAW_AMOUNT, null)
        if (rawAmount.isNullOrBlank()) return null
        val currencyCode = prefs.getString(KEY_CURRENCY_CODE, "USD") ?: "USD"
        return ExpenseEntryState(
            rawAmount = rawAmount,
            selectedCategoryId = prefs.getString(KEY_CATEGORY_ID, "food") ?: "food",
            note = prefs.getString(KEY_NOTE, "") ?: "",
            currencyCode = currencyCode,
            // Falls back to the draft's own currency (not a hardcoded "USD") so a foreign-currency
            // draft restores as same-currency rather than silently becoming "foreign" against the
            // wrong home currency and corrupting homeCurrencyMoney on save.
            homeCurrencyCode = prefs.getString(KEY_HOME_CURRENCY_CODE, currencyCode) ?: currencyCode,
            exchangeRateRaw = prefs.getString(KEY_EXCHANGE_RATE_RAW, "1") ?: "1",
            recordedAtEpochMillis = prefs.getLong(KEY_RECORDED_AT, System.currentTimeMillis()),
        )
    }

    fun clear(context: Context) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
