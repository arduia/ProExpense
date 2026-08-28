package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.feature.reports.GenerateReportPeriodUseCase
import com.arduia.expense.feature.reports.REPORT_OTHER_CATEGORY_ID
import com.arduia.expense.feature.reports.selectInitialPeriodIndex
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
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

/** How many trailing months the period switcher offers. */
private const val PERIOD_COUNT = 12

private val MONTH_LABELS =
    listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

data class ReportCategoryRow(
    val categoryId: String,
    val label: String,
    val amount: String,
    val fraction: Float,
    val isOtherRollup: Boolean,
)

data class ReportPeriodOption(
    val index: Int,
    val label: String,
    val isEmpty: Boolean,
)

data class ReportsUiState(
    val periods: List<ReportPeriodOption> = emptyList(),
    val selectedIndex: Int = 0,
    val total: String = "",
    val dailyAverage: String = "",
    val categories: List<ReportCategoryRow> = emptyList(),
    val allUncategorized: Boolean = false,
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && categories.isEmpty()

    val selectedLabel: String get() = periods.getOrNull(selectedIndex)?.label.orEmpty()
}

/**
 * 12 · Reports — month-by-month spend totals and the top-5 category breakdown.
 *
 * Totals, ranking and the "Other" rollup all come from [GenerateReportPeriodUseCase]; the initial
 * month comes from [selectInitialPeriodIndex] so the screen opens on data rather than on an empty
 * current month. Month boundaries use `kotlinx-datetime` in the device timezone — the same library
 * the history repository already uses for its period math.
 */
class ReportsViewModel(
    private val financeRecordRepository: FinanceRecordRepository,
    private val categoryRepository: CategoryRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val generateReportPeriod: GenerateReportPeriodUseCase,
    /** Injected so the trailing-month window is pinnable in tests — reading the wall clock here
     *  makes month-boundary behaviour a dated time bomb. */
    private val nowEpochMillis: () -> Long = ::currentEpochMillis,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<ReportsUiState>(ReportsUiState(), dispatcher) {
    private var records: List<FinanceRecord> = emptyList()
    private var categoryNames: Map<String, String> = emptyMap()
    private var symbol: String = "$"
    private var bounds: List<Pair<Long, Long>> = emptyList()
    private var initialised = false

    init {
        viewModelScope.launch {
            val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
            symbol = currencySymbol(code ?: "USD")
            bounds = trailingMonthBounds(nowEpochMillis())
            financeRecordRepository
                .observeAll()
                .combine(categoryRepository.observeAll()) { all, categories ->
                    all to categories.associate { it.id.value to it.name }
                }.collect { (all, names) ->
                    records = all
                    categoryNames = names
                    project()
                }
        }
    }

    fun onPeriodSelected(index: Int) {
        setState { it.copy(selectedIndex = index.coerceIn(0, bounds.lastIndex)) }
        project()
    }

    private fun project() {
        val periodResults =
            bounds.map { (start, end) ->
                generateReportPeriod(records, start, end, daysBetween(start, end))
            }
        val options =
            bounds.mapIndexed { index, (start, _) ->
                ReportPeriodOption(index = index, label = monthLabel(start), isEmpty = periodResults[index].empty)
            }
        // Only auto-pick a period on the first emission — after that the user's choice wins, even
        // if their selected month happens to be empty.
        val selected =
            if (initialised) {
                currentState().selectedIndex.coerceIn(0, bounds.lastIndex)
            } else {
                initialised = true
                selectInitialPeriodIndex(periodResults.map { it.empty })
            }
        val result = periodResults[selected]
        setState {
            it.copy(
                periods = options,
                selectedIndex = selected,
                total = AmountInput.formatMoney(result.totalCents, symbol),
                dailyAverage = AmountInput.formatMoney(result.dailyAvgCents, symbol),
                categories =
                    result.categories.map { breakdown ->
                        ReportCategoryRow(
                            categoryId = breakdown.categoryId,
                            label = labelFor(breakdown.categoryId, breakdown.isOtherRollup),
                            amount = AmountInput.formatMoney(breakdown.amountCents, symbol),
                            fraction = breakdown.fraction,
                            isOtherRollup = breakdown.isOtherRollup,
                        )
                    },
                allUncategorized = result.allUncategorized,
                isLoading = false,
            )
        }
    }

    private fun labelFor(
        categoryId: String,
        isOtherRollup: Boolean,
    ): String =
        when {
            isOtherRollup || categoryId == REPORT_OTHER_CATEGORY_ID -> "Other"
            else -> categoryNames[categoryId] ?: categoryId
        }

    private fun monthLabel(startEpochMillis: Long): String {
        val date = Instant.fromEpochMilliseconds(startEpochMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${MONTH_LABELS[date.month.number - 1]} ${date.year}"
    }

    private fun daysBetween(
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Int = ((endEpochMillis - startEpochMillis) / MILLIS_PER_DAY).toInt()

    /** [PERIOD_COUNT] month ranges ending with the month containing [nowEpochMillis], oldest first. */
    private fun trailingMonthBounds(nowEpochMillis: Long): List<Pair<Long, Long>> {
        val zone = TimeZone.currentSystemDefault()
        val today = Instant.fromEpochMilliseconds(nowEpochMillis).toLocalDateTime(zone).date
        return (PERIOD_COUNT - 1 downTo 0).map { monthsBack ->
            val totalMonths = today.year * MONTHS_PER_YEAR + (today.month.number - 1) - monthsBack
            val year = totalMonths / MONTHS_PER_YEAR
            val month = totalMonths % MONTHS_PER_YEAR + 1
            val start = LocalDate(year, month, 1).atStartOfDayIn(zone).toEpochMilliseconds()
            val nextTotal = totalMonths + 1
            val nextStart =
                LocalDate(nextTotal / MONTHS_PER_YEAR, nextTotal % MONTHS_PER_YEAR + 1, 1)
                    .atStartOfDayIn(zone)
                    .toEpochMilliseconds()
            start to nextStart
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
        const val MONTHS_PER_YEAR = 12
    }
}
