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
        val draft = ExpenseEntryState(
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
