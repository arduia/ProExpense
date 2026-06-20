package com.arduia.expense.feature.history

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.calculateBudgetProgress
import com.arduia.expense.domain.formatWithSymbol
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeBudgetSummary(
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val statusLabel: String,
    val isOverBudget: Boolean,
)

data class HomeActiveEventHighlight(
    val eventId: String,
    val title: String,
    val dateRange: String,
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val isOverBudget: Boolean,
)

data class HomeUiState(
    val greetingName: String = "",
    val monthSpend: String = "$0",
    val monthDelta: String? = null,
    val dayGroups: List<ExpenseDayGroup> = emptyList(),
    val showEmptyHint: Boolean = true,
    val showPinSetupBanner: Boolean = false,
    val budgetSummary: HomeBudgetSummary? = null,
    val activeEvent: HomeActiveEventHighlight? = null,
)

class HomeViewModel(
    private val historyRepository: HistoryRepository,
    private val budgetRepository: BudgetRepository,
    private val eventRepository: EventRepository,
    private val securityState: SecurityStateReader,
    private val formatter: RecordDateFormatter,
    private val categoryLabel: (String) -> String,
    private val homeCurrencyCode: String,
    private val displayNameProvider: () -> String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var bannerDismissedForSession = false

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            val now = formatter.nowEpochMillis()
            val summary = historyRepository.getSummary(SummaryPeriod.MONTHLY, now)
            val records = historyRepository.getRecords()
            val pinConfigured = securityState.hasPinConfigured()
            val daysInMonth = formatter.daysInMonth(now).coerceAtLeast(1)

            val budget = budgetRepository.getMonthlyBudget()
            val budgetCents = when (budget) {
                is Result.Success -> budget.data?.valueInCents
                is Result.Error -> null
            }
            val spentCents = when (summary) {
                is Result.Success -> summary.data.totalInHomeCurrency.valueInCents
                is Result.Error -> 0L
            }
            val progress = calculateBudgetProgress(
                spentCents = spentCents,
                budgetCents = budgetCents,
                periodDays = daysInMonth,
            )
            val monthSpend = Amount(spentCents).formatWithSymbol(homeCurrencyCode)
            val recordList = when (records) {
                is Result.Success -> records.data
                is Result.Error -> emptyList()
            }
            val dayGroups = groupRecordsByDay(
                records = recordList,
                formatter = formatter,
                homeCurrencyCode = homeCurrencyCode,
                categoryLabel = categoryLabel,
                recentLimit = 10,
            )
            val monthDelta = formatMonthDelta(
                spentCents = spentCents,
                now = now,
                hasRecords = recordList.isNotEmpty(),
                daysInMonth = daysInMonth,
                dailyAverageCents = progress.dailyAverageCents,
            )
            val budgetSummary = budgetCents?.let { cents ->
                val statusLabel = if (progress.progressPercent > 100f) {
                    "Over budget"
                } else {
                    "On track"
                }
                HomeBudgetSummary(
                    spentLabel = monthSpend,
                    budgetLabel = "of ${Amount(cents).formatWithSymbol(homeCurrencyCode)}",
                    progress = (progress.progressPercent / 100f).coerceIn(0f, 1f),
                    statusLabel = statusLabel,
                    isOverBudget = progress.progressPercent > 100f,
                )
            }
            val activeEvent = loadActiveEventHighlight()

            _uiState.update {
                it.copy(
                    greetingName = displayNameProvider().trim(),
                    monthSpend = monthSpend,
                    monthDelta = monthDelta,
                    dayGroups = dayGroups,
                    showEmptyHint = recordList.isEmpty(),
                    showPinSetupBanner = !pinConfigured && !bannerDismissedForSession,
                    budgetSummary = budgetSummary,
                    activeEvent = activeEvent,
                )
            }
        }
    }

    fun onPinBannerDismissed() {
        bannerDismissedForSession = true
        _uiState.update { it.copy(showPinSetupBanner = false) }
    }

    private suspend fun formatMonthDelta(
        spentCents: Long,
        now: Long,
        hasRecords: Boolean,
        daysInMonth: Int,
        dailyAverageCents: Long,
    ): String? {
        if (!hasRecords) return null
        val previousAnchor = formatter.minusMonths(now, 1)
        val previousSummary = historyRepository.getSummary(SummaryPeriod.MONTHLY, previousAnchor)
        val previousCents = when (previousSummary) {
            is Result.Success -> previousSummary.data.totalInHomeCurrency.valueInCents
            is Result.Error -> 0L
        }
        if (previousCents > 0L) {
            val changePercent = ((spentCents - previousCents).toDouble() / previousCents * 100).roundToInt()
            val sign = if (changePercent > 0) "+" else if (changePercent < 0) "" else ""
            return "$sign$changePercent% from last month"
        }
        val dailyAverage = Amount(dailyAverageCents).formatWithSymbol(homeCurrencyCode)
        return "$dailyAverage/day avg"
    }

    private suspend fun loadActiveEventHighlight(): HomeActiveEventHighlight? {
        val events = when (val result = eventRepository.getAll()) {
            is Result.Success -> result.data.filter { it.status == EventStatus.ACTIVE }
            is Result.Error -> emptyList()
        }
        val event = events.firstOrNull() ?: return null
        val records = when (val result = historyRepository.getRecords()) {
            is Result.Success -> result.data.filter {
                it.tagType == ExpenseTagType.EVENT && it.tagId == event.id
            }
            is Result.Error -> emptyList()
        }
        val spent = records.sumOf { it.homeCurrencyAmount.valueInCents }
        val periodDays = eventPeriodDays(event.startEpochMillis, event.endEpochMillis)
        val progress = calculateBudgetProgress(
            spentCents = spent,
            budgetCents = event.budgetAmount.valueInCents,
            periodDays = periodDays,
        )
        val dateRange = "${formatter.formatDayTitle(formatter.dayKey(event.startEpochMillis))} — ${formatter.formatDayTitle(formatter.dayKey(event.endEpochMillis))}"
        return HomeActiveEventHighlight(
            eventId = event.id,
            title = event.name,
            dateRange = dateRange,
            spentLabel = Amount(spent).formatWithSymbol(homeCurrencyCode),
            budgetLabel = "of ${event.budgetAmount.formatWithSymbol(homeCurrencyCode)}",
            progress = (progress.progressPercent / 100f).coerceIn(0f, 1f),
            isOverBudget = progress.progressPercent > 100f,
        )
    }

    private fun eventPeriodDays(startEpochMillis: Long, endEpochMillis: Long): Int {
        val span = abs(endEpochMillis - startEpochMillis)
        return ((span / 86_400_000L) + 1).toInt().coerceAtLeast(1)
    }
}
