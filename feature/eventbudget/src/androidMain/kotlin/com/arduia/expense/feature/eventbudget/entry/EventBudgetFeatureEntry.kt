package com.arduia.expense.feature.eventbudget.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import com.arduia.expense.feature.eventbudget.UpdateEventUseCase
import com.arduia.expense.feature.eventbudget.activeEventCount
import com.arduia.expense.feature.eventbudget.ui.EventsFlow
import com.arduia.expense.feature.eventbudget.ui.preview.EventCreateFormState
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.feature.eventbudget.ui.preview.EventLinkedExpenseUi
import com.arduia.expense.feature.eventbudget.visibleBudgetListEvents
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.eventBudgetTone
import com.arduia.expense.ui.design.expenseCategoryLabel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface EventBudgetFeatureEntry {
    @Composable
    fun EventsTab(
        events: List<Event>,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        records: List<FinanceRecord> = emptyList(),
        spentByEvent: Map<String, Money> = emptyMap(),
        categoryNames: Map<String, String> = emptyMap(),
        modifier: Modifier = Modifier,
        initialSelectedEventId: String? = null,
        onAddTaggedExpense: (eventId: String) -> Unit = { onAddClick() },
        onExpenseClick: (recordId: String) -> Unit = {},
        homeCurrencySymbol: String = "$",
        isLoading: Boolean = false,
    )
}

internal class EventBudgetFeatureEntryImpl : EventBudgetFeatureEntry {
    @Composable
    override fun EventsTab(
        events: List<Event>,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        records: List<FinanceRecord>,
        spentByEvent: Map<String, Money>,
        categoryNames: Map<String, String>,
        modifier: Modifier,
        initialSelectedEventId: String?,
        onAddTaggedExpense: (eventId: String) -> Unit,
        onExpenseClick: (recordId: String) -> Unit,
        homeCurrencySymbol: String,
        isLoading: Boolean,
    ) {
        val scope = rememberCoroutineScope()
        val computeProgress: ComputeEventProgressUseCase = koinInject()
        val createEvent: CreateEventUseCase = koinInject()
        val updateEvent: UpdateEventUseCase = koinInject()
        val closeEvent: CloseEventUseCase = koinInject()
        val archiveEvent: ArchiveEventUseCase = koinInject()
        val deleteEvent: DeleteEventUseCase = koinInject()

        val linkedByEvent =
            remember(records) {
                records
                    .filter { it.link is RecordLink.ToEvent }
                    .groupBy { (it.link as RecordLink.ToEvent).eventId.value }
            }

        // `events` (unfiltered) still backs `details`/`editForms` below so an archived event stays
        // reachable if the user gets to it via a deep link — see US-EVT-2 Scenario 4.
        val visibleEvents = visibleBudgetListEvents(events)
        val cards =
            visibleEvents.map { it.toCardState(computeProgress(it, spentByEvent[it.id.value]), homeCurrencySymbol) }
        val activeCount = activeEventCount(visibleEvents)
        val activeOverBudgetCount =
            visibleEvents.zip(cards).count { (event, card) ->
                event.status == EventStatus.ACTIVE && card.isOverBudget
            }
        val details =
            events.associate { event ->
                event.id.value to
                    event.toDetailState(
                        progress = computeProgress(event, spentByEvent[event.id.value]),
                        linkedRecords = linkedByEvent[event.id.value].orEmpty(),
                        categoryNames = categoryNames,
                        currencySymbol = homeCurrencySymbol,
                    )
            }

        val editForms = events.associate { it.id.value to it.toEditFormState() }

        EventsFlow(
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            events = cards,
            activeCount = activeCount,
            overBudgetCount = activeOverBudgetCount,
            eventDetails = details,
            eventEditForms = editForms,
            onCreateEvent = { name, budgetRaw, startEpochMillis, endEpochMillis, onCreated ->
                scope.launch {
                    createEvent.createThenNotify(name, budgetRaw, startEpochMillis, endEpochMillis, onCreated)
                }
            },
            onUpdateEvent = { id, name, budgetRaw, startEpochMillis, endEpochMillis ->
                val existing = events.firstOrNull { it.id.value == id }
                if (existing != null) {
                    scope.launch { updateEvent(existing, name, budgetRaw, startEpochMillis, endEpochMillis) }
                }
            },
            onCloseEvent = { id ->
                val existing = events.firstOrNull { it.id.value == id }
                if (existing != null) {
                    scope.launch { closeEvent(existing) }
                }
            },
            onArchiveEvent = { id ->
                val existing = events.firstOrNull { it.id.value == id }
                if (existing != null) {
                    scope.launch { archiveEvent(existing) }
                }
            },
            onDeleteEvent = { id ->
                scope.launch { deleteEvent(EventId(id)) }
            },
            initialSelectedEventId = initialSelectedEventId,
            onAddTaggedExpense = onAddTaggedExpense,
            onExpenseClick = onExpenseClick,
            isLoading = isLoading,
            modifier = modifier,
        )
    }
}

