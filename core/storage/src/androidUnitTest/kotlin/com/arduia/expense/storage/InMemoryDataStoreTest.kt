/**
 * Domain rules (persistence):
 * - Entity round-trip preserves amount cap, currency, and tag metadata.
 * - clearAll wipes entities and security settings.
 */
package com.arduia.expense.storage

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryDataStoreTest {
    private val store = InMemoryDataStore()

    @Test
    fun insertRecord_roundTripsAtMaxAmount() = runTest {
        val record = FinanceRecord(
            id = "max",
            amount = Amount(Amount.MAX_VALUE_IN_CENTS),
            currency = CurrencyCode("EUR"),
            homeCurrencyAmount = Amount(Amount.MAX_VALUE_IN_CENTS),
            categoryId = "food",
            type = RecordType.EXPENSE,
            note = "cap",
            recordedAtEpochMillis = 1L,
            tagType = ExpenseTagType.EVENT,
            tagId = "event_1",
        )
        store.insertRecord(record)
        val loaded = store.allRecords().single()
        assertEquals(Amount.MAX_VALUE_IN_CENTS, loaded.homeCurrencyAmount.valueInCents)
        assertEquals("EUR", loaded.currency.code)
        assertEquals(ExpenseTagType.EVENT, loaded.tagType)
    }

    @Test
    fun clearAll_resetsSecurityAndEntities() = runTest {
        store.securityQuestionId = "pet"
        store.securityAnswerHash = "hash"
        store.biometricEnrolled = true
        store.insertRecord(
            FinanceRecord(
                id = "r1",
                amount = Amount(100),
                currency = CurrencyCode("USD"),
                homeCurrencyAmount = Amount(100),
                categoryId = "food",
                type = RecordType.EXPENSE,
                note = null,
                recordedAtEpochMillis = 1L,
            ),
        )
        store.clearAll()
        assertTrue(store.allRecords().isEmpty())
        assertNull(store.securityQuestionId)
        assertNull(store.securityAnswerHash)
        assertEquals(false, store.biometricEnrolled)
    }
}
