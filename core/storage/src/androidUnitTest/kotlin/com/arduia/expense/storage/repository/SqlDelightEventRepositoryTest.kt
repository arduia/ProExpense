package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightEventRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightEventRepository(database, UnconfinedTestDispatcher())

    private fun event(
        id: String,
        start: Long,
        status: EventStatus = EventStatus.ACTIVE,
    ) = Event(
        id = id,
        name = "Trip",
        startEpochMillis = start,
        endEpochMillis = start + 1_000,
        budgetAmount = Amount(50_000),
        status = status,
    )

    // Rule: a saved event reads back field-for-field, including its status enum.
    @Test
    fun upsert_then_getById_roundTrips() = runTest {
        val saved = event(id = "e1", start = 100, status = EventStatus.CLOSED)
        repository.upsert(saved).getOrFail()

        assertEquals(saved, repository.getById("e1").getOrFail())
    }

    // Rule: getAll is ordered by start_epoch_millis descending.
    @Test
    fun getAll_ordersByStartDescending() = runTest {
        repository.upsert(event(id = "old", start = 100)).getOrFail()
        repository.upsert(event(id = "new", start = 300)).getOrFail()

        assertEquals(listOf("new", "old"), repository.getAll().getOrFail().map { it.id })
    }

    // Rule: delete removes the targeted event.
    @Test
    fun delete_removesEvent() = runTest {
        repository.upsert(event(id = "e1", start = 100)).getOrFail()
        repository.delete("e1").getOrFail()

        assertNull(repository.getById("e1").getOrFail())
    }
}
