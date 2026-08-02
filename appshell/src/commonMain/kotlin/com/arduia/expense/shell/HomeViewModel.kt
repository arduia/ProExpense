package com.arduia.expense.shell

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordType
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.DateZone
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** Home shows the last handful of entries, not the whole history (US-HOME-2). */
const val RECENT_HOME_LIMIT = 8

data class HomeBudgetSummary(
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val isOverBudget: Boolean,
)

data class HomeDayGroup(
    val dayTitle: String,
    val dayTotal: String,
    val rows: List<ProTransactionRowModel>,
)

data class HomeUiState(
    val greetingName: String = "",
    val monthSpend: String = "",
    val homeCurrencySymbol: String = "$",
    val dayGroups: List<HomeDayGroup> = emptyList(),
    val budgetSummary: HomeBudgetSummary? = null,
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && dayGroups.isEmpty()
}

/**
 * Home's data projection — greeting, month-to-date spend, budget progress and the recent-rows list.
 *
 * Lives in `commonMain` so the SwiftUI Home renders from exactly the same derivation the Compose
 * Home does. Every label is built through [PlatformDateFormatter] / [AmountInput], both of which
 * already have iOS actuals, so no formatting logic is duplicated per platform.
 */
class HomeViewModel(
    private val financeRecordRepository: FinanceRecordRepository,
    private val categoryRepository: CategoryRepository,
    private val profileRepository: ProfileRepository,
    private val budgetRepository: BudgetRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<HomeUiState>(HomeUiState(), dispatcher) {
    init {
        viewModelScope.launch {
            val symbol = loadHomeCurrencySymbol()
            val budget = (budgetRepository.getMonthlyBudget() as? Result.Success)?.data
            val displayName = (profileRepository.getDisplayName() as? Result.Success)?.data.orEmpty()
            setState { it.copy(greetingName = displayName, homeCurrencySymbol = symbol) }
            financeRecordRepository
                .observeAll()
                .combine(categoryRepository.observeAll()) { records, categories ->
                    records to categories.associate { category -> category.id.value to category.name }
                }.collect { (records, categoryNames) ->
                    project(records, categoryNames, symbol, budget)
                }
        }
    }

    private suspend fun loadHomeCurrencySymbol(): String {
        val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
        return currencySymbol(code ?: "USD")
    }

    private fun project(
        records: List<FinanceRecord>,
        categoryNames: Map<String, String>,
        symbol: String,
        budget: Money?,
    ) {
        val now = currentEpochMillis()
        val monthSpendCents = monthToDateSpendCents(records, now)
        setState {
            it.copy(
                monthSpend = AmountInput.formatMoney(monthSpendCents, symbol),
                dayGroups =
                    RecordRowProjection.toDayGroups(
                        records = records.sortedByDescending { r -> r.recordedAtEpochMillis }.take(RECENT_HOME_LIMIT),
                        categoryNames = categoryNames,
                        currencySymbol = symbol,
                        nowEpochMillis = now,
                    ),
                budgetSummary = buildBudgetSummary(monthSpendCents, budget, symbol),
                isLoading = false,
            )
        }
    }

    /**
     * "Spend this month" (US-HOME-1) — month-to-date, and expenses only: income must not offset the
     * figure the header promises.
     */
    private fun monthToDateSpendCents(
        records: List<FinanceRecord>,
        nowEpochMillis: Long,
    ): Long {
        val currentMonthKey = monthKey(nowEpochMillis)
        return records
            .filter { it.type == RecordType.EXPENSE && monthKey(it.recordedAtEpochMillis) == currentMonthKey }
            .sumOf { it.homeCurrencyMoney.amount.valueInCents }
    }

    /**
     * "Jun/2026" style bucket. Derived from [PlatformDateFormatter] rather than a calendar API so it
     * stays portable — `shortDateLabel` renders "Jun 3, 2026", whose first token is the month.
     */
    private fun monthKey(epochMillis: Long): String {
        val month = PlatformDateFormatter.shortDateLabel(epochMillis, withYear = true).substringBefore(' ')
        val year = PlatformDateFormatter.yearOf(epochMillis, DateZone.DeviceLocal)
        return "$month/$year"
    }

    private fun buildBudgetSummary(
        spentCents: Long,
        budget: Money?,
        symbol: String,
    ): HomeBudgetSummary? {
        val budgetCents = budget?.amount?.valueInCents
        return if (budgetCents == null || budgetCents <= 0L) {
            null
        } else {
            HomeBudgetSummary(
                spentLabel = AmountInput.formatMoney(spentCents, symbol),
                budgetLabel = AmountInput.formatMoney(budgetCents, symbol),
                progress = (spentCents.toDouble() / budgetCents.toDouble()).coerceIn(0.0, 1.0).toFloat(),
                isOverBudget = spentCents > budgetCents,
            )
        }
    }
}
