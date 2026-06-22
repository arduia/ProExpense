package com.arduia.expense.ui.reports

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReportsViewModelTest {

    private val now = 1_700_000_000_000L // mid-month "today"
    private val twoMonthsAgo = now - 60L * 24 * 60 * 60 * 1000

    private fun record(id: String, cents: Long, at: Long, categoryId: String = "food", type: RecordType = RecordType.EXPENSE) =
        FinanceRecord(
            id = id,
            amount = Amount(cents),
            currency = CurrencyCode("USD"),
            homeCurrencyAmount = Amount(cents),
            categoryId = categoryId,
            type = type,
            note = null,
            recordedAtEpochMillis = at,
        )

    private fun TestScope.reportsViewModel(records: List<FinanceRecord>) = ReportsViewModel(
        financeRepository = FakeFinanceRecordRepository(records),
        profileRepository = FakeProfileRepository(currency = CurrencyCode("USD"), onboardingCompleted = true),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        clock = { now },
    )

    // Rule: no expenses → no periods (drives the empty state in the UI).
    @Test
    fun emptyData_noPeriods() = runTest {
        assertTrue(reportsViewModel(emptyList()).state.value.periods.isEmpty())
    }

    // Rule: records are bucketed into one period per calendar month.
    @Test
    fun bucketsByMonth() = runTest {
        val vm = reportsViewModel(
            listOf(
                record("a", 1000, now),
                record("b", 2000, now),
                record("old", 3000, twoMonthsAgo),
            ),
        )
        assertEquals(2, vm.state.value.periods.size)
    }

    // Rule: a month's total + category breakdown is computed and ordered by share.
    @Test
    fun computesTotalsAndCategoryShare() = runTest {
        val vm = reportsViewModel(
            listOf(
                record("a", 7000, now, categoryId = "food"),
                record("b", 3000, now, categoryId = "transport"),
            ),
        )
        val period = vm.state.value.periods.first()
        assertTrue(period.totalLabel.contains("100.00"))
        assertEquals("food", period.categories.first().categoryId) // largest share first
        assertEquals("70%", period.categories.first().percentLabel)
    }

    // Rule: income never contributes to a spend report.
    @Test
    fun excludesIncome() = runTest {
        val vm = reportsViewModel(
            listOf(
                record("a", 1000, now),
                record("income", 9999, now, type = RecordType.INCOME),
            ),
        )
        assertTrue(vm.state.value.periods.first().totalLabel.contains("10.00"))
    }
}