object EventBudgetFeatureUi : EventBudgetFeatureEntry by EventBudgetFeatureEntryImpl()

private suspend fun CreateEventUseCase.createThenNotify(
    name: String,
    budgetRaw: String,
    startEpochMillis: Long,
    endEpochMillis: Long,
    onCreated: (eventId: String) -> Unit,
) {
    val id = this(name, budgetRaw, startEpochMillis = startEpochMillis, endEpochMillis = endEpochMillis)
    if (id != null) onCreated(id.value)
}

private fun Event.dateRangeLabel(): String =
    if (startEpochMillis == endEpochMillis) {
        PlatformDateFormatter.shortDateLabel(startEpochMillis)
    } else {
        "${PlatformDateFormatter.shortDateLabel(startEpochMillis)} — " +
            PlatformDateFormatter.shortDateLabel(endEpochMillis)
    }

private fun Event.toEditFormState(): EventCreateFormState =
    EventCreateFormState(
        name = name,
        budgetRaw = (budget.amount.valueInCents / 100).toString(),
        startLabel = PlatformDateFormatter.shortDateLabel(startEpochMillis),
        endLabel = PlatformDateFormatter.shortDateLabel(endEpochMillis),
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
    )

private fun Event.toCardState(
    progress: com.arduia.expense.feature.eventbudget.EventProgress,
    currencySymbol: String,
): EventBudgetCardState =
    EventBudgetCardState(
        id = id.value,
        title = name,
        dateRange = dateRangeLabel(),
        spentLabel = AmountInput.formatMoney(progress.spentCents, currencySymbol),
        budgetLabel = "of " + AmountInput.formatMoney(progress.budgetCents, currencySymbol),
        progress = progress.progress,
        isOverBudget = progress.isOverBudget,
        overAmountLabel =
            if (progress.isOverBudget) {
                AmountInput.formatMoney(-progress.remainingCents, currencySymbol)
            } else {
                null
            },
        overBudgetPercent = progress.overBudgetPercent,
    )

private fun Event.toDetailState(
    progress: com.arduia.expense.feature.eventbudget.EventProgress,
    linkedRecords: List<FinanceRecord>,
    categoryNames: Map<String, String>,
    currencySymbol: String,
): EventDetailUiState {
    val linkedExpenses =
        linkedRecords
            .sortedByDescending { it.recordedAtEpochMillis }
            .map { record ->
                EventLinkedExpenseUi(
                    id = record.id.value,
                    title =
                        record.note?.trim().orEmpty().ifEmpty {
                            categoryNames[record.categoryId.value] ?: expenseCategoryLabel(record.categoryId.value)
                        },
                    categoryId = record.categoryId.value,
                    categoryLabel = categoryNames[record.categoryId.value] ?: expenseCategoryLabel(record.categoryId.value),
                    amountLabel = AmountInput.formatMoney(record.money.amount.valueInCents, currencySymbol),
                )
            }
    val isReadOnly =
        com.arduia.expense.feature.eventbudget.isEventReadOnly(
            status = status,
            closedAtEpochMillis = closedAtEpochMillis,
            nowEpochMillis = System.currentTimeMillis(),
        )
    val isClosed = status == EventStatus.CLOSED
    // A closed event gets the muted, bordered inline chip (not the green "active" eyebrow) and a
    // grayed-out final summary card so its archived state reads visually distinct at a glance.
    return EventDetailUiState(
        id = id.value,
        title = name,
        subtitle = dateRangeLabel(),
        statusEyebrow = status.name.takeIf { !isClosed },
        statusInlineChip = status.name.takeIf { isClosed },
        summary =
            EventBudgetSummaryState(
                eyebrow =
                    (if (isClosed) "FINAL · " else "") +
                        if (progress.remainingCents < 0) "OVER BUDGET" else "REMAINING",
                remainingLabel = AmountInput.formatMoneySigned(progress.remainingCents, currencySymbol),
                spentLabel = AmountInput.formatMoney(progress.spentCents, currencySymbol),
                budgetLabel = AmountInput.formatMoney(progress.budgetCents, currencySymbol),
                spentCaption = "Spent",
                budgetCaption = "Budget",
                progress = progress.progress,
                tone = eventBudgetTone(progress.spentRatio),
                isFinal = isClosed,
            ),
        linkedCount = linkedExpenses.size,
        linkedExpenses = linkedExpenses,
        // Active and the 24h grace-period window both still allow new links (US-EVT-5); only
        // past the grace period is a closed event truly locked.
        showAddTagged = !isReadOnly,
        readOnly = isReadOnly,
        isClosed = isClosed,
    )
}
