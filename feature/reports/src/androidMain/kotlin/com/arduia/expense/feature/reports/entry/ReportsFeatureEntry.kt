package com.arduia.expense.feature.reports.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.reports.ui.preview.ReportsCategoryUi
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.expenseCategoryLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
import org.koin.compose.koinInject
import com.arduia.expense.data.FinanceRecordRepository

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
        var periods by remember { mutableStateOf<List<ReportsUiState>>(emptyList()) }

        LaunchedEffect(Unit) {
            val records = (financeRecordRepository.getAll() as? Result.Success)?.data.orEmpty()
            val categoryNames = (categoryRepository.getAll() as? Result.Success)?.data.orEmpty()
                .associate { it.id.value to it.name }

            val now = Calendar.getInstance()
            val previousMonth = (now.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            periods = listOf(
                buildPeriodState(records, now, categoryNames),
                buildPeriodState(records, previousMonth, categoryNames),
            )
        }

        com.arduia.expense.feature.reports.ui.ReportsFlow(
            onBack = onBack,
            modifier = modifier,
            periods = periods,
            empty = empty,
            onLogFirstExpense = onLogFirstExpense,
        )
    }
}

object ReportsFeatureUi : ReportsFeatureEntry by ReportsFeatureEntryImpl()

private fun buildPeriodState(
    records: List<FinanceRecord>,
    month: Calendar,
    categoryNames: Map<String, String>,
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

    val inPeriod = records.filter { record ->
        record.type == RecordType.EXPENSE &&
            record.recordedAtEpochMillis >= start.timeInMillis &&
            record.recordedAtEpochMillis < end.timeInMillis
    }

    if (inPeriod.isEmpty()) {
        return ReportsUiState(
            periodLabel = periodLabel,
            totalLabel = "",
            dailyAvgLabel = "",
            daysLabel = "",
            categories = emptyList(),
            empty = true,
        )
    }

    val totalCents = inPeriod.sumOf { it.homeCurrencyMoney.amount.valueInCents }
    val daysInMonth = start.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dailyAvgCents = totalCents / daysInMonth

    val categories = inPeriod
        .groupBy { it.categoryId.value }
        .mapValues { (_, group) -> group.sumOf { it.homeCurrencyMoney.amount.valueInCents } }
        .entries
        .sortedByDescending { it.value }
        .take(5)
        .map { (categoryId, amountCents) ->
            val fraction = if (totalCents == 0L) 0f else amountCents.toFloat() / totalCents
            ReportsCategoryUi(
                categoryId = categoryId,
                label = categoryNames[categoryId] ?: expenseCategoryLabel(categoryId),
                percentLabel = "${(fraction * 100).roundToInt()}%",
                amountLabel = moneyLabel(amountCents),
                fraction = fraction,
            )
        }

    return ReportsUiState(
        periodLabel = periodLabel,
        totalLabel = moneyLabel(totalCents),
        dailyAvgLabel = moneyLabel(dailyAvgCents),
        daysLabel = "$daysInMonth days in",
        categories = categories,
    )
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
