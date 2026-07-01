package com.arduia.expense.feature.reports.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.reports.GenerateReportPeriodUseCase
import com.arduia.expense.feature.reports.REPORT_OTHER_CATEGORY_ID
import com.arduia.expense.feature.reports.ReportPeriodResult
import com.arduia.expense.feature.reports.daysElapsedInPeriod
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

interface ReportsFeatureEntry {
    @Composable
    fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        empty: Boolean = false,
        onLogFirstExpense: () -> Unit = {},
    )
}

internal class ReportsFeatureEntryImpl : ReportsFeatureEntry {
    @Composable
    override fun ReportsFlow(
        onBack: () -> Unit,
        modifier: Modifier,
        empty: Boolean,
        onLogFirstExpense: () -> Unit,
    ) {
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val categoryRepository: CategoryRepository = koinInject()
        val generateReportPeriod: GenerateReportPeriodUseCase = koinInject()
        var periods by remember { mutableStateOf<List<ReportsUiState>>(emptyList()) }
        val otherCategoryLabel = stringResource(R.string.reports_other_category)

        LaunchedEffect(Unit) {
            val records = (financeRecordRepository.getAll() as? Result.Success)?.data.orEmpty()
            val categoryNames = (categoryRepository.getAll() as? Result.Success)?.data.orEmpty()
                .associate { it.id.value to it.name }

            val now = Calendar.getInstance()
            periods = (0 until REPORT_PERIOD_WINDOW_MONTHS).map { monthsBack ->
                val month = (now.clone() as Calendar).apply { add(Calendar.MONTH, -monthsBack) }
                buildPeriodState(generateReportPeriod, records, month, categoryNames, now, otherCategoryLabel)
            }
        }

        val initialPage = selectInitialPeriodIndex(periods.map { it.empty })

        com.arduia.expense.feature.reports.ui.ReportsFlow(
            onBack = onBack,
            modifier = modifier,
            periods = periods,
            initialPage = initialPage,
            empty = empty,
            onLogFirstExpense = onLogFirstExpense,
        )
    }
}

object ReportsFeatureUi : ReportsFeatureEntry by ReportsFeatureEntryImpl()

private fun buildPeriodState(
    generateReportPeriod: GenerateReportPeriodUseCase,
    records: List<com.arduia.expense.domain.FinanceRecord>,
    month: Calendar,
    categoryNames: Map<String, String>,
    now: Calendar,
    otherCategoryLabel: String,
): ReportsUiState {
    val start = (month.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val end = (start.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
    val periodLabel = SimpleDateFormat("MMMM yyyy", Locale.US).format(start.time)
    val daysInMonth = start.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysElapsed = daysElapsedInPeriod(
        periodYear = start.get(Calendar.YEAR),
        periodMonth = start.get(Calendar.MONTH),
        nowYear = now.get(Calendar.YEAR),
        nowMonth = now.get(Calendar.MONTH),
        nowDayOfMonth = now.get(Calendar.DAY_OF_MONTH),
        daysInMonth = daysInMonth,
    )

    val result: ReportPeriodResult = generateReportPeriod(
        records = records,
        periodStartEpochMillis = start.timeInMillis,
        periodEndEpochMillis = end.timeInMillis,
        daysInPeriod = daysElapsed,
    )

    if (result.empty) {
        return ReportsUiState(
            periodLabel = periodLabel,
            totalLabel = "",
            dailyAvgLabel = "",
            daysLabel = "",
            categories = emptyList(),
            empty = true,
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
            amountLabel = moneyLabel(breakdown.amountCents),
            fraction = breakdown.fraction,
        )
    }

    return ReportsUiState(
        periodLabel = periodLabel,
        totalLabel = moneyLabel(result.totalCents),
        dailyAvgLabel = moneyLabel(result.dailyAvgCents),
        daysLabel = "$daysElapsed days in",
        categories = categories,
        uncategorized = result.allUncategorized,
    )
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
