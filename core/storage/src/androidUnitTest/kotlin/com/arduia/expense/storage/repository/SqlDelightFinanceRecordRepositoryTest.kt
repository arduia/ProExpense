package com.arduia.expense.storage.repository

import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
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
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightFinanceRecordRepositoryTest {

    private fun repository(database: ProExpenseDatabase = inMemoryDatabase()) =
        SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )

    private fun record(
        id: String,
        link: RecordLink = RecordLink.None,
        amountCents: Long = 5_000,
        recordedAtEpochMillis: Long = 1_000,
        categoryId: String = "food",
        note: String? = "note-$id",
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(amountCents), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(amountCents), CurrencyCode("USD")),
        categoryId = CategoryId(categoryId),
        type = RecordType.EXPENSE,
        note = note,
        recordedAtEpochMillis = recordedAtEpochMillis,
        link = link,
    )

    private fun seedEvent(database: ProExpenseDatabase, id: String) {
        database.eventQueries.insertEvent(
            id = id,
            name = "Trip",
            start_epoch_millis = 0,
            end_epoch_millis = 100,
            budget_cents = 100_000,
            currency_code = "USD",
            status = 0L,
            created_at = 0,
            cached_spent_cents = 0,
            cache_updated_at = 0,
            closed_at_epoch_millis = null,
        )
    }

    @Test
    fun upsert_thenGetById_returnsStoredRecord_withStampedIntegrity() = runTest {
        val repo = repository()
        val record = record("rec-1", RecordLink.ToEvent(EventId("evt-1")))

        assertTrue(repo.upsert(record) is Result.Success)

        val fetched = repo.getById(RecordId("rec-1"))
        assertTrue(fetched is Result.Success)
        // Content matches; the repo stamps an integrity checksum on write.
        assertEquals(record, fetched.data!!.copy(integrity = null))
        assertEquals("SHA-256", fetched.data!!.integrity!!.algorithm)
    }

    @Test
    fun verifyIntegrity_trueForStoredRecord() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1"))

        val verified = repo.verifyIntegrity(RecordId("rec-1"))
        assertTrue(verified is Result.Success)
        assertTrue(verified.data)
    }

    @Test
    fun verifyIntegrity_falseWhenStoredContentTamperedOutsideRepo() = runTest {
        val database = inMemoryDatabase()
        val queries = database.financeRecordQueries
        val repo = repository(database)
        repo.upsert(record("rec-1"))

        // Simulate tampering: rewrite the amount but keep the old checksum columns.
        val stored = queries.selectRecordById("rec-1").executeAsOneOrNull()!!
        queries.insertRecord(
            id = stored.id,
            amount_cents = stored.amount_cents + 1,
            currency_code = stored.currency_code,
            home_amount_cents = stored.home_amount_cents,
            category_id = stored.category_id,
            type = stored.type,
            note = stored.note,
            recorded_at = stored.recorded_at,
            updated_at = stored.updated_at,
            tag_type = stored.tag_type,
            tag_id = stored.tag_id,
            integrity_algo = stored.integrity_algo,
            integrity_hash = stored.integrity_hash,
            home_currency_code = stored.home_currency_code,
        )

        val verified = repo.verifyIntegrity(RecordId("rec-1"))
        assertTrue(verified is Result.Success)
        assertFalse(verified.data)
    }

    @Test
    fun verifyIntegrity_errorWhenMissing() = runTest {
        val repo = repository()
        assertTrue(repo.verifyIntegrity(RecordId("nope")) is Result.Error)
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

    @Test
    fun observeAll_skipsRowWithUnmappableTagType_insteadOfThrowing() = runTest {
        val database = inMemoryDatabase()
        val repo = repository(database)
        repo.upsert(record("rec-1"))
        // A row a mapper can't decode (e.g. left behind by a future-version downgrade) must not
        // take down the whole flow for every other, perfectly valid row.
        database.financeRecordQueries.insertRecord(
            id = "rec-bad",
            amount_cents = 100,
            currency_code = "USD",
            home_amount_cents = null,
            category_id = "food",
            type = 0L,
            note = null,
            recorded_at = 1_000,
            updated_at = 1_000,
            tag_type = "BOGUS",
            tag_id = "x",
            integrity_algo = "SHA-256",
            integrity_hash = "dummy",
            home_currency_code = null,
        )

        val observed = repo.observeAll().first()
        assertEquals(listOf("rec-1"), observed.map { it.id.value })
    }

    @Test
    fun upsert_linkedToEvent_recomputesCachedSpent() = runTest {
        val database = inMemoryDatabase()
        seedEvent(database, "evt-1")
        val repo = repository(database)

        repo.upsert(record("rec-1", RecordLink.ToEvent(EventId("evt-1")), amountCents = 1_000))
        repo.upsert(record("rec-2", RecordLink.ToEvent(EventId("evt-1")), amountCents = 2_000))

        val event = database.eventQueries.selectEventById("evt-1").executeAsOne()
        assertEquals(3_000, event.cached_spent_cents)
    }

    @Test
    fun upsert_foreignCurrencyRecord_roundTripsHomeCurrencyAndAmount() = runTest {
        val database = inMemoryDatabase()
        val repo = repository(database)
        val foreign = FinanceRecord(
            id = RecordId("rec-eur"),
            money = Money(Amount(4_500), CurrencyCode("EUR")),
            homeCurrencyMoney = Money(Amount(4_860), CurrencyCode("USD")),
            categoryId = CategoryId("food"),
            type = RecordType.EXPENSE,
            note = null,
            recordedAtEpochMillis = 1_000,
        )

        repo.upsert(foreign)

        val row = database.financeRecordQueries.selectRecordById("rec-eur").executeAsOne()
        assertEquals("EUR", row.currency_code)
        assertEquals(4_500, row.amount_cents)
        assertEquals("USD", row.home_currency_code)
        assertEquals(4_860, row.home_amount_cents)

        val reloaded = (repo.getById(RecordId("rec-eur")) as Result.Success).data!!
        assertEquals(CurrencyCode("EUR"), reloaded.money.currency)
        assertEquals(CurrencyCode("USD"), reloaded.homeCurrencyMoney.currency)
        assertEquals(4_860, reloaded.homeCurrencyMoney.amount.valueInCents)
    }

    @Test
    fun upsert_linkedToEvent_withForeignCurrency_cachesHomeCurrencyAmount() = runTest {
        val database = inMemoryDatabase()
        seedEvent(database, "evt-1")
        val repo = repository(database)
        val foreign = FinanceRecord(
            id = RecordId("rec-eur"),
            money = Money(Amount(1_000), CurrencyCode("EUR")),
            homeCurrencyMoney = Money(Amount(1_080), CurrencyCode("USD")),
            categoryId = CategoryId("food"),
            type = RecordType.EXPENSE,
            note = null,
            recordedAtEpochMillis = 1_000,
            link = RecordLink.ToEvent(EventId("evt-1")),
        )

        repo.upsert(foreign)

        // Event's cached spend is in the event's own (home) currency — must sum the converted
        // amount, not the foreign record's raw cents, or a mixed-currency event misreports spend.
        val event = database.eventQueries.selectEventById("evt-1").executeAsOne()
        assertEquals(1_080, event.cached_spent_cents)
    }

    @Test
    fun upsert_movingLinkToAnotherEvent_recomputesBothCaches() = runTest {
        val database = inMemoryDatabase()
        seedEvent(database, "evt-1")
        seedEvent(database, "evt-2")
        val repo = repository(database)
        repo.upsert(record("rec-1", RecordLink.ToEvent(EventId("evt-1")), amountCents = 1_000))

        repo.upsert(record("rec-1", RecordLink.ToEvent(EventId("evt-2")), amountCents = 1_000))

        val first = database.eventQueries.selectEventById("evt-1").executeAsOne()
        val second = database.eventQueries.selectEventById("evt-2").executeAsOne()
        assertEquals(0, first.cached_spent_cents)
        assertEquals(1_000, second.cached_spent_cents)
    }

    @Test
    fun delete_linkedToEvent_recomputesCachedSpent() = runTest {
        val database = inMemoryDatabase()
        seedEvent(database, "evt-1")
        val repo = repository(database)
        repo.upsert(record("rec-1", RecordLink.ToEvent(EventId("evt-1")), amountCents = 1_000))

        repo.delete(RecordId("rec-1"))

        val event = database.eventQueries.selectEventById("evt-1").executeAsOne()
        assertEquals(0, event.cached_spent_cents)
    }

    @Test
    fun getRecordsPage_ordersByRecordedAtThenIdDescending() = runTest {
        val repo = repository()
        repo.upsert(record("rec-a", recordedAtEpochMillis = 1_000))
        repo.upsert(record("rec-c", recordedAtEpochMillis = 3_000))
        repo.upsert(record("rec-b1", recordedAtEpochMillis = 2_000))
        repo.upsert(record("rec-b2", recordedAtEpochMillis = 2_000))

        val page = repo.getRecordsPage(limit = 10)

        assertTrue(page is Result.Success)
        // Same recorded_at (2_000) breaks the tie by id descending — "rec-b2" before "rec-b1".
        assertEquals(listOf("rec-c", "rec-b2", "rec-b1", "rec-a"), page.data.map { it.id.value })
    }

    @Test
    fun getRecordsPage_respectsLimit() = runTest {
        val repo = repository()
        repeat(5) { i -> repo.upsert(record("rec-$i", recordedAtEpochMillis = i.toLong())) }

        val page = repo.getRecordsPage(limit = 2)

        assertTrue(page is Result.Success)
        assertEquals(2, page.data.size)
        assertEquals(listOf("rec-4", "rec-3"), page.data.map { it.id.value })
    }

    @Test
    fun getRecordsPage_cursorAdvancesToNextPageWithoutOverlap() = runTest {
        val repo = repository()
        repeat(5) { i -> repo.upsert(record("rec-$i", recordedAtEpochMillis = i.toLong())) }

        val firstPage = (repo.getRecordsPage(limit = 2) as Result.Success).data
        val last = firstPage.last()
        val cursor = RecordPageCursor(last.recordedAtEpochMillis, last.id)
        val secondPage = repo.getRecordsPage(cursor = cursor, limit = 2)

        assertTrue(secondPage is Result.Success)
        assertEquals(listOf("rec-2", "rec-1"), secondPage.data.map { it.id.value })
        assertTrue(firstPage.map { it.id.value }.none { it in secondPage.data.map { r -> r.id.value } })
    }

    @Test
    fun getRecordsPage_cursorAtEndReturnsEmptyList() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1", recordedAtEpochMillis = 1_000))
        val last = (repo.getRecordsPage(limit = 10) as Result.Success).data.last()

        val page = repo.getRecordsPage(cursor = RecordPageCursor(last.recordedAtEpochMillis, last.id), limit = 10)

        assertTrue(page is Result.Success)
        assertEquals(emptyList<FinanceRecord>(), page.data)
    }

    @Test
    fun getRecordsPage_filtersByCategory() = runTest {
        val repo = repository()
        repo.upsert(record("rec-food", categoryId = "food", recordedAtEpochMillis = 1_000))
        repo.upsert(record("rec-transport", categoryId = "transport", recordedAtEpochMillis = 2_000))

        val page = repo.getRecordsPage(filter = RecordPageFilter(categoryId = CategoryId("food")), limit = 10)

        assertTrue(page is Result.Success)
        assertEquals(listOf("rec-food"), page.data.map { it.id.value })
    }

    @Test
    fun getRecordsPage_filtersByDateRange() = runTest {
        val repo = repository()
        repo.upsert(record("rec-early", recordedAtEpochMillis = 1_000))
        repo.upsert(record("rec-mid", recordedAtEpochMillis = 2_000))
        repo.upsert(record("rec-late", recordedAtEpochMillis = 3_000))

        val page = repo.getRecordsPage(
            filter = RecordPageFilter(fromEpochMillis = 1_500, toEpochMillis = 2_500),
            limit = 10,
        )

        assertTrue(page is Result.Success)
        assertEquals(listOf("rec-mid"), page.data.map { it.id.value })
    }

    @Test
    fun getRecordsPage_filtersByQueryAgainstNote() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1", note = "Coffee with friends", recordedAtEpochMillis = 1_000))
        repo.upsert(record("rec-2", note = "Groceries", recordedAtEpochMillis = 2_000))

        val page = repo.getRecordsPage(filter = RecordPageFilter(query = "coffee"), limit = 10)

        assertTrue(page is Result.Success)
        assertEquals(listOf("rec-1"), page.data.map { it.id.value })
    }

    @Test
    fun existsByCategory_trueOnlyWhenARecordIsAssigned() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1", categoryId = "food"))

        assertEquals(true, (repo.existsByCategory(CategoryId("food")) as Result.Success).data)
        assertEquals(false, (repo.existsByCategory(CategoryId("uncategorized")) as Result.Success).data)
    }

    @Test
    fun observeChangeSignal_reflectsCountAndLatestUpdate() = runTest {
        val repo = repository()
        repo.upsert(record("rec-1"))

        val signal = repo.observeChangeSignal().first()

        assertEquals(1L, signal.count)
        assertTrue(signal.lastUpdatedAtEpochMillis > 0)
    }
}
