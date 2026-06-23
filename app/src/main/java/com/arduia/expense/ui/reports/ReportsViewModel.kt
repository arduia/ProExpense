package com.arduia.expense.ui.reports

import com.arduia.expense.R
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.reports.ui.preview.ReportsCategoryUi
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import com.arduia.expense.ui.orPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class ReportsScreenState(
    val periods: List<ReportsUiState> = emptyList(),
)

/**
 * Read-model for Reports: buckets expenses by calendar month (most recent first) and computes the
 * per-month total, daily average and category breakdown in the home currency. The feature flow
 * navigates the resulting period list; an empty list drives the new-user empty state.
 */
class ReportsViewModel(
    private val financeRepository: FinanceRecordRepository,
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    private val _state = MutableStateFlow(ReportsScreenState())
    val state: StateFlow<ReportsScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    private suspend fun reload() {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val expenses = financeRepository.getAll()
            .orPost(uiMessages, R.string.data_load_error, emptyList())
            .filter { it.type == RecordType.EXPENSE }
        val now = clock()

        val periods = expenses
            .groupBy { DateLabels.monthKey(it.recordedAtEpochMillis) }
            .entries
            .sortedByDescending { it.key }
            .map { (_, monthRecords) -> monthRecords.toPeriod(currency, now) }

        _state.value = ReportsScreenState(periods = periods)
    }

    private fun List<FinanceRecord>.toPeriod(currency: String, now: Long): ReportsUiState {
        val monthMillis = first().recordedAtEpochMillis
        val totalCents = sumOf { it.homeCurrencyAmount.valueInCents }
        val days = DateLabels.daysElapsedInMonth(monthMillis, now).coerceAtLeast(1)

        val categories = groupBy { it.categoryId }
            .map { (categoryId, records) ->
                val catCents = records.sumOf { it.homeCurrencyAmount.valueInCents }
                val fraction = if (totalCents > 0) catCents.toFloat() / totalCents.toFloat() else 0f
                ReportsCategoryUi(
                    categoryId = categoryId,
                    label = expenseCategoryLabel(categoryId),
                    percentLabel = "${(fraction * 100).roundToInt()}%",
                    amountLabel = MoneyFormatter.format(catCents, currency),
                    fraction = fraction,
                )
            }
            .sortedByDescending { it.fraction }

        return ReportsUiState(
            periodLabel = DateLabels.monthYearLabel(monthMillis),
            totalLabel = MoneyFormatter.format(totalCents, currency),
            dailyAvgLabel = MoneyFormatter.format(totalCents / days, currency),
            daysLabel = "$days days in",
            categories = categories,
        )
    }
}
