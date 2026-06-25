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
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
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
import kotlin.math.roundToLong
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface EventBudgetFeatureEntry {
    @Composable
    fun EventsTab(
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class EventBudgetFeatureEntryImpl : EventBudgetFeatureEntry {
    @Composable
    override fun EventsTab(
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val eventRepository: EventRepository = koinInject()

        val events by eventRepository.observeAll().collectAsState(emptyList())
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

        val cards = events.map { it.toCardState(spentByEvent[it.id.value]) }
        val details = events.associate { it.id.value to it.toDetailState(spentByEvent[it.id.value]) }

        EventsFlow(
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            events = cards,
            eventDetails = details,
            onCreateEvent = { name, budgetRaw ->
                scope.launch {
                    val budgetValue = AmountInput.numericValue(budgetRaw) ?: 0.0
                    val now = System.currentTimeMillis()
                    val event = Event(
                        id = EventId(newEventId(name)),
                        name = name,
                        startEpochMillis = now,
                        endEpochMillis = now,
                        budget = Money(Amount((budgetValue * 100).roundToLong()), CurrencyCode("USD")),
                        status = EventStatus.ACTIVE,
                    )
                    eventRepository.upsert(event)
                }
            },
            modifier = modifier,
        )
    }
}

object EventBudgetFeatureUi : EventBudgetFeatureEntry by EventBudgetFeatureEntryImpl()

private fun newEventId(name: String): String =
    name.trim().lowercase(Locale.US).replace(" ", "-") + "-" + System.currentTimeMillis()

private fun Event.dateRangeLabel(): String =
    if (startEpochMillis == endEpochMillis) {
        shortDateLabel(startEpochMillis)
    } else {
        "${shortDateLabel(startEpochMillis)} — ${shortDateLabel(endEpochMillis)}"
    }

private fun Event.toCardState(spent: Money?): EventBudgetCardState {
    val spentCents = spent?.amount?.valueInCents ?: 0L
    val budgetCents = budget.amount.valueInCents
    val progress = if (budgetCents > 0) spentCents.toFloat() / budgetCents.toFloat() else 0f
    return EventBudgetCardState(
        id = id.value,
        title = name,
        dateRange = dateRangeLabel(),
        spentLabel = moneyLabel(spentCents),
        budgetLabel = "of " + moneyLabel(budgetCents),
        progress = progress.coerceIn(0f, 1f),
        isOverBudget = spentCents > budgetCents,
    )
}

private fun Event.toDetailState(spent: Money?): EventDetailUiState {
    val spentCents = spent?.amount?.valueInCents ?: 0L
    val budgetCents = budget.amount.valueInCents
    val remainingCents = budgetCents - spentCents
    val progress = if (budgetCents > 0) spentCents.toFloat() / budgetCents.toFloat() else 0f
    return EventDetailUiState(
        id = id.value,
        title = name,
        subtitle = dateRangeLabel(),
        statusEyebrow = status.name,
        summary = EventBudgetSummaryState(
            eyebrow = if (remainingCents < 0) "OVER BUDGET" else "REMAINING",
            remainingLabel = moneyLabel(remainingCents),
            spentLabel = moneyLabel(spentCents),
            budgetLabel = moneyLabel(budgetCents),
            spentCaption = "Spent",
            budgetCaption = "Budget",
            progress = progress.coerceIn(0f, 1f),
            tone = eventBudgetTone(progress),
        ),
        showAddTagged = status == EventStatus.ACTIVE,
        readOnly = status == EventStatus.CLOSED,
    )
}

private fun moneyLabel(valueInCents: Long): String {
    val sign = if (valueInCents < 0) "-" else ""
    return sign + "$" + AmountInput.formatDisplay(
        String.format(Locale.US, "%.2f", abs(valueInCents) / 100.0),
    )
}
