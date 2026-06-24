package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightFinanceRecordRepositoryTest {

    private fun repository() =
        SqlDelightFinanceRecordRepository(inMemoryDatabase().financeRecordQueries, Dispatchers.Unconfined)

    private fun record(id: String, link: RecordLink = RecordLink.None) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(5_000), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(5_000), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        type = RecordType.EXPENSE,
        note = "note-$id",
        recordedAtEpochMillis = 1_000,
        link = link,
    )

    @Test
    fun upsert_thenGetById_returnsStoredRecord() = runTest {
        val repo = repository()
        val record = record("rec-1", RecordLink.ToEvent(EventId("evt-1")))

        assertTrue(repo.upsert(record) is Result.Success)

        val fetched = repo.getById(RecordId("rec-1"))
        assertTrue(fetched is Result.Success)
        assertEquals(record, fetched.data)
    }

    @Test
    fun upsert_replacesExistingRecordWithSameId() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1"))
        repo.upsert(record("rec-1").copy(note = "updated"))

        val all = repo.getAll()
        assertTrue(all is Result.Success)
        assertEquals(1, all.data.size)
        assertEquals("updated", all.data.single().note)
    }

    @Test
    fun delete_removesRecord() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1"))

        assertTrue(repo.delete(RecordId("rec-1")) is Result.Success)

        val fetched = repo.getById(RecordId("rec-1"))
        assertTrue(fetched is Result.Success)
        assertNull(fetched.data)
    }

    @Test
    fun observeAll_reflectsCurrentRows() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1"))
        repo.upsert(record("rec-2"))

        val observed = repo.observeAll().first()
        assertEquals(setOf("rec-1", "rec-2"), observed.map { it.id.value }.toSet())
    }
}
