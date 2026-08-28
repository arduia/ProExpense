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
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

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

/** Everything Home reads from, bundled so the constructor stays readable. */
data class HomeSources(
    val records: FinanceRecordRepository,
    val categories: CategoryRepository,
    val profile: ProfileRepository,
    val budget: BudgetRepository,
    val currencySettings: CurrencySettingsRepository,
)

/**
 * Home's data projection — greeting, month-to-date spend, budget progress and the recent-rows list.
 *
 * Lives in `commonMain` so the SwiftUI Home renders from exactly the same derivation the Compose
 * Home does. Every label is built through [AmountInput] and [RecordRowProjection], both of which
 * already work on iOS, so no formatting logic is duplicated per platform.
 */
class HomeViewModel(
    private val sources: HomeSources,
    /** Injected so month-boundary behaviour is pinnable, like the other date-sensitive ViewModels. */
    private val nowEpochMillis: () -> Long = ::currentEpochMillis,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<HomeUiState>(HomeUiState(), dispatcher) {
    init {
        viewModelScope.launch {
            val symbol = loadHomeCurrencySymbol()
            val budget = (sources.budget.getMonthlyBudget() as? Result.Success)?.data
            val displayName = (sources.profile.getDisplayName() as? Result.Success)?.data.orEmpty()
            setState { it.copy(greetingName = displayName, homeCurrencySymbol = symbol) }
            sources.records
                .observeAll()
                .combine(sources.categories.observeAll()) { records, categories ->
                    records to categories.associate { category -> category.id.value to category.name }
                }.collect { (records, categoryNames) ->
                    project(records, categoryNames, symbol, budget)
                }
        }
    }

    private suspend fun loadHomeCurrencySymbol(): String {
        val code = (sources.currencySettings.getHomeCurrency() as? Result.Success)?.data?.code
        return currencySymbol(code ?: "USD")
    }

    private fun project(
        records: List<FinanceRecord>,
        categoryNames: Map<String, String>,
        symbol: String,
        budget: Money?,
    ) {
        val now = nowEpochMillis()
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
     *
     * The month's bounds are computed once and compared numerically. Deriving a month key per record
     * instead meant two platform date-formatter calls for every row in the history, which is the
     * expensive part of this projection and scales with the whole record list, not the visible page.
     */
    private fun monthToDateSpendCents(
        records: List<FinanceRecord>,
        nowEpochMillis: Long,
    ): Long {
        val (monthStart, monthEnd) = currentMonthBounds(nowEpochMillis)
        return records
            .filter {
                it.type == RecordType.EXPENSE &&
                    it.recordedAtEpochMillis >= monthStart &&
                    it.recordedAtEpochMillis < monthEnd
            }.sumOf { it.homeCurrencyMoney.amount.valueInCents }
    }

    /** Half-open `[start, end)` bounds of the calendar month containing [epochMillis], device-local. */
    private fun currentMonthBounds(epochMillis: Long): Pair<Long, Long> {
        val zone = TimeZone.currentSystemDefault()
        val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(zone).date
        val start = LocalDate(date.year, date.month, 1).atStartOfDayIn(zone).toEpochMilliseconds()
        val nextMonth =
            if (date.month.number == MONTHS_PER_YEAR) {
                LocalDate(date.year + 1, 1, 1)
            } else {
                LocalDate(date.year, date.month.number + 1, 1)
            }
        return start to nextMonth.atStartOfDayIn(zone).toEpochMilliseconds()
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

    private companion object {
        const val MONTHS_PER_YEAR = 12
    }
}
