package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightFinanceRecordRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightFinanceRecordRepository(database, UnconfinedTestDispatcher())

    private fun record(
        id: String,
        recordedAt: Long,
        tagType: ExpenseTagType? = null,
        tagId: String? = null,
        note: String? = "lunch",
    ) = FinanceRecord(
        id = id,
        amount = Amount(1_250),
        currency = CurrencyCode("USD"),
        homeCurrencyAmount = Amount(1_250),
        categoryId = "cat_food",
        type = RecordType.EXPENSE,
        note = note,
        recordedAtEpochMillis = recordedAt,
        tagType = tagType,
        tagId = tagId,
    )

    // Rule: a saved record reads back field-for-field (the core persistence contract).
    @Test
    fun upsert_then_getById_roundTrips() = runTest {
        val saved = record(id = "r1", recordedAt = 100)
        repository.upsert(saved).getOrFail()

        assertEquals(saved, repository.getById("r1").getOrFail())
    }

    // Rule: polymorphic @tag (tagType + tagId) round-trips, including the null case.
    @Test
    fun upsert_preservesTagAndNullableFields() = runTest {
        val tagged = record(id = "r1", recordedAt = 100, tagType = ExpenseTagType.EVENT, tagId = "e1", note = null)
        repository.upsert(tagged).getOrFail()

        val read = repository.getById("r1").getOrFail()!!
        assertEquals(ExpenseTagType.EVENT, read.tagType)
        assertEquals("e1", read.tagId)
        assertNull(read.note)
    }

    // Rule: getAll is ordered newest-first by recorded_at.
    @Test
    fun getAll_ordersByRecordedAtDescending() = runTest {
        repository.upsert(record(id = "old", recordedAt = 100)).getOrFail()
        repository.upsert(record(id = "new", recordedAt = 300)).getOrFail()
        repository.upsert(record(id = "mid", recordedAt = 200)).getOrFail()

        assertEquals(listOf("new", "mid", "old"), repository.getAll().getOrFail().map { it.id })
    }

    // Rule: upsert on an existing id replaces rather than duplicating (INSERT OR REPLACE).
    @Test
    fun upsert_sameId_replaces() = runTest {
        repository.upsert(record(id = "r1", recordedAt = 100, note = "first")).getOrFail()
        repository.upsert(record(id = "r1", recordedAt = 100, note = "second")).getOrFail()

        val all = repository.getAll().getOrFail()
        assertEquals(1, all.size)
        assertEquals("second", all.single().note)
    }

    // Rule: delete removes only the targeted record.
    @Test
    fun delete_removesRecord() = runTest {
        repository.upsert(record(id = "r1", recordedAt = 100)).getOrFail()
        repository.upsert(record(id = "r2", recordedAt = 200)).getOrFail()

        repository.delete("r1").getOrFail()

        assertEquals(listOf("r2"), repository.getAll().getOrFail().map { it.id })
    }

    // Rule: a missing id is a successful empty read, not an error.
    @Test
    fun getById_missing_returnsNullSuccess() = runTest {
        assertNull(repository.getById("nope").getOrFail())
    }

    // Rule: getAll on an empty table succeeds with an empty list.
    @Test
    fun getAll_empty_returnsEmptyList() = runTest {
        assertTrue(repository.getAll().getOrFail().isEmpty())
    }
}
