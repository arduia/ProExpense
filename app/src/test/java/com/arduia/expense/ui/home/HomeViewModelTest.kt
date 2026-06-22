package com.arduia.expense.ui.home

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.fake.FakeBudgetRepository
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import com.arduia.expense.fake.FakeSecurityStateReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeViewModelTest {

    private val now = 1_700_000_000_000L // fixed "today"
    private val lastMonth = now - 60L * 24 * 60 * 60 * 1000

    private fun expense(id: String, cents: Long, at: Long, type: RecordType = RecordType.EXPENSE) =
        FinanceRecord(
            id = id,
            amount = Amount(cents),
            currency = CurrencyCode("USD"),
            homeCurrencyAmount = Amount(cents),
            categoryId = "food",
            type = type,
            note = "Lunch",
            recordedAtEpochMillis = at,
            tagType = null,
            tagId = null,
        )

    private fun TestScope.homeViewModel(
        records: List<FinanceRecord> = emptyList(),
        budget: Amount? = null,
        name: String = "Maya",
        onboardingCompleted: Boolean = true,
        pinConfigured: Boolean = false,
    ) = HomeViewModel(
        financeRepository = FakeFinanceRecordRepository(records),
        budgetRepository = FakeBudgetRepository(budget),
        profileRepository = FakeProfileRepository(
            name = name,
            currency = CurrencyCode("USD"),
            onboardingCompleted = onboardingCompleted,
        ),
        securityStateReader = FakeSecurityStateReader(pinConfigured),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        clock = { now },
    )

    // Rule: an unfinished profile routes to Onboarding.
    @Test
    fun route_isOnboarding_whenNotCompleted() = runTest {
        val vm = homeViewModel(onboardingCompleted = false)
        assertEquals(AppRoute.Onboarding, vm.state.value.route)
    }

    // Rule: a completed profile routes Home and shows the greeting name.
    @Test
    fun route_isHome_whenCompleted() = runTest {
        val vm = homeViewModel(name = "Maya")
        assertEquals(AppRoute.Home, vm.state.value.route)
        assertEquals("Maya", vm.state.value.home.greetingName)
    }

    // Rule: month spend sums only this month's EXPENSE records (excludes income + prior months).
    @Test
    fun monthSpend_sumsThisMonthExpensesOnly() = runTest {
        val vm = homeViewModel(
            records = listOf(
                expense("a", 1000, now),
                expense("b", 2000, now),
                expense("old", 5000, lastMonth),
                expense("income", 9999, now, type = RecordType.INCOME),
            ),
        )
        assertTrue(vm.state.value.home.monthSpend.contains("30.00"))
        assertFalse(vm.state.value.home.showEmptyHint)
    }

    // Rule: empty data sets the empty-hint flag and an empty recent list.
    @Test
    fun empty_setsEmptyHint() = runTest {
        val vm = homeViewModel(records = emptyList())
        assertTrue(vm.state.value.home.isEmpty)
        assertTrue(vm.state.value.home.dayGroups.isEmpty())
    }

    // Rule: recent rows carry the real record id so a tap can route to edit.
    @Test
    fun recentRows_carryRecordId() = runTest {
        val vm = homeViewModel(records = listOf(expense("rec-1", 1000, now)))
        val ids = vm.state.value.home.dayGroups.flatMap { it.transactions }.map { it.id }
        assertTrue("rec-1" in ids)
    }

    // Rule: a set budget produces a summary; over-budget is flagged.
    @Test
    fun budgetSummary_flagsOverBudget() = runTest {
        val vm = homeViewModel(
            records = listOf(expense("a", 60_000, now)),
            budget = Amount(50_000),
        )
        val summary = vm.state.value.home.budgetSummary
        assertNotNull(summary)
        assertTrue(summary.isOverBudget)
    }

    // Rule: no budget → no summary.
    @Test
    fun noBudget_noSummary() = runTest {
        val vm = homeViewModel(records = listOf(expense("a", 1000, now)), budget = null)
        assertNull(vm.state.value.home.budgetSummary)
    }

    // Rule: the PIN banner shows only once onboarding is done and no PIN is set.
    @Test
    fun pinBanner_shownWhenOnboardedAndNoPin() = runTest {
        assertTrue(homeViewModel(pinConfigured = false).state.value.showPinBanner)
        assertFalse(homeViewModel(pinConfigured = true).state.value.showPinBanner)
    }

    // Rule: completing onboarding persists the profile and flips the route to Home.
    @Test
    fun completeOnboarding_persistsAndRoutesHome() = runTest {
        val vm = homeViewModel(onboardingCompleted = false, name = "")
        vm.completeOnboarding("Carlos", "EUR")
        assertEquals(AppRoute.Home, vm.state.value.route)
        assertEquals("Carlos", vm.state.value.home.greetingName)
    }
}
