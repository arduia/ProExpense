package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun importExportRepo(database: ProExpenseDatabase): SqlDelightImportExportRepository {
    val financeRecordRepository = SqlDelightFinanceRecordRepository(
        queries = database.financeRecordQueries,
        eventQueries = database.eventQueries,
        dispatcher = Dispatchers.Unconfined,
    )
    val eventRepository = SqlDelightEventRepository(database.eventQueries, Dispatchers.Unconfined)
    val debtRepository = SqlDelightDebtRepository(database.debtQueries, Dispatchers.Unconfined)
    val sharedCostRepository = SqlDelightSharedCostRepository(
        queries = database.sharedCostQueries,
        financeRecordRepository = financeRecordRepository,
        dispatcher = Dispatchers.Unconfined,
    )
    return SqlDelightImportExportRepository(
        financeRecordRepository = financeRecordRepository,
        eventRepository = eventRepository,
        debtRepository = debtRepository,
        sharedCostRepository = sharedCostRepository,
        dispatcher = Dispatchers.Unconfined,
    )
}

class SqlDelightImportExportRepositoryTest {

    private val home = CurrencyCode("USD")

    @Test
    fun exportGrouped_returnsOneCsvPerRecordTypeWithRealData() = runTest {
        val database = inMemoryDatabase()
        val financeRecordRepository = SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )
        financeRecordRepository.upsert(
            FinanceRecord(
                id = RecordId("r1"),
                money = Money(Amount(50_00), home),
                homeCurrencyMoney = Money(Amount(50_00), home),
                categoryId = CategoryId("food"),
                type = RecordType.EXPENSE,
                note = "Lunch",
                recordedAtEpochMillis = 1000,
            ),
        )
        val eventRepository = SqlDelightEventRepository(database.eventQueries, Dispatchers.Unconfined)
        eventRepository.upsert(
            Event(
                id = EventId("e1"),
                name = "Trip",
                startEpochMillis = 1000,
                endEpochMillis = 2000,
                budget = Money(Amount(500_00), home),
            ),
        )
        val debtRepository = SqlDelightDebtRepository(database.debtQueries, Dispatchers.Unconfined)
        debtRepository.upsert(
            Debt(
                id = DebtId("d1"),
                personName = "Alice",
                money = Money(Amount(20_00), home),
                direction = DebtDirection.OWED_TO_ME,
            ),
        )

        val repo = importExportRepo(database)
        val result = repo.exportGrouped()

        assertTrue(result is Result.Success)
        val files = result.data
        assertEquals(setOf("expenses.csv", "events.csv", "debts.csv", "shared_costs.csv"), files.keys)
        assertTrue(files.getValue("expenses.csv").contains("\"r1\""))
        assertTrue(files.getValue("events.csv").contains("\"e1\""))
        assertTrue(files.getValue("debts.csv").contains("\"d1\""))
        assertEquals("shared_costs.csv" to "id,title,total_cents,currency_code,participant_count,recorded_at", "shared_costs.csv" to files.getValue("shared_costs.csv").lines().first())
    }

    private suspend fun seedForeignCurrencyRecord(repo: SqlDelightImportExportRepository, financeRecordRepository: SqlDelightFinanceRecordRepository) {
        financeRecordRepository.upsert(
            FinanceRecord(
                id = RecordId("r-eur"),
                money = Money(Amount(45_00), CurrencyCode("EUR")),
                homeCurrencyMoney = Money(Amount(48_60), home),
                categoryId = CategoryId("food"),
                type = RecordType.EXPENSE,
                note = "Dinner in Lisbon",
                recordedAtEpochMillis = 1_000,
            ),
        )
    }

    @Test
    fun csvRoundTrip_recordWithNoNoteAndNoTag_parsesWithoutColumnDesync() = runTest {
        val database = inMemoryDatabase()
        val financeRecordRepository = SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )
        val repo = importExportRepo(database)
        // The common case — no note, no @ tag — used to corrupt every column after the first
        // empty field because the CSV parser couldn't tell an empty quoted field ("") apart from
        // an escaped literal quote inside content.
        financeRecordRepository.upsert(
            FinanceRecord(
                id = RecordId("r-plain"),
                money = Money(Amount(12_34), home),
                homeCurrencyMoney = Money(Amount(12_34), home),
                categoryId = CategoryId("food"),
                type = RecordType.EXPENSE,
                note = null,
                recordedAtEpochMillis = 1_000,
            ),
        )

        val csv = (repo.exportAll(ExportFormat.CSV) as Result.Success).data
        val previewed = (repo.previewImport(csv, ExportFormat.CSV) as Result.Success).data

        assertEquals(1, previewed.size)
        val record = previewed.single()
        assertEquals(1_234, record.money.amount.valueInCents)
        assertEquals(home, record.money.currency)
        assertEquals("food", record.categoryId.value)
        assertEquals(null, record.note)
    }

    @Test
    fun csvRoundTrip_foreignCurrencyRecord_preservesHomeCurrencyAmount() = runTest {
        val database = inMemoryDatabase()
        val financeRecordRepository = SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )
        val repo = importExportRepo(database)
        seedForeignCurrencyRecord(repo, financeRecordRepository)

        val csv = (repo.exportAll(ExportFormat.CSV) as Result.Success).data
        val previewed = (repo.previewImport(csv, ExportFormat.CSV) as Result.Success).data

        assertEquals(1, previewed.size)
        val record = previewed.single()
        assertEquals(CurrencyCode("EUR"), record.money.currency)
        assertEquals(4_500, record.money.amount.valueInCents)
        assertEquals(home, record.homeCurrencyMoney.currency)
        assertEquals(4_860, record.homeCurrencyMoney.amount.valueInCents)
    }

    @Test
    fun jsonRoundTrip_foreignCurrencyRecord_preservesHomeCurrencyAmount() = runTest {
        val database = inMemoryDatabase()
        val financeRecordRepository = SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )
        val repo = importExportRepo(database)
        seedForeignCurrencyRecord(repo, financeRecordRepository)

        val json = (repo.exportAll(ExportFormat.JSON) as Result.Success).data
        val previewed = (repo.previewImport(json, ExportFormat.JSON) as Result.Success).data

        assertEquals(1, previewed.size)
        val record = previewed.single()
        assertEquals(CurrencyCode("EUR"), record.money.currency)
        assertEquals(4_500, record.money.amount.valueInCents)
        assertEquals(home, record.homeCurrencyMoney.currency)
        assertEquals(4_860, record.homeCurrencyMoney.amount.valueInCents)
        assertEquals("Dinner in Lisbon", record.note)
    }
}
