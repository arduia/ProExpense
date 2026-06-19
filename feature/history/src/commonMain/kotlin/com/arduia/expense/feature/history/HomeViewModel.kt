package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.domain.formatWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val greetingName: String = "",
    val monthSpend: String = "$0",
    val monthDelta: String? = null,
    val dayGroups: List<ExpenseDayGroup> = emptyList(),
    val showEmptyHint: Boolean = true,
    val showPinSetupBanner: Boolean = false,
)

class HomeViewModel(
    private val historyRepository: HistoryRepository,
    private val securityState: SecurityStateReader,
    private val formatter: RecordDateFormatter,
    private val categoryLabel: (String) -> String,
    private val homeCurrencyCode: String,
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

            val monthSpend = when (summary) {
                is Result.Success -> summary.data.totalInHomeCurrency.formatWithSymbol(homeCurrencyCode)
                is Result.Error -> "$0"
            }
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
            val daysInMonth = formatter.daysInMonth(now).coerceAtLeast(1)
            val dailyAverageCents = when (summary) {
                is Result.Success -> summary.data.totalInHomeCurrency.valueInCents / daysInMonth
                is Result.Error -> 0L
            }
            val dailyAverage = com.arduia.expense.domain.Amount(dailyAverageCents)
                .formatWithSymbol(homeCurrencyCode)

            _uiState.update {
                it.copy(
                    monthSpend = monthSpend,
                    monthDelta = if (recordList.isEmpty()) null else "$dailyAverage/day avg",
                    dayGroups = dayGroups,
                    showEmptyHint = recordList.isEmpty(),
                    showPinSetupBanner = !pinConfigured && !bannerDismissedForSession,
                )
            }
        }
    }

    fun onPinBannerDismissed() {
        bannerDismissedForSession = true
        _uiState.update { it.copy(showPinSetupBanner = false) }
    }
}
