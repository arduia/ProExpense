package com.arduia.expense.feature.reports.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.reports.GenerateReportPeriodUseCase
import com.arduia.expense.feature.reports.REPORT_OTHER_CATEGORY_ID
import com.arduia.expense.feature.reports.ReportPeriodResult
import com.arduia.expense.feature.reports.ReportsPeriodBounds
import com.arduia.expense.feature.reports.daysElapsedInPeriod
import com.arduia.expense.feature.reports.daysElapsedInWeek
import com.arduia.expense.feature.reports.findGranularitySwitchTargetIndex
import com.arduia.expense.feature.reports.selectInitialPeriodIndex
import com.arduia.expense.feature.reports.ui.preview.ReportsCategoryUi
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.feature.reports.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.expenseCategoryLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
import org.koin.compose.koinInject
import com.arduia.expense.data.FinanceRecordRepository

private const val REPORT_PERIOD_WINDOW_MONTHS = 12
private const val REPORT_PERIOD_WINDOW_WEEKS = 12

interface ReportsFeatureEntry {
    @Composable
    fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        empty: Boolean = false,
        onLogFirstExpense: () -> Unit = {},
        homeCurrencySymbol: String = "$",
    )
}

internal class ReportsFeatureEntryImpl : ReportsFeatureEntry {
    @Composable
    override fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier,
        empty: Boolean,
        onLogFirstExpense: () -> Unit,
        homeCurrencySymbol: String,
    ) {
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val categoryRepository: CategoryRepository = koinInject()
        val generateReportPeriod: GenerateReportPeriodUseCase = koinInject()
        var monthlyPeriods by remember { mutableStateOf<List<ReportsUiState>>(emptyList()) }
        var weeklyPeriods by remember { mutableStateOf<List<ReportsUiState>>(emptyList()) }
        var granularityIndex by remember { mutableStateOf(0) }
        var isLoading by remember { mutableStateOf(true) }
        // Set on each granularity switch to the period the user was just viewing, so the new
        // granularity lands on the period that overlaps it instead of always jumping to the
        // latest period with data.
        var switchAnchor by remember { mutableStateOf<Pair<Long, Long>?>(null) }
        val otherCategoryLabel = stringResource(R.string.reports_other_category)
        // getQuantityString, not pluralStringResource — this is resolved inside the LaunchedEffect
        // (a suspend, non-@Composable context) once per period, each with its own day count.
        val resources = LocalContext.current.resources
        val daysInLabel: (Int) -> String = { count ->
            resources.getQuantityString(R.plurals.reports_days_in, count, count)
        }

        LaunchedEffect(Unit) {
            val records = (financeRecordRepository.getAll() as? Result.Success)?.data.orEmpty()
            val categoryNames = (categoryRepository.getAll() as? Result.Success)?.data.orEmpty()
                .associate { it.id.value to it.name }

            val now = Calendar.getInstance()
            monthlyPeriods = (0 until REPORT_PERIOD_WINDOW_MONTHS).map { monthsBack ->
                buildPeriodState(generateReportPeriod, records, monthWindow(monthsBack, now), categoryNames, otherCategoryLabel, homeCurrencySymbol, daysInLabel)
            }
            weeklyPeriods = (0 until REPORT_PERIOD_WINDOW_WEEKS).map { weeksBack ->
                buildPeriodState(generateReportPeriod, records, weekWindow(weeksBack, now), categoryNames, otherCategoryLabel, homeCurrencySymbol, daysInLabel)
            }
            isLoading = false
        }

        val periods = if (granularityIndex == 0) monthlyPeriods else weeklyPeriods
        val anchoredPage = switchAnchor?.let { (start, end) ->
            findGranularitySwitchTargetIndex(
                oldPeriodStartEpochMillis = start,
                oldPeriodEndEpochMillis = end,
                newPeriods = periods.map { ReportsPeriodBounds(it.periodStartEpochMillis, it.periodEndEpochMillis, it.empty) },
            ).takeIf { it >= 0 }
        }
        val initialPage = anchoredPage ?: selectInitialPeriodIndex(periods.map { it.empty })

        com.arduia.expense.feature.reports.ui.ReportsFlow(
            onBack = onBack,
            modifier = modifier,
            periods = periods,
            initialPage = initialPage,
            empty = empty,
            isLoading = isLoading,
            onLogFirstExpense = onLogFirstExpense,
            granularityIndex = granularityIndex,
            onGranularityChange = { index, anchor ->
                granularityIndex = index
                switchAnchor = anchor
            },
        )
    }
}

object ReportsFeatureUi : ReportsFeatureEntry by ReportsFeatureEntryImpl()

private data class PeriodWindow(
    val label: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val daysInPeriod: Int,
)

