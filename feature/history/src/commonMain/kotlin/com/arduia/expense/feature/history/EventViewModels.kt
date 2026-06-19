package com.arduia.expense.feature.history

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.calculateBudgetProgress
import com.arduia.expense.domain.formatWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class EventListItemState(
    val id: String,
    val title: String,
    val dateRange: String,
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val isOverBudget: Boolean = false,
    val overAmountLabel: String? = null,
)

data class EventBudgetUiState(
    val events: List<EventListItemState> = emptyList(),
    val isLoading: Boolean = true,
)

class EventBudgetViewModel(
    private val eventRepository: EventRepository,
    private val historyRepository: HistoryRepository,
    private val formatter: RecordDateFormatter,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(EventBudgetUiState())
    val uiState: StateFlow<EventBudgetUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = eventRepository.getAll()) {
                is Result.Success -> {
                    val cards = result.data.map { event -> event.toCardState(historyRepository, formatter, homeCurrencyCode) }
                    _uiState.update { it.copy(events = cards, isLoading = false) }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun Event.toCardState(
        historyRepository: HistoryRepository,
        formatter: RecordDateFormatter,
        currency: String,
    ): EventListItemState {
        val records = when (val r = historyRepository.getRecords()) {
            is Result.Success -> r.data.filter {
                it.tagType == ExpenseTagType.EVENT && it.tagId == id
            }
            is Result.Error -> emptyList()
        }
        val spent = records.sumOf { it.homeCurrencyAmount.valueInCents }
        val progress = calculateBudgetProgress(
            spentCents = spent,
            budgetCents = budgetAmount.valueInCents,
            periodDays = formatter.daysInMonth(startEpochMillis).coerceAtLeast(1),
        )
        val dateRange = "${formatter.formatDayTitle(formatter.dayKey(startEpochMillis))} — ${formatter.formatDayTitle(formatter.dayKey(endEpochMillis))}"
        return EventListItemState(
            id = id,
            title = name,
            dateRange = dateRange,
            spentLabel = Amount(spent).formatWithSymbol(currency),
            budgetLabel = "of ${budgetAmount.formatWithSymbol(currency)}",
            progress = (progress.progressPercent / 100f).coerceIn(0f, 1f),
            isOverBudget = progress.progressPercent > 100f,
            overAmountLabel = if (progress.progressPercent > 100f) "over" else null,
        )
    }
}

data class EventCreateUiState(
    val name: String = "",
    val amountText: String = "",
    val showErrors: Boolean = false,
    val dateRangeLabel: String = "",
)

class EventCreateViewModel(
    private val eventRepository: EventRepository,
    private val formatter: RecordDateFormatter,
    private val scope: CoroutineScope,
    private val onSaved: () -> Unit,
) {
    private val _uiState = MutableStateFlow(EventCreateUiState())
    val uiState: StateFlow<EventCreateUiState> = _uiState.asStateFlow()

    private val startMillis = formatter.nowEpochMillis()
    private val endMillis = startMillis + 14L * 86_400_000L

    init {
        _uiState.update {
            it.copy(
                dateRangeLabel = "${formatter.formatDayTitle(formatter.dayKey(startMillis))} — ${formatter.formatDayTitle(formatter.dayKey(endMillis))}",
            )
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name.take(30), showErrors = false) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amountText = amount, showErrors = false) }
    }

    fun onSave() {
        val state = _uiState.value
        val cents = state.amountText.replace(",", "").toDoubleOrNull()?.let { (it * 100).toLong() }
        if (state.name.isBlank() || cents == null || cents <= 0) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        scope.launch {
            val event = Event(
                id = "event_${Random.nextLong().toString(16)}",
                name = state.name.trim(),
                startEpochMillis = startMillis,
                endEpochMillis = endMillis,
                budgetAmount = Amount(cents),
                status = EventStatus.ACTIVE,
            )
            when (eventRepository.upsert(event)) {
                is Result.Success -> onSaved()
                is Result.Error -> _uiState.update { it.copy(showErrors = true) }
            }
        }
    }
}

data class EventDetailUiState(
    val title: String = "",
    val dateRange: String = "",
    val spentLabel: String = "",
    val budgetLabel: String = "",
    val progress: Float = 0f,
    val isClosed: Boolean = false,
    val transactions: List<ExpenseListItem> = emptyList(),
    val isLoading: Boolean = true,
)

class EventDetailViewModel(
    private val eventId: String,
    private val eventRepository: EventRepository,
    private val historyRepository: HistoryRepository,
    private val formatter: RecordDateFormatter,
    private val homeCurrencyCode: String,
    private val categoryLabel: (String) -> String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        scope.launch { load() }
    }

    private suspend fun load() {
        when (val eventResult = eventRepository.getById(eventId)) {
            is Result.Success -> {
                val event = eventResult.data
                if (event == null) {
                    _uiState.update { it.copy(isLoading = false) }
                    return
                }
                val records = when (val r = historyRepository.getRecords()) {
                    is Result.Success -> r.data.filter {
                        it.tagType == ExpenseTagType.EVENT && it.tagId == eventId
                    }
                    is Result.Error -> emptyList()
                }
                val spent = records.sumOf { it.homeCurrencyAmount.valueInCents }
                val progress = calculateBudgetProgress(
                    spentCents = spent,
                    budgetCents = event.budgetAmount.valueInCents,
                    periodDays = formatter.daysInMonth(event.startEpochMillis).coerceAtLeast(1),
                )
                val dateRange = "${formatter.formatDayTitle(formatter.dayKey(event.startEpochMillis))} — ${formatter.formatDayTitle(formatter.dayKey(event.endEpochMillis))}"
                val transactions = records.map { record ->
                    ExpenseListItem(
                        id = record.id,
                        categoryId = record.categoryId,
                        note = record.note.orEmpty(),
                        meta = formatter.formatMeta(record.recordedAtEpochMillis, categoryLabel(record.categoryId)),
                        amount = record.homeCurrencyAmount.formatWithSymbol(homeCurrencyCode),
                        tag = null,
                    )
                }
                _uiState.update {
                    it.copy(
                        title = event.name,
                        dateRange = dateRange,
                        spentLabel = Amount(spent).formatWithSymbol(homeCurrencyCode),
                        budgetLabel = "of ${event.budgetAmount.formatWithSymbol(homeCurrencyCode)}",
                        progress = (progress.progressPercent / 100f).coerceIn(0f, 1f),
                        isClosed = event.status != EventStatus.ACTIVE,
                        transactions = transactions,
                        isLoading = false,
                    )
                }
            }
            is Result.Error -> _uiState.update { it.copy(isLoading = false) }
        }
    }
}
