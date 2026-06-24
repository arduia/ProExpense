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
        override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> =
            Result.Success(records.any { it.id == id })
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

    // getSummary tests deferred — implementation pending
    /* TODO: implement getSummary in DefaultHistoryRepository
    @Test
    fun getSummary_monthly_sumsExpensesInMonthOnly() = runTest { }

    @Test
    fun getSummary_daily_boundsToAnchorDay() = runTest { }

    @Test
    fun getSummary_emptyPeriod_returnsZeroInHomeCurrency() = runTest { }
    */
}
