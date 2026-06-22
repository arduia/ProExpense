package com.arduia.expense.ui.home

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import com.arduia.expense.ui.preview.HomeBudgetSummaryState
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppRoute { Loading, Onboarding, Home }

data class HomeScreenState(
    val route: AppRoute = AppRoute.Loading,
    val home: HomeUiState = HomeUiState(),
    val showPinBanner: Boolean = false,
)

/**
 * Read-model for Splash routing + Home, sourced entirely from the encrypted repositories. Built as
 * a plain class (scope + clock injected) so it can be unit-tested with fakes and no Android runtime.
 */
class HomeViewModel(
    private val financeRepository: FinanceRecordRepository,
    private val budgetRepository: BudgetRepository,
    private val profileRepository: ProfileRepository,
    private val securityStateReader: SecurityStateReader,
    private val scope: CoroutineScope,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    suspend fun completeOnboarding(name: String, currencyCode: String) {
        profileRepository.saveProfile(name.trim(), parseCurrency(currencyCode))
        profileRepository.setOnboardingCompleted(true)
        reload()
    }

    private suspend fun reload() {
        val profile = profileRepository.getProfile().getOrNull()
        val records = financeRepository.getAll().getOrNull().orEmpty()
        val budgetCents = budgetRepository.getMonthlyBudget().getOrNull()?.valueInCents
        val pinConfigured = securityStateReader.hasPinConfigured()

        val onboardingComplete = profile?.onboardingCompleted == true
        val currency = profile?.homeCurrency?.code ?: "USD"
        val now = clock()

        _state.value = HomeScreenState(
            route = if (onboardingComplete) AppRoute.Home else AppRoute.Onboarding,
            home = buildHome(profile?.name.orEmpty(), currency, records, budgetCents, now),
            showPinBanner = onboardingComplete && !pinConfigured,
        )
    }

    private fun buildHome(
        name: String,
        currency: String,
        records: List<FinanceRecord>,
        budgetCents: Long?,
        now: Long,
    ): HomeUiState {
        val expenses = records.filter { it.type == RecordType.EXPENSE }
        val monthStart = DateLabels.startOfMonth(now)
        val monthSpentCents = expenses
            .filter { it.recordedAtEpochMillis >= monthStart }
            .sumOf { it.homeCurrencyAmount.valueInCents }

        val recent = expenses.sortedByDescending { it.recordedAtEpochMillis }.take(MAX_RECENT)
        val dayGroups = recent
            .groupBy { DateLabels.startOfDay(it.recordedAtEpochMillis) }
            .map { (dayMillis, items) ->
                HomeDayGroup(
                    dayTitle = DateLabels.dayGroupTitle(dayMillis, now),
                    dayTotal = MoneyFormatter.format(items.sumOf { it.homeCurrencyAmount.valueInCents }, currency),
                    transactions = items.map { it.toItem(currency) },
                )
            }

        return HomeUiState(
            greetingName = name,
            dateLabel = DateLabels.topBar(now),
            monthLabel = DateLabels.monthLabel(now),
            monthSpend = MoneyFormatter.format(monthSpentCents, currency),
            showEmptyHint = expenses.isEmpty(),
            dayGroups = dayGroups,
            budgetSummary = budgetCents?.let { budgetSummary(monthSpentCents, it, currency) },
        )
    }

    private fun budgetSummary(spentCents: Long, budgetCents: Long, currency: String): HomeBudgetSummaryState {
        val ratio = if (budgetCents > 0) spentCents.toFloat() / budgetCents.toFloat() else 0f
        val overBudget = spentCents > budgetCents
        return HomeBudgetSummaryState(
            spentLabel = MoneyFormatter.format(spentCents, currency),
            budgetLabel = "of ${MoneyFormatter.format(budgetCents, currency)}",
            progress = ratio.coerceIn(0f, 1f),
            statusLabel = if (overBudget) "Over budget" else "On track",
            isOverBudget = overBudget,
        )
    }

    private fun FinanceRecord.toItem(currency: String): HomeTransactionItem {
        val categoryLabel = expenseCategoryLabel(categoryId)
        return HomeTransactionItem(
            id = id,
            categoryId = categoryId,
            note = note?.takeIf { it.isNotBlank() } ?: categoryLabel,
            meta = "$categoryLabel · ${DateLabels.timeLabel(recordedAtEpochMillis)}",
            amount = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
            tag = null,
        )
    }

    private fun parseCurrency(code: String): CurrencyCode = try {
        CurrencyCode(code)
    } catch (e: IllegalArgumentException) {
        CurrencyCode("USD")
    }

    private companion object {
        const val MAX_RECENT = 12
    }
}
