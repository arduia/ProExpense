package com.arduia.expense.feature.eventbudget.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.feature.eventbudget.CloseEventUseCase
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.eventbudget.CreateEventUseCase
import com.arduia.expense.feature.eventbudget.UpdateEventUseCase
import com.arduia.expense.feature.eventbudget.ui.EventsFlow
import com.arduia.expense.feature.eventbudget.ui.preview.EventCreateFormState
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.feature.eventbudget.ui.preview.EventLinkedExpenseUi
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.design.eventBudgetTone
import com.arduia.expense.ui.design.shortDateLabel
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface EventBudgetFeatureEntry {
    @Composable
    fun EventsTab(
        events: List<Event>,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier = Modifier,
        initialSelectedEventId: String? = null,
        onAddTaggedExpense: (eventId: String) -> Unit = { onAddClick() },
        homeCurrencySymbol: String = "$",
    )
}

internal class EventBudgetFeatureEntryImpl : EventBudgetFeatureEntry {
    @Composable
    override fun EventsTab(
        events: List<Event>,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier,
        initialSelectedEventId: String?,
        onAddTaggedExpense: (eventId: String) -> Unit,
        homeCurrencySymbol: String,
    ) {
        val scope = rememberCoroutineScope()
        val eventRepository: EventRepository = koinInject()
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val categoryRepository: CategoryRepository = koinInject()
        val computeProgress: ComputeEventProgressUseCase = koinInject()
        val createEvent: CreateEventUseCase = koinInject()
        val updateEvent: UpdateEventUseCase = koinInject()
        val closeEvent: CloseEventUseCase = koinInject()

        var spentByEvent by remember { mutableStateOf<Map<String, Money>>(emptyMap()) }
        val records by financeRecordRepository.observeAll().collectAsState(emptyList())
        var categoryNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(events) {
            val spent = mutableMapOf<String, Money>()
            events.forEach { event ->
                when (val result = eventRepository.getSpent(event.id)) {
                    is Result.Success -> spent[event.id.value] = result.data
                    is Result.Error -> Unit
                }
            }
            spentByEvent = spent
        }

        LaunchedEffect(Unit) {
            when (val result = categoryRepository.getAll()) {
                is Result.Success -> categoryNames = result.data.associate { it.id.value to it.name }
                is Result.Error -> Unit
            }
        }

        val linkedByEvent = remember(records) {
            records
                .filter { it.link is RecordLink.ToEvent }
                .groupBy { (it.link as RecordLink.ToEvent).eventId.value }
        }

        val cards = events.map { it.toCardState(computeProgress(it, spentByEvent[it.id.value]), homeCurrencySymbol) }
        val details = events.associate { event ->
            event.id.value to event.toDetailState(
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
            eventDetails = details,
            eventEditForms = editForms,
            onCreateEvent = { name, budgetRaw, startEpochMillis, endEpochMillis ->
                scope.launch { createEvent(name, budgetRaw, startEpochMillis = startEpochMillis, endEpochMillis = endEpochMillis) }
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
            initialSelectedEventId = initialSelectedEventId,
            onAddTaggedExpense = onAddTaggedExpense,
            modifier = modifier,
        )
    }
}

object EventBudgetFeatureUi : EventBudgetFeatureEntry by EventBudgetFeatureEntryImpl()

private fun Event.dateRangeLabel(): String =
    if (startEpochMillis == endEpochMillis) {
        shortDateLabel(startEpochMillis)
    } else {
        "${shortDateLabel(startEpochMillis)} — ${shortDateLabel(endEpochMillis)}"
    }

private fun Event.toEditFormState(): EventCreateFormState =
    EventCreateFormState(
        name = name,
        budgetRaw = (budget.amount.valueInCents / 100).toString(),
        startLabel = shortDateLabel(startEpochMillis),
        endLabel = shortDateLabel(endEpochMillis),
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
        spentLabel = moneyLabel(progress.spentCents, currencySymbol),
        budgetLabel = "of " + moneyLabel(progress.budgetCents, currencySymbol),
        progress = progress.progress,
        isOverBudget = progress.isOverBudget,
        overAmountLabel = if (progress.isOverBudget) {
            moneyLabel(-progress.remainingCents, currencySymbol)
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
    val linkedExpenses = linkedRecords
        .sortedByDescending { it.recordedAtEpochMillis }
        .map { record ->
            EventLinkedExpenseUi(
                id = record.id.value,
                title = record.note?.trim().orEmpty().ifEmpty {
                    categoryNames[record.categoryId.value] ?: expenseCategoryLabel(record.categoryId.value)
                },
                categoryId = record.categoryId.value,
                categoryLabel = categoryNames[record.categoryId.value] ?: expenseCategoryLabel(record.categoryId.value),
                amountLabel = moneyLabel(record.money.amount.valueInCents, currencySymbol),
            )
        }
    return EventDetailUiState(
        id = id.value,
        title = name,
        subtitle = dateRangeLabel(),
        statusEyebrow = status.name,
        summary = EventBudgetSummaryState(
            eyebrow = if (progress.remainingCents < 0) "OVER BUDGET" else "REMAINING",
            remainingLabel = moneyLabel(progress.remainingCents, currencySymbol),
            spentLabel = moneyLabel(progress.spentCents, currencySymbol),
            budgetLabel = moneyLabel(progress.budgetCents, currencySymbol),
            spentCaption = "Spent",
            budgetCaption = "Budget",
            progress = progress.progress,
            tone = eventBudgetTone(progress.progress),
        ),
        linkedCount = linkedExpenses.size,
        linkedExpenses = linkedExpenses,
        showAddTagged = status == EventStatus.ACTIVE,
        readOnly = status == EventStatus.CLOSED,
    )
}

private fun moneyLabel(valueInCents: Long, currencySymbol: String): String {
    val sign = if (valueInCents < 0) "-" else ""
    return sign + currencySymbol + AmountInput.formatDisplay(
        String.format(Locale.US, "%.2f", abs(valueInCents) / 100.0),
    )
}
