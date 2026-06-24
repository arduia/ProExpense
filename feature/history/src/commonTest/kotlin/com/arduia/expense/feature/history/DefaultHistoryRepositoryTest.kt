package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultHistoryRepositoryTest {

    private class FakeFinanceRecordRepository(private val records: List<FinanceRecord>) : FinanceRecordRepository {
        override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(records)
        override suspend fun getById(id: RecordId): Result<FinanceRecord?> =
            Result.Success(records.firstOrNull { it.id == id })
        override suspend fun upsert(record: FinanceRecord): Result<Unit> = Result.Success(Unit)
        override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)
        override fun observeAll(): Flow<List<FinanceRecord>> = flowOf(records)
    }

    private val usd = CurrencyCode("USD")

    private fun atUtc(year: Int, month: Int, day: Int, hour: Int = 12): Long =
        LocalDateTime(year, month, day, hour, 0).toInstant(TimeZone.UTC).toEpochMilliseconds()

    private fun record(
        id: String,
        epochMillis: Long,
        homeCents: Long,
        type: RecordType = RecordType.EXPENSE,
        category: String = "food",
        note: String? = null,
        currency: CurrencyCode = usd,
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(homeCents), currency),
        homeCurrencyMoney = Money(Amount(homeCents), usd),
        categoryId = CategoryId(category),
        type = type,
        note = note,
        recordedAtEpochMillis = epochMillis,
    )

    private fun repository(records: List<FinanceRecord>) = DefaultHistoryRepository(
        financeRecordRepository = FakeFinanceRecordRepository(records),
        homeCurrencyProvider = { usd },
        timeZone = TimeZone.UTC,
    )

    @Test
    fun getRecords_filtersByCategoryDateAndQuery() = runTest {
        val records = listOf(
            record("a", atUtc(2026, 6, 10), 100, category = "food", note = "Coffee shop"),
            record("b", atUtc(2026, 6, 20), 200, category = "transport", note = "Taxi"),
            record("c", atUtc(2026, 7, 1), 300, category = "food", note = "Groceries"),
        )
        val repo = repository(records)

        val byCategory = repo.getRecords(RecordHistoryFilter(categoryId = CategoryId("food")))
        assertTrue(byCategory is Result.Success)
        assertEquals(setOf("a", "c"), byCategory.data.map { it.id.value }.toSet())

        val byRange = repo.getRecords(
            RecordHistoryFilter(fromEpochMillis = atUtc(2026, 6, 1), toEpochMillis = atUtc(2026, 6, 30)),
        )
        assertTrue(byRange is Result.Success)
        assertEquals(setOf("a", "b"), byRange.data.map { it.id.value }.toSet())

        val byQuery = repo.getRecords(RecordHistoryFilter(query = "coffee"))
        assertTrue(byQuery is Result.Success)
        assertEquals(listOf("a"), byQuery.data.map { it.id.value })
    }

    @Test
    fun getSummary_monthly_sumsExpensesInMonthOnly() = runTest {
        val records = listOf(
            record("jun-1", atUtc(2026, 6, 10), 1_000),
            record("jun-2", atUtc(2026, 6, 20), 500),
            record("jun-income", atUtc(2026, 6, 12), 9_999, type = RecordType.INCOME),
            record("may", atUtc(2026, 5, 31), 700),
            record("jul", atUtc(2026, 7, 1), 200),
        )
        val repo = repository(records)

        val result = repo.getSummary(SummaryPeriod.MONTHLY, anchorEpochMillis = atUtc(2026, 6, 15))

        assertTrue(result is Result.Success)
        assertEquals(1_500, result.data.totalInHomeCurrency.amount.valueInCents)
        assertEquals("USD", result.data.totalInHomeCurrency.currency.code)
        assertEquals(2, result.data.recordCount)
    }

    @Test
    fun getSummary_daily_boundsToAnchorDay() = runTest {
        val records = listOf(
            record("today-early", atUtc(2026, 6, 15, hour = 1), 300),
            record("today-late", atUtc(2026, 6, 15, hour = 23), 100),
            record("yesterday", atUtc(2026, 6, 14, hour = 23), 999),
        )
        val repo = repository(records)

        val result = repo.getSummary(SummaryPeriod.DAILY, anchorEpochMillis = atUtc(2026, 6, 15, hour = 10))

        assertTrue(result is Result.Success)
        assertEquals(400, result.data.totalInHomeCurrency.amount.valueInCents)
        assertEquals(2, result.data.recordCount)
    }

    @Test
    fun getSummary_emptyPeriod_returnsZeroInHomeCurrency() = runTest {
        val repo = repository(emptyList())

        val result = repo.getSummary(SummaryPeriod.WEEKLY, anchorEpochMillis = atUtc(2026, 6, 15))

        assertTrue(result is Result.Success)
        assertEquals(0, result.data.totalInHomeCurrency.amount.valueInCents)
        assertEquals("USD", result.data.totalInHomeCurrency.currency.code)
        assertEquals(0, result.data.recordCount)
    }
}