private fun monthWindow(monthsBack: Int, now: Calendar): PeriodWindow {
    val month = (now.clone() as Calendar).apply { add(Calendar.MONTH, -monthsBack) }
    val start = (month.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val end = (start.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
    val label = SimpleDateFormat("MMMM yyyy", Locale.US).format(start.time)
    val daysInMonth = start.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysElapsed = daysElapsedInPeriod(
        periodYear = start.get(Calendar.YEAR),
        periodMonth = start.get(Calendar.MONTH),
        nowYear = now.get(Calendar.YEAR),
        nowMonth = now.get(Calendar.MONTH),
        nowDayOfMonth = now.get(Calendar.DAY_OF_MONTH),
        daysInMonth = daysInMonth,
    )
    return PeriodWindow(label, start.timeInMillis, end.timeInMillis, daysElapsed)
}

/** Week bounds follow the device locale's first day of week (e.g. Sunday in en-US). */
private fun weekWindow(weeksBack: Int, now: Calendar): PeriodWindow {
    val target = (now.clone() as Calendar).apply { add(Calendar.WEEK_OF_YEAR, -weeksBack) }
    val firstDayOfWeek = target.firstDayOfWeek
    val offsetFromWeekStart = (target.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7
    val start = (target.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, -offsetFromWeekStart)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }
    val lastDay = (end.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
    val label = "${SimpleDateFormat("MMM d", Locale.US).format(start.time)} – " +
        SimpleDateFormat("MMM d", Locale.US).format(lastDay.time)

    val today = (now.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val isCurrentWeek = !today.before(start) && today.before(end)
    // Calendar-day count, not a fixed-24h-millis division — the latter is wrong by one on a
    // DST transition day.
    val daysSinceWeekStart = if (isCurrentWeek) calendarDayDiff(start, today) else 0
    val daysElapsed = daysElapsedInWeek(isCurrentWeek, daysSinceWeekStart)
    return PeriodWindow(label, start.timeInMillis, end.timeInMillis, daysElapsed)
}

private fun calendarDayDiff(from: Calendar, to: Calendar): Int {
    val fromYear = from.get(Calendar.YEAR)
    val fromDayOfYear = from.get(Calendar.DAY_OF_YEAR)
    val toYear = to.get(Calendar.YEAR)
    val toDayOfYear = to.get(Calendar.DAY_OF_YEAR)
    if (fromYear == toYear) return toDayOfYear - fromDayOfYear
    val daysInFromYear = (from.clone() as Calendar).getActualMaximum(Calendar.DAY_OF_YEAR)
    return (daysInFromYear - fromDayOfYear) + toDayOfYear
}

private fun buildPeriodState(
    generateReportPeriod: GenerateReportPeriodUseCase,
    records: List<com.arduia.expense.domain.FinanceRecord>,
    window: PeriodWindow,
    categoryNames: Map<String, String>,
    otherCategoryLabel: String,
    currencySymbol: String,
    daysInLabel: (Int) -> String,
): ReportsUiState {
    val result: ReportPeriodResult = generateReportPeriod(
        records = records,
        periodStartEpochMillis = window.startEpochMillis,
        periodEndEpochMillis = window.endEpochMillis,
        daysInPeriod = window.daysInPeriod,
    )

    if (result.empty) {
        return ReportsUiState(
            periodLabel = window.label,
            totalLabel = "",
            dailyAvgLabel = "",
            daysLabel = "",
            categories = emptyList(),
            empty = true,
            periodStartEpochMillis = window.startEpochMillis,
            periodEndEpochMillis = window.endEpochMillis,
        )
    }

    val categories = result.categories.map { breakdown ->
        ReportsCategoryUi(
            categoryId = breakdown.categoryId,
            label = if (breakdown.isOtherRollup) {
                otherCategoryLabel
            } else {
                categoryNames[breakdown.categoryId] ?: expenseCategoryLabel(breakdown.categoryId)
            },
            percentLabel = "${(breakdown.fraction * 100).roundToInt()}%",
            amountLabel = moneyLabel(breakdown.amountCents, currencySymbol),
            fraction = breakdown.fraction,
        )
    }

    return ReportsUiState(
        periodLabel = window.label,
        totalLabel = moneyLabel(result.totalCents, currencySymbol),
        dailyAvgLabel = moneyLabel(result.dailyAvgCents, currencySymbol),
        daysLabel = daysInLabel(window.daysInPeriod),
        categories = categories,
        uncategorized = result.allUncategorized,
        periodStartEpochMillis = window.startEpochMillis,
        periodEndEpochMillis = window.endEpochMillis,
    )
}

private fun moneyLabel(valueInCents: Long, currencySymbol: String): String =
    currencySymbol + AmountInput.formatMoney(valueInCents)
