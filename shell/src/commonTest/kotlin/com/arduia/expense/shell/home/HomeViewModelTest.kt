package com.arduia.expense.shell.home

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import com.arduia.expense.feature.currency.CurrencySettings
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Wiring coverage: repository Flows and one-shot settings reads reach [ShellUiState], and the
 * shell's change callbacks recompute Home. The mapping rules themselves are pinned in
 * [HomeStateMapperTest]. Traces to US-HOME-1/2/4 and US-CUR-1 (home currency).
 */
class HomeViewModelTest {
    private val now = 1_784_116_800_000L // 2026-07-15T12:00:00Z
    private val hour = 3_600_000L

    private val records = FakeFinanceRecordRepository()
    private val categories = FakeCategoryRepository()
    private val events = FakeEventRepository()
    private val debts = FakeDebtRepository()
    private val sharedCosts = FakeSharedCostRepository()
    private val budget = FakeBudgetRepository()
    private val defaultCategory = FakeDefaultCategoryRepository()
    private val currency = FakeCurrencyRepository()

    private fun viewModel() =
        HomeViewModel(
            sources =
                HomeDataSources(
                    records = records,
                    categories = categories,
                    events = events,
                    debts = debts,
                    sharedCosts = sharedCosts,
                    budget = budget,
                    defaultCategory = defaultCategory,
                    currency = currency,
                ),
            computeEventProgress = ComputeEventProgressUseCase(),
            nowEpochMillis = { now },
        )

    /**
     * [com.arduia.expense.shared.ProViewModel] owns a real `Dispatchers.Default` scope, so the
     * test waits on the real clock instead of `runTest`'s virtual one.
     */
    private suspend fun HomeViewModel.awaitState(predicate: (ShellUiState) -> Boolean): ShellUiState =
        withContext(Dispatchers.Default) {
            withTimeout(AWAIT_TIMEOUT_MILLIS) { uiState.first(predicate) }
        }

    @Test
    fun recordsFlow_reachesHomeState() =
        runTest {
            records.flow.value =
                listOf(
                    testRecord("r1", 3_000, now - hour),
                    testRecord("r2", 2_000, now - 2 * hour),
                )
            val vm = viewModel()

            val state = vm.awaitState { it.records.size == 2 }

            assertEquals(2, state.home.dayGroups.sumOf { group -> group.transactions.size })
            assertEquals(false, state.home.showEmptyHint)
            assertEquals(false, state.recordsLoading)
            vm.onCleared()
        }

    @Test
    fun beforeFirstEmission_stateReportsLoadingRatherThanEmpty() {
        // The starting state itself, not a timing-dependent read of a live ViewModel: the shell
        // must never see `isEmpty` before the first record emission has had a chance to land.
        val initial = ShellUiState()

        assertTrue(initial.recordsLoading)
        assertTrue(initial.home.isLoading)
        assertEquals(false, initial.home.isEmpty)
    }

    @Test
    fun settings_loadIntoStateOnStart() =
        runTest {
            currency.settings = Result.Success(CurrencySettings(CurrencyCode("EUR")))
            budget.budget = Result.Success(Money(Amount(10_000), CurrencyCode("USD")))
            defaultCategory.defaultCategoryId = Result.Success("transport")
            records.flow.value = listOf(testRecord("r1", 5_000, now - hour))
            val vm = viewModel()

            val state =
                vm.awaitState {
                    it.records.isNotEmpty() && it.defaultCategoryId == "transport" && it.homeCurrencyCode == "EUR"
                }

            val summary = assertNotNull(state.home.budgetSummary)
            assertTrue(summary.spentLabel.startsWith("€"), summary.spentLabel)
            vm.onCleared()
        }

    @Test
    fun settingsFailure_leavesDefaultsIntactInsteadOfCrashing() =
        runTest {
            currency.settings = Result.Error("no settings")
            budget.budget = Result.Error("no budget")
            defaultCategory.defaultCategoryId = Result.Error("no default category")
            records.flow.value = listOf(testRecord("r1", 1_000, now - hour))
            val vm = viewModel()

            val state = vm.awaitState { it.records.isNotEmpty() }

            assertEquals("USD", state.homeCurrencyCode)
            assertEquals("food", state.defaultCategoryId)
            assertEquals(null, state.home.budgetSummary)
            assertEquals("$10", state.home.monthSpend)
            vm.onCleared()
        }

    @Test
    fun activeEvent_isTheMostRecentlyStartedActiveEvent() =
        runTest {
            events.flow.value =
                listOf(
                    Event(
                        id = EventId("e_old"),
                        name = "Old Trip",
                        startEpochMillis = now - 20L * 24 * hour,
                        endEpochMillis = now,
                        budget = Money(Amount(100_000), CurrencyCode("USD")),
                    ),
                    Event(
                        id = EventId("e_new"),
                        name = "Bali Trip",
                        startEpochMillis = now - 24 * hour,
                        endEpochMillis = now + 5L * 24 * hour,
                        budget = Money(Amount(200_000), CurrencyCode("USD")),
                    ),
                    // A closed event never becomes the Home card (US-EVT-5).
                    Event(
                        id = EventId("e_closed"),
                        name = "Closed Trip",
                        startEpochMillis = now,
                        endEpochMillis = now + 24 * hour,
                        budget = Money(Amount(300_000), CurrencyCode("USD")),
                        status = EventStatus.CLOSED,
                    ),
                )
            val vm = viewModel()

            val state = vm.awaitState { it.events.size == 3 && it.home.activeEvent != null }

            val active = assertNotNull(state.home.activeEvent)
            assertEquals("e_new", active.eventId)
            assertEquals("Bali Trip", active.title)
            assertEquals("of $2,000", active.budgetLabel)
            vm.onCleared()
        }

    @Test
    fun currencyChange_reformatsHomeAmounts() =
        runTest {
            records.flow.value = listOf(testRecord("r1", 1_234, now - hour))
            val vm = viewModel()
            vm.awaitState { it.records.isNotEmpty() }

            vm.onHomeCurrencyChanged("EUR")

            val state = vm.uiState.value
            assertEquals("EUR", state.homeCurrencyCode)
            assertTrue(state.home.monthSpend.startsWith("€"), state.home.monthSpend)
            vm.onCleared()
        }

    @Test
    fun userName_flowsIntoTheGreeting() =
        runTest {
            val vm = viewModel()

            vm.onUserNameChanged("Maya")

            assertEquals("Maya", vm.uiState.value.home.greetingName)
            vm.onCleared()
        }

    private companion object {
        const val AWAIT_TIMEOUT_MILLIS = 5_000L
    }
}
