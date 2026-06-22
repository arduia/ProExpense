package com.arduia.expense.ui.logging

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpenseEntryViewModelTest {

    private val finance = FakeFinanceRecordRepository()
    private val profile = FakeProfileRepository(name = "Maya", currency = CurrencyCode("EUR"))
    private val vm = ExpenseEntryViewModel(
        financeRepository = finance,
        profileRepository = profile,
        clock = { 1_000L },
        idGenerator = { "generated-id" },
    )

    private fun handoff(
        id: String? = null,
        raw: String = "12.50",
        category: String = "food",
        note: String = "Lunch",
        recordedAt: Long? = null,
    ) = LoggedExpenseHandoff(
        id = id,
        categoryId = category,
        note = note,
        rawAmount = raw,
        currencyCode = "EUR",
        recordedAtEpochMillis = recordedAt,
        timeLabel = "12:30 PM",
    )

    // Rule: a new entry start carries the home currency and no id (create mode).
    @Test
    fun newEntryStart_usesHomeCurrency() = runTest {
        val start = vm.newEntryStart()
        assertNull(start.id)
        assertEquals("EUR", start.currencyCode)
        assertEquals("food", start.categoryId)
    }

    // Rule: submitting with no id creates a record with a generated id, amount in cents, now as date.
    @Test
    fun submit_create_persistsNewRecord() = runTest {
        vm.submit(handoff(raw = "12.50")).let { }

        val saved = finance.store.values.single()
        assertEquals("generated-id", saved.id)
        assertEquals(Amount(1250), saved.amount)
        assertEquals(Amount(1250), saved.homeCurrencyAmount)
        assertEquals(CurrencyCode("EUR"), saved.currency)
        assertEquals(RecordType.EXPENSE, saved.type)
        assertEquals("Lunch", saved.note)
        assertEquals(1_000L, saved.recordedAtEpochMillis)
    }

    // Rule: a blank note persists as null (not an empty string).
    @Test
    fun submit_blankNote_storesNull() = runTest {
        vm.submit(handoff(note = "   "))
        assertNull(finance.store.values.single().note)
    }

    // Rule: submitting with an existing id updates that record in place (no duplicate).
    @Test
    fun submit_update_replacesExisting() = runTest {
        val existing = FinanceRecord(
            id = "e1",
            amount = Amount(500),
            currency = CurrencyCode("EUR"),
            homeCurrencyAmount = Amount(500),
            categoryId = "food",
            type = RecordType.EXPENSE,
            note = "old",
            recordedAtEpochMillis = 42L,
            tagType = null,
            tagId = null,
        )
        finance.upsert(existing)

        vm.submit(handoff(id = "e1", raw = "9.99", note = "new", recordedAt = 42L))

        assertEquals(1, finance.store.size)
        val updated = finance.store.getValue("e1")
        assertEquals(Amount(999), updated.amount)
        assertEquals("new", updated.note)
        assertEquals(42L, updated.recordedAtEpochMillis) // preserved, not reset to clock
    }

    // Rule: loadForEdit reconstructs an editable handoff including amount string and tag.
    @Test
    fun loadForEdit_roundTripsRecord() = runTest {
        finance.upsert(
            FinanceRecord(
                id = "e1",
                amount = Amount(1599),
                currency = CurrencyCode("EUR"),
                homeCurrencyAmount = Amount(1599),
                categoryId = "transport",
                type = RecordType.EXPENSE,
                note = "Taxi",
                recordedAtEpochMillis = 5_000L,
                tagType = ExpenseTagType.EVENT,
                tagId = "bali",
            ),
        )

        val loaded = vm.loadForEdit("e1")!!
        assertEquals("e1", loaded.id)
        assertEquals("15.99", loaded.rawAmount)
        assertEquals("transport", loaded.categoryId)
        assertEquals("Taxi", loaded.note)
        assertEquals(ExpenseTagType.EVENT, loaded.tagType)
        assertEquals("bali", loaded.tagId)
        assertEquals(5_000L, loaded.recordedAtEpochMillis)
    }

    // Rule: editing a missing record yields null (no crash).
    @Test
    fun loadForEdit_missing_returnsNull() = runTest {
        assertNull(vm.loadForEdit("nope"))
    }
}
