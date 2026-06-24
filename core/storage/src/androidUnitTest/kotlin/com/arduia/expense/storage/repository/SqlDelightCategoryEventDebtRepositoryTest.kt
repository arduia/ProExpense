package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightCategoryEventDebtRepositoryTest {

    private val home = CurrencyCode("USD")

    @Test
    fun category_upsertGetDelete() = runTest {
        val repo = SqlDelightCategoryRepository(inMemoryDatabase().categoryQueries, Dispatchers.Unconfined)
        repo.upsert(Category(CategoryId("custom-1"), "Pets", isCustom = true))

        val all = repo.getAll()
        assertTrue(all is Result.Success)
        assertEquals(1, all.data.size)
        assertEquals("Pets", all.data.single().name)
        assertTrue(all.data.single().isCustom)

        repo.delete(CategoryId("custom-1"))
        val afterDelete = repo.getAll()
        assertTrue(afterDelete is Result.Success)
        assertTrue(afterDelete.data.isEmpty())
    }

    @Test
    fun event_roundTripsBudgetInHomeCurrency() = runTest {
        val repo = SqlDelightEventRepository(inMemoryDatabase().eventQueries, Dispatchers.Unconfined)
        val event = Event(
            id = EventId("evt-1"),
            name = "Trip",
            startEpochMillis = 1,
            endEpochMillis = 100,
            budget = Money(Amount(200_00), home),
            status = EventStatus.ACTIVE,
        )
        repo.upsert(event)

        val fetched = repo.getById(EventId("evt-1"))
        assertTrue(fetched is Result.Success)
        assertEquals(event, fetched.data)
        assertEquals(event, repo.observeAll().first().single())
    }

    @Test
    fun event_upsert_preservesCacheAndCreatedAt_acrossEdit() = runTest {
        val database = inMemoryDatabase()
        val repo = SqlDelightEventRepository(database.eventQueries, Dispatchers.Unconfined)
        val event = Event(
            id = EventId("evt-1"),
            name = "Trip",
            startEpochMillis = 1,
            endEpochMillis = 100,
            budget = Money(Amount(200_00), home),
            status = EventStatus.ACTIVE,
        )
        repo.upsert(event)
        database.eventQueries.updateEventCache(5_00, 42, "evt-1")
        val createdAtAfterFirstWrite = database.eventQueries.selectEventById("evt-1").executeAsOne().created_at

        repo.upsert(event.copy(name = "Trip renamed"))

        val row = database.eventQueries.selectEventById("evt-1").executeAsOne()
        assertEquals(500, row.cached_spent_cents)
        assertEquals(42, row.cache_updated_at)
        assertEquals(createdAtAfterFirstWrite, row.created_at)
    }

    @Test
    fun getSpent_returnsCachedTotalInHomeCurrency() = runTest {
        val database = inMemoryDatabase()
        val repo = SqlDelightEventRepository(database.eventQueries, Dispatchers.Unconfined)
        repo.upsert(
            Event(
                id = EventId("evt-1"),
                name = "Trip",
                startEpochMillis = 1,
                endEpochMillis = 100,
                budget = Money(Amount(200_00), home),
                status = EventStatus.ACTIVE,
            ),
        )
        database.eventQueries.updateEventCache(15_00, 42, "evt-1")

        val spent = repo.getSpent(EventId("evt-1"))
        assertTrue(spent is Result.Success)
        assertEquals(Money(Amount(15_00), home), spent.data)
    }

    @Test
    fun getSpent_errorWhenEventMissing() = runTest {
        val repo = SqlDelightEventRepository(inMemoryDatabase().eventQueries, Dispatchers.Unconfined)
        assertTrue(repo.getSpent(EventId("nope")) is Result.Error)
    }

    @Test
    fun debt_findByPersonName_returnsMatchingRows() = runTest {
        val repo = SqlDelightDebtRepository(inMemoryDatabase().debtQueries, Dispatchers.Unconfined)
        repo.upsert(debt("d1", "Alice"))
        repo.upsert(debt("d2", "Bob"))

        val alice = repo.findByPersonName("Alice")
        assertTrue(alice is Result.Success)
        assertEquals(listOf("d1"), alice.data.map { it.id.value })
    }

    private fun debt(id: String, person: String) = Debt(
        id = DebtId(id),
        personName = person,
        money = Money(Amount(1_000), home),
        direction = DebtDirection.I_OWE,
        dueEpochMillis = null,
        isSettled = false,
    )
}
