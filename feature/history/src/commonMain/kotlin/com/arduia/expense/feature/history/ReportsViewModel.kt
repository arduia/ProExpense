package com.arduia.expense.feature.history

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.BudgetColorTier
import com.arduia.expense.domain.calculateBudgetProgress
import com.arduia.expense.domain.formatWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsCategoryState(
    val categoryId: String,
    val label: String,
    val amountLabel: String,
    val share: Float,
)

data class ReportsUiState(
    val monthLabel: String = "",
    val totalSpend: String = "$0",
    val dailyAverage: String = "",
    val budgetColor: BudgetColorTier? = null,
    val categories: List<ReportsCategoryState> = emptyList(),
    val isLoading: Boolean = true,
)

class ReportsViewModel(
    private val historyRepository: HistoryRepository,
    private val budgetRepository: BudgetRepository,
    private val formatter: RecordDateFormatter,
    private val homeCurrencyCode: String,
    private val categoryLabel: (String) -> String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private var anchorMillis = formatter.nowEpochMillis()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val summary = historyRepository.getSummary(SummaryPeriod.MONTHLY, anchorMillis)
            val budget = budgetRepository.getMonthlyBudget()
            val days = formatter.daysInMonth(anchorMillis).coerceAtLeast(1)
            when (summary) {
                is Result.Success -> {
                    val spent = summary.data.totalInHomeCurrency.valueInCents
                    val budgetCents = (budget as? Result.Success)?.data?.valueInCents
                    val progress = calculateBudgetProgress(spent, budgetCents, days)
                    val monthLabel = formatter.formatMonthYear(anchorMillis)
                    val categories = buildCategoryBreakdown(anchorMillis, spent)
                    _uiState.update {
                        it.copy(
                            monthLabel = monthLabel,
                            totalSpend = summary.data.totalInHomeCurrency.formatWithSymbol(homeCurrencyCode),
                            dailyAverage = com.arduia.expense.domain.Amount(progress.dailyAverageCents)
                                .formatWithSymbol(homeCurrencyCode) + "/day",
                            budgetColor = progress.colorTier,
                            categories = categories,
                            isLoading = false,
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onPreviousMonth() {
        anchorMillis -= 30L * 86_400_000L
        refresh()
    }

    fun onNextMonth() {
        anchorMillis += 30L * 86_400_000L
        refresh()
    }

    private suspend fun buildCategoryBreakdown(anchorMillis: Long, totalCents: Long): List<ReportsCategoryState> {
        if (totalCents <= 0L) return emptyList()
        val monthKey = formatter.formatMonthYear(anchorMillis)
        val records = when (val result = historyRepository.getRecords()) {
            is Result.Success -> result.data.filter {
                formatter.formatMonthYear(it.recordedAtEpochMillis) == monthKey
            }
            is Result.Error -> emptyList()
        }
        val grouped = records.groupBy { it.categoryId }
        return grouped.map { (categoryId, items) ->
            val cents = items.sumOf { it.homeCurrencyAmount.valueInCents }
            ReportsCategoryState(
                categoryId = categoryId,
                label = categoryLabel(categoryId),
                amountLabel = com.arduia.expense.domain.Amount(cents).formatWithSymbol(homeCurrencyCode),
                share = cents.toFloat() / totalCents.toFloat(),
            )
        }.sortedByDescending { it.share }
    }
}
