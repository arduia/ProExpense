package com.arduia.expense.shell

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.feature.eventbudget.ArchiveEventUseCase
import com.arduia.expense.feature.eventbudget.CloseEventUseCase
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.eventbudget.CreateEventUseCase
import com.arduia.expense.feature.eventbudget.DeleteEventUseCase
import com.arduia.expense.feature.eventbudget.activeEventCount
import com.arduia.expense.feature.eventbudget.isEventReadOnly
import com.arduia.expense.feature.eventbudget.visibleBudgetListEvents
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class EventCard(
    val eventId: String,
    val title: String,
    val dateRange: String,
    val spentLabel: String,
    val budgetLabel: String,
    val remainingLabel: String,
    val progress: Float,
    /** Uncapped spend ÷ budget — drives the US-EVT-3 warning/danger tiers past 100%. */
    val spentRatio: Float,
    val isOverBudget: Boolean,
    val overBudgetPercent: Int,
    val isClosed: Boolean,
    val isReadOnly: Boolean,
)

data class EventBudgetUiState(
    val cards: List<EventCard> = emptyList(),
    val activeCount: Int = 0,
    val selectedEventId: String? = null,
    val linkedRows: List<com.arduia.expense.ui.design.ProTransactionRowModel> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && cards.isEmpty()

    val selectedCard: EventCard? get() = cards.firstOrNull { it.eventId == selectedEventId }
}

/** The event lifecycle use cases, bundled so the ViewModel's constructor stays readable. */
data class EventBudgetActions(
    val computeProgress: ComputeEventProgressUseCase,
    val create: CreateEventUseCase,
    val close: CloseEventUseCase,
    val archive: ArchiveEventUseCase,
    val delete: DeleteEventUseCase,
)

/**
 * 07 · Event Budget and 08 · Event Detail.
 *
 * Spend is summed from the observed record list rather than [EventRepository.getSpent], so the
 * card and detail react immediately to any add/edit/delete of a linked expense — the same choice
 * the Compose shell makes. Visibility, active count, progress tiers and the closed-event grace
 * period all come from the shared use cases.
 */
class EventBudgetViewModel(
    private val eventRepository: EventRepository,
    private val financeRecordRepository: FinanceRecordRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val actions: EventBudgetActions,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<EventBudgetUiState>(EventBudgetUiState(), dispatcher) {
    private var events: List<Event> = emptyList()
    private var records: List<FinanceRecord> = emptyList()
    private var symbol: String = "$"

    init {
        viewModelScope.launch {
            val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
            symbol = currencySymbol(code ?: "USD")
            eventRepository
                .observeAll()
                .combine(financeRecordRepository.observeAll()) { allEvents, allRecords ->
                    allEvents to allRecords
                }.collect { (allEvents, allRecords) ->
                    events = allEvents
                    records = allRecords
                    project()
                }
        }
    }

    fun onEventSelected(eventId: String?) {
        setState { it.copy(selectedEventId = eventId) }
        project()
    }

    suspend fun create(
        name: String,
        rawBudget: String,
        startEpochMillis: Long?,
        endEpochMillis: Long?,
    ): String? {
        val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code ?: "USD"
        return actions.create(name, rawBudget, code, startEpochMillis, endEpochMillis)?.value
    }

    suspend fun close(eventId: String): Boolean {
        val event = events.firstOrNull { it.id.value == eventId } ?: return false
        return actions.close(event)
    }

    suspend fun archive(eventId: String): Boolean {
        val event = events.firstOrNull { it.id.value == eventId } ?: return false
        return actions.archive(event)
    }

    suspend fun delete(eventId: String) {
        actions.delete(EventId(eventId))
        setState { if (it.selectedEventId == eventId) it.copy(selectedEventId = null) else it }
    }

    private fun project() {
        val now = currentEpochMillis()
        val visible = visibleBudgetListEvents(events)
        val cards = visible.map { event -> event.toCard(now) }
        val selected = currentState().selectedEventId
        setState {
            it.copy(
                cards = cards,
                activeCount = activeEventCount(events),
                linkedRows = selected?.let { id -> linkedRowsFor(id) }.orEmpty(),
                isLoading = false,
            )
        }
    }

    private fun Event.toCard(nowEpochMillis: Long): EventCard {
        val progress = actions.computeProgress(this, spentFor(this))
        return EventCard(
            eventId = id.value,
            title = name,
            dateRange = dateRangeLabel(),
            spentLabel = AmountInput.formatMoney(progress.spentCents, symbol),
            budgetLabel = AmountInput.formatMoney(progress.budgetCents, symbol),
            remainingLabel = AmountInput.formatMoney(progress.remainingCents, symbol),
            progress = progress.progress,
            spentRatio = progress.spentRatio,
            isOverBudget = progress.isOverBudget,
            overBudgetPercent = progress.overBudgetPercent,
            isClosed = status == EventStatus.CLOSED,
            isReadOnly = isEventReadOnly(status, closedAtEpochMillis, nowEpochMillis),
        )
    }

    private fun Event.dateRangeLabel(): String =
        if (startEpochMillis == endEpochMillis) {
            PlatformDateFormatter.shortDateLabel(startEpochMillis)
        } else {
            PlatformDateFormatter.shortDateLabel(startEpochMillis) + " — " +
                PlatformDateFormatter.shortDateLabel(endEpochMillis)
        }

    private fun spentFor(event: Event): Money {
        val cents =
            records
                .filter { (it.link as? RecordLink.ToEvent)?.eventId == event.id }
                .sumOf { it.homeCurrencyMoney.amount.valueInCents }
        return Money(Amount(cents), event.budget.currency)
    }

    private fun linkedRowsFor(eventId: String): List<com.arduia.expense.ui.design.ProTransactionRowModel> =
        records
            .filter { (it.link as? RecordLink.ToEvent)?.eventId?.value == eventId }
            .sortedByDescending { it.recordedAtEpochMillis }
            .map { RecordRowProjection.toRow(it, emptyMap(), symbol) }
}
