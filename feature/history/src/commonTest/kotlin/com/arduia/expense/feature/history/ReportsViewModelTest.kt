package com.arduia.expense.feature.history

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.BudgetColorTier
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun refresh_includesDailyAverageAndBudgetColor() = runTest(dispatcher) {
        val records = listOf(
            FinanceRecord(
                id = "r1",
                amount = Amount(30000),
                currency = com.arduia.expense.domain.CurrencyCode("USD"),
                homeCurrencyAmount = Amount(30000),
                categoryId = "food",
                type = RecordType.EXPENSE,
                recordedAtEpochMillis = 1_700_000_000_000L,
            ),
        )
        val viewModel = ReportsViewModel(
            historyRepository = FakeHistoryRepository(records),
            budgetRepository = FakeBudgetRepository(Amount(10000)),
            formatter = FakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = scope.backgroundScope,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.dailyAverage.contains("/day"))
        assertEquals(BudgetColorTier.SIGNIFICANTLY_OVER, state.budgetColor)
    }
}

private class FakeHistoryRepository(
    private val records: List<FinanceRecord>,
) : HistoryRepository {
    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        Result.Success(records)

    override suspend fun getById(id: String): Result<FinanceRecord?> =
        Result.Success(records.firstOrNull { it.id == id })

    override suspend fun deleteRecord(id: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> {
        val total = records.sumOf { it.homeCurrencyAmount.valueInCents }
        return Result.Success(
            RecordSummary(
                period = period,
                totalInHomeCurrency = Amount(total),
                recordCount = records.size,
            ),
        )
    }
}

private class FakeBudgetRepository(
    private val budget: Amount?,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Amount?> = Result.Success(budget)

    override suspend fun setMonthlyBudget(amount: Amount?): Result<Unit> = Result.Success(Unit)
}

private class FakeRecordDateFormatter : RecordDateFormatter {
    override fun dayKey(epochMillis: Long): Long = epochMillis

    override fun formatDayTitle(dayKey: Long): String = "May 12"

    override fun formatMonthYear(epochMillis: Long): String = "May 2025"

    override fun formatMeta(epochMillis: Long, categoryLabel: String): String = categoryLabel

    override fun nowEpochMillis(): Long = 1_700_000_000_000L

    override fun daysInMonth(epochMillis: Long): Int = 30
}
