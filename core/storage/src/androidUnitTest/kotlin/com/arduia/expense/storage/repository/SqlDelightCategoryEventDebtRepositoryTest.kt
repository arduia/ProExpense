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
    fun category_reorder_persistsSortOrder() = runTest {
        val repo = SqlDelightCategoryRepository(inMemoryDatabase().categoryQueries, Dispatchers.Unconfined)
        repo.upsert(Category(CategoryId("cat-1"), "First"))
        repo.upsert(Category(CategoryId("cat-2"), "Second"))
        repo.upsert(Category(CategoryId("cat-3"), "Third"))

        repo.reorder(listOf(CategoryId("cat-3"), CategoryId("cat-1"), CategoryId("cat-2")))

        val all = repo.getAll()
        assertTrue(all is Result.Success)
        assertEquals(listOf("cat-3", "cat-1", "cat-2"), all.data.map { it.id.value })
    }

    @Test
    fun category_observeAll_emitsOrdered() = runTest {
        val repo = SqlDelightCategoryRepository(inMemoryDatabase().categoryQueries, Dispatchers.Unconfined)
        repo.upsert(Category(CategoryId("cat-1"), "First", sortOrder = 2))
        repo.upsert(Category(CategoryId("cat-2"), "Second", sortOrder = 0))
        repo.upsert(Category(CategoryId("cat-3"), "Third", sortOrder = 1))

        val all = repo.observeAll().first()
        assertEquals(listOf("cat-2", "cat-3", "cat-1"), all.map { it.id.value })
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

    @Test
    fun debt_observeAll_emitsUpsertedRowsOrderedByPerson() = runTest {
        val repo = SqlDelightDebtRepository(inMemoryDatabase().debtQueries, Dispatchers.Unconfined)
        repo.upsert(debt("d1", "Bob"))
        repo.upsert(debt("d2", "Alice"))

        val all = repo.observeAll().first()
        assertEquals(2, all.size)
        assertEquals(listOf("Alice", "Bob"), all.map { it.personName })
    }

    @Test
    fun event_observeAll_skipsRowWithUnmappableStatus_insteadOfThrowing() = runTest {
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
        database.eventQueries.insertEvent(
            id = "evt-bad",
            name = "Corrupt",
            start_epoch_millis = 0,
            end_epoch_millis = 0,
            budget_cents = 0,
            currency_code = "USD",
            status = 99L,
            created_at = 0,
            cached_spent_cents = 0,
            cache_updated_at = 0,
            closed_at_epoch_millis = null,
        )

        val all = repo.observeAll().first()
        assertEquals(listOf("evt-1"), all.map { it.id.value })
    }

    @Test
    fun debt_observeAll_skipsRowWithUnmappableDirection_insteadOfThrowing() = runTest {
        val database = inMemoryDatabase()
        val repo = SqlDelightDebtRepository(database.debtQueries, Dispatchers.Unconfined)
        repo.upsert(debt("d1", "Alice"))
        database.debtQueries.insertDebt(
            id = "d-bad",
            person_name = "Corrupt",
            amount_cents = 100,
            currency_code = "USD",
            direction = 99L,
            due_epoch_millis = null,
            is_settled = 0L,
            note = null,
        )

        val all = repo.observeAll().first()
        assertEquals(listOf("d1"), all.map { it.id.value })
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
