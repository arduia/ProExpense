/**
 * Domain rules (design plan D10 / Reports):
 * - Monthly spend and daily average label (spent ÷ days in month).
 * - Budget color tiers from shared BudgetEngine.
 */
package com.arduia.expense.feature.history

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.BudgetColorTier
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

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
                note = null,
                recordedAtEpochMillis = 1_700_000_000_000L,
            ),
        )
        val viewModel = ReportsViewModel(
            historyRepository = HistoryFakeHistoryRepository(records),
            budgetRepository = HistoryFakeBudgetRepository(Amount(10000)),
            formatter = HistoryFakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = this,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.dailyAverage.contains("/day"))
        assertEquals(BudgetColorTier.SIGNIFICANTLY_OVER, state.budgetColor)
    }

    @Test
    fun refresh_slightlyOverBudget_usesYellowTier() = runTest(dispatcher) {
        val records = listOf(
            FinanceRecord(
                id = "r1",
                amount = Amount(10500),
                currency = com.arduia.expense.domain.CurrencyCode("USD"),
                homeCurrencyAmount = Amount(10500),
                categoryId = "food",
                type = RecordType.EXPENSE,
                note = null,
                recordedAtEpochMillis = 1_700_000_000_000L,
            ),
        )
        val viewModel = ReportsViewModel(
            historyRepository = HistoryFakeHistoryRepository(records),
            budgetRepository = HistoryFakeBudgetRepository(Amount(10000)),
            formatter = HistoryFakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = this,
        )
        advanceUntilIdle()

        assertEquals(BudgetColorTier.SLIGHTLY_OVER, viewModel.uiState.value.budgetColor)
    }

    @Test
    fun refresh_withoutBudget_hasNullColorTier() = runTest(dispatcher) {
        val viewModel = ReportsViewModel(
            historyRepository = HistoryFakeHistoryRepository(emptyList()),
            budgetRepository = HistoryFakeBudgetRepository(null),
            formatter = HistoryFakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = this,
        )
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.budgetColor)
        assertTrue(viewModel.uiState.value.dailyAverage.contains("/day"))
    }
}
