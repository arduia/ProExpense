/**
 * Domain rules (design plan Screen 03 / C6):
 * - PIN setup banner when no PIN configured.
 * - Banner dismiss hides for session only.
 * - Daily average uses full calendar month days (D10).
 */
package com.arduia.expense.feature.history

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Test
    fun refresh_showsPinBannerWhenPinNotConfigured() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            historyRepository = HistoryFakeHistoryRepository(emptyList()),
            budgetRepository = HistoryFakeBudgetRepository(null),
            securityState = HistoryFakeSecurityState(pinConfigured = false),
            formatter = HistoryFakeRecordDateFormatter(),
            categoryLabel = { "Food" },
            homeCurrencyCode = "USD",
            scope = this,
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showPinSetupBanner)
    }

    @Test
    fun refresh_hidesPinBannerWhenPinConfigured() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            historyRepository = HistoryFakeHistoryRepository(emptyList()),
            budgetRepository = HistoryFakeBudgetRepository(null),
            securityState = HistoryFakeSecurityState(pinConfigured = true),
            formatter = HistoryFakeRecordDateFormatter(),
            categoryLabel = { "Food" },
            homeCurrencyCode = "USD",
            scope = this,
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showPinSetupBanner)
    }

    @Test
    fun onPinBannerDismissed_hidesBannerForSession() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            historyRepository = HistoryFakeHistoryRepository(emptyList()),
            budgetRepository = HistoryFakeBudgetRepository(null),
            securityState = HistoryFakeSecurityState(pinConfigured = false),
            formatter = HistoryFakeRecordDateFormatter(),
            categoryLabel = { "Food" },
            homeCurrencyCode = "USD",
            scope = this,
        )
        advanceUntilIdle()
        viewModel.onPinBannerDismissed()

        assertFalse(viewModel.uiState.value.showPinSetupBanner)
    }

    @Test
    fun refresh_includesDailyAverageInDelta() = runTest(dispatcher) {
        val records = listOf(
            FinanceRecord(
                id = "r1",
                amount = Amount(3000),
                currency = com.arduia.expense.domain.CurrencyCode("USD"),
                homeCurrencyAmount = Amount(3000),
                categoryId = "food",
                type = RecordType.EXPENSE,
                note = null,
                recordedAtEpochMillis = 1_700_000_000_000L,
            ),
        )
        val viewModel = HomeViewModel(
            historyRepository = HistoryFakeHistoryRepository(records),
            budgetRepository = HistoryFakeBudgetRepository(Amount(10000)),
            securityState = HistoryFakeSecurityState(pinConfigured = true),
            formatter = HistoryFakeRecordDateFormatter(),
            categoryLabel = { "Food" },
            homeCurrencyCode = "USD",
            scope = this,
        )
        advanceUntilIdle()

        assertEquals("$1/day avg", viewModel.uiState.value.monthDelta)
    }
}
