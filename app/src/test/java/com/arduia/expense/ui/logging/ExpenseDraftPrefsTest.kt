package com.arduia.expense.ui.logging

import androidx.test.core.app.ApplicationProvider
import com.arduia.expense.feature.logging.ui.ExpenseDraftPrefs
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ExpenseDraftPrefsTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun save_then_load_roundTripsDraftFields() {
        ExpenseDraftPrefs.clear(context)
        val draft =
            ExpenseEntryState(
                rawAmount = "12.50",
                selectedCategoryId = "coffee",
                note = "Latte",
                currencyCode = "EUR",
                recordedAtEpochMillis = 12345L,
            )

        ExpenseDraftPrefs.save(context, draft)
        val loaded = ExpenseDraftPrefs.load(context)

        assertEquals("12.50", loaded?.rawAmount)
        assertEquals("coffee", loaded?.selectedCategoryId)
        assertEquals("Latte", loaded?.note)
        assertEquals("EUR", loaded?.currencyCode)
        assertEquals(12345L, loaded?.recordedAtEpochMillis)
    }

    @Test
    fun save_then_load_roundTripsHomeCurrencyAndExchangeRate() {
        ExpenseDraftPrefs.clear(context)
        val draft =
            ExpenseEntryState(
                rawAmount = "45.00",
                currencyCode = "EUR",
                homeCurrencyCode = "USD",
                exchangeRateRaw = "1.08",
            )

        ExpenseDraftPrefs.save(context, draft)
        val loaded = ExpenseDraftPrefs.load(context)

        assertEquals("USD", loaded?.homeCurrencyCode)
        assertEquals("1.08", loaded?.exchangeRateRaw)
    }

    @Test
    fun load_fallsBackHomeCurrencyToDraftCurrencyForPreMultiCurrencyDrafts() {
        ExpenseDraftPrefs.clear(context)
        context
            .getSharedPreferences("expense_draft", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("raw_amount", "12.50")
            .putString("currency_code", "EUR")
            .apply()

        val loaded = ExpenseDraftPrefs.load(context)

        assertEquals("EUR", loaded?.homeCurrencyCode)
    }

    @Test
    fun load_returnsNullWhenNoDraftSaved() {
        ExpenseDraftPrefs.clear(context)

        assertNull(ExpenseDraftPrefs.load(context))
    }

    @Test
    fun save_withBlankAmountClearsAnyExistingDraft() {
        ExpenseDraftPrefs.save(context, ExpenseEntryState(rawAmount = "5.00"))

        ExpenseDraftPrefs.save(context, ExpenseEntryState(rawAmount = ""))

        assertNull(ExpenseDraftPrefs.load(context))
    }

    @Test
    fun clear_removesAPreviouslySavedDraft() {
        ExpenseDraftPrefs.save(context, ExpenseEntryState(rawAmount = "5.00"))

        ExpenseDraftPrefs.clear(context)

        assertNull(ExpenseDraftPrefs.load(context))
    }
}
