package com.arduia.expense.feature.eventbudget.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.eventbudget.CreateEventUseCase
import com.arduia.expense.feature.eventbudget.ui.EventsFlow
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.HomeNavTab
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
    ) {
        val scope = rememberCoroutineScope()
        val eventRepository: EventRepository = koinInject()
        val computeProgress: ComputeEventProgressUseCase = koinInject()
        val createEvent: CreateEventUseCase = koinInject()

        var spentByEvent by remember { mutableStateOf<Map<String, Money>>(emptyMap()) }

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

        val cards = events.map { it.toCardState(computeProgress(it, spentByEvent[it.id.value])) }
        val details = events.associate { it.id.value to it.toDetailState(computeProgress(it, spentByEvent[it.id.value])) }

        EventsFlow(
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            events = cards,
            eventDetails = details,
            onCreateEvent = { name, budgetRaw ->
                scope.launch { createEvent(name, budgetRaw) }
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

private fun Event.toCardState(progress: com.arduia.expense.feature.eventbudget.EventProgress): EventBudgetCardState =
    EventBudgetCardState(
        id = id.value,
        title = name,
        dateRange = dateRangeLabel(),
        spentLabel = moneyLabel(progress.spentCents),
        budgetLabel = "of " + moneyLabel(progress.budgetCents),
        progress = progress.progress,
        isOverBudget = progress.isOverBudget,
    )

private fun Event.toDetailState(progress: com.arduia.expense.feature.eventbudget.EventProgress): EventDetailUiState =
    EventDetailUiState(
        id = id.value,
        title = name,
        subtitle = dateRangeLabel(),
        statusEyebrow = status.name,
        summary = EventBudgetSummaryState(
            eyebrow = if (progress.remainingCents < 0) "OVER BUDGET" else "REMAINING",
            remainingLabel = moneyLabel(progress.remainingCents),
            spentLabel = moneyLabel(progress.spentCents),
            budgetLabel = moneyLabel(progress.budgetCents),
            spentCaption = "Spent",
            budgetCaption = "Budget",
            progress = progress.progress,
            tone = eventBudgetTone(progress.progress),
        ),
        showAddTagged = status == EventStatus.ACTIVE,
        readOnly = status == EventStatus.CLOSED,
    )

private fun moneyLabel(valueInCents: Long): String {
    val sign = if (valueInCents < 0) "-" else ""
    return sign + "$" + AmountInput.formatDisplay(
        String.format(Locale.US, "%.2f", abs(valueInCents) / 100.0),
    )
}
