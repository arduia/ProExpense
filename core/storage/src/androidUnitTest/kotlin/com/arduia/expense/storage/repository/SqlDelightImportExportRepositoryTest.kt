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
}
