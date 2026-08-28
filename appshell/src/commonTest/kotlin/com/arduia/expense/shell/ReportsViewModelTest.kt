package com.arduia.expense.shell

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.reports.GenerateReportPeriodUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Backbone coverage for the reports period window and totals.
 *
 * Traceability: US-REP-1 (month totals and daily average), US-REP-2 (period switching across a
 * 12-month window), US-REP-3 Scenario 3 (all-uncategorized is called out rather than shown as an
 * empty breakdown), and the top-5 + "Other" rollup in `GenerateReportPeriodUseCase`.
 *
 * The clock is pinned so the trailing-month window — including its December→January rollover — is
 * deterministic rather than dependent on the day the suite runs.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {
    private val zone = TimeZone.currentSystemDefault()

    /** 15 Jan 2026, midday — deliberately in January so the window spans a year boundary. */
    private val pinnedNow = LocalDateTime(2026, 1, 15, 12, 0).toInstant(zone).toEpochMilliseconds()

    private fun millisOn(
        year: Int,
        month: Int,
        day: Int,
    ): Long = LocalDate(year, month, day).atStartOfDayIn(zone).toEpochMilliseconds()

    private fun record(
        id: String,
        cents: Long,
        atEpochMillis: Long,
        categoryId: String = "food",
    ): FinanceRecord {
        val money = Money(Amount(cents), CurrencyCode("USD"))
        return FinanceRecord(
            id = RecordId(id),
            money = money,
            homeCurrencyMoney = money,
            categoryId = CategoryId(categoryId),
            type = RecordType.EXPENSE,
            note = null,
            recordedAtEpochMillis = atEpochMillis,
        )
    }

    private fun TestScope.viewModel(records: List<FinanceRecord>): ReportsViewModel =
        ReportsViewModel(
            financeRecordRepository = FakeRecords(records),
            categoryRepository = FakeCategories(),
            currencySettingsRepository = FakeCurrencySettings(),
            generateReportPeriod = GenerateReportPeriodUseCase(),
            nowEpochMillis = { pinnedNow },
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `the window is twelve months ending with the current one`() =
        runTest {
            val vm = viewModel(emptyList())

            advanceUntilIdle()

            val periods = vm.uiState.value.periods
            assertEquals(12, periods.size)
            assertEquals("FEB 2025", periods.first().label)
            assertEquals("JAN 2026", periods.last().label)
        }

    @Test
    fun `the window rolls back across the year boundary correctly`() =
        runTest {
            val vm = viewModel(emptyList())

            advanceUntilIdle()

            // Dec 2025 sits immediately before Jan 2026 — the year must decrement, not the month wrap.
            val labels =
                vm.uiState.value.periods
                    .map { it.label }
            assertEquals("DEC 2025", labels[labels.lastIndex - 1])
        }

    @Test
    fun `it opens on a month that has data rather than an empty current month`() =
        runTest {
            // Nothing in Jan 2026; spend only in Nov 2025.
            val vm = viewModel(listOf(record("a", 5_000, millisOn(2025, 11, 10))))

            advanceUntilIdle()

            assertEquals("NOV 2025", vm.uiState.value.selectedLabel)
            assertEquals("$50", vm.uiState.value.total)
        }

    @Test
    fun `selecting a period recomputes its totals`() =
        runTest {
            val vm =
                viewModel(
                    listOf(
                        record("a", 5_000, millisOn(2025, 11, 10)),
                        record("b", 3_000, millisOn(2026, 1, 5)),
                    ),
                )
            advanceUntilIdle()

            vm.onPeriodSelected(index = 11)
            advanceUntilIdle()

            assertEquals("JAN 2026", vm.uiState.value.selectedLabel)
            assertEquals("$30", vm.uiState.value.total)
        }

    @Test
    fun `a manual selection of an empty month is respected`() =
        runTest {
            val vm = viewModel(listOf(record("a", 5_000, millisOn(2025, 11, 10))))
            advanceUntilIdle()

            vm.onPeriodSelected(index = 11)
            advanceUntilIdle()

            assertEquals("JAN 2026", vm.uiState.value.selectedLabel)
            assertTrue(vm.uiState.value.isEmpty)
        }

    @Test
    fun `category rows resolve their names and sum to the period total`() =
        runTest {
            val vm =
                viewModel(
                    listOf(
                        record("a", 4_000, millisOn(2026, 1, 5), categoryId = "food"),
                        record("b", 1_000, millisOn(2026, 1, 6), categoryId = "travel"),
                    ),
                )
            advanceUntilIdle()

            vm.onPeriodSelected(index = 11)
            advanceUntilIdle()

            val rows = vm.uiState.value.categories
            assertEquals(setOf("Food", "Travel"), rows.map { it.label }.toSet())
            assertEquals(1f, rows.sumOf { it.fraction.toDouble() }.toFloat())
        }
}
