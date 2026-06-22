package com.arduia.expense.ui.events

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.eventBudgetTone
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.feature.eventbudget.ui.preview.EventLinkedExpenseUi
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

data class EventsScreenState(
    val cards: List<EventBudgetCardState> = emptyList(),
    val details: Map<String, EventDetailUiState> = emptyMap(),
)

/**
 * Read/write model for the Events (Event Budget) tab. Each event's spend is the sum of the finance
 * records tagged to it (tagType = EVENT), so the cards, detail summaries and linked-expense lists
 * are derived live from the encrypted store in the home currency. Create persists a new [Event];
 * the stubbed date pickers default to a two-week window from today.
 */
class EventsViewModel(
    private val eventRepository: EventRepository,
    private val financeRepository: FinanceRecordRepository,
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val idGenerator: () -> String = { java.util.UUID.randomUUID().toString() },
) {
    private val _state = MutableStateFlow(EventsScreenState())
    val state: StateFlow<EventsScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    fun create(name: String, budgetRaw: String) {
        scope.launch {
            val now = clock()
            val budgetCents = (AmountInput.numericValue(budgetRaw) ?: 0.0)
                .times(100).roundToLong().coerceIn(0L, Amount.MAX_VALUE_IN_CENTS)
            eventRepository.upsert(
                Event(
                    id = idGenerator(),
                    name = name.trim(),
                    startEpochMillis = now,
                    endEpochMillis = now + TWO_WEEKS_MILLIS,
                    budgetAmount = Amount(budgetCents),
                    status = EventStatus.ACTIVE,
                ),
            )
            reload()
        }
    }

    private suspend fun reload() {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val events = eventRepository.getAll().getOrNull().orEmpty()
        val byEvent = financeRepository.getAll().getOrNull().orEmpty()
            .filter { it.tagType == ExpenseTagType.EVENT && it.tagId != null }
            .groupBy { it.tagId!! }

        val cards = events.map { it.toCard(byEvent[it.id].orEmpty(), currency) }
        val details = events.associate { it.id to it.toDetail(byEvent[it.id].orEmpty(), currency) }

        _state.value = EventsScreenState(cards = cards, details = details)
    }

    private fun Event.spentCents(records: List<FinanceRecord>): Long =
        records.sumOf { it.homeCurrencyAmount.valueInCents }

    private fun Event.toCard(records: List<FinanceRecord>, currency: String): EventBudgetCardState {
        val spent = spentCents(records)
        val budget = budgetAmount.valueInCents
        val over = spent > budget && budget > 0
        return EventBudgetCardState(
            id = id,
            title = name,
            dateRange = DateLabels.eventRange(startEpochMillis, endEpochMillis),
            spentLabel = MoneyFormatter.format(spent, currency),
            budgetLabel = if (budget > 0) "of ${MoneyFormatter.format(budget, currency)}" else "",
            progress = ratio(spent, budget),
            isOverBudget = over,
            overAmountLabel = if (over) "over" else null,
        )
    }

    private fun Event.toDetail(records: List<FinanceRecord>, currency: String): EventDetailUiState {
        val spent = spentCents(records)
        val budget = budgetAmount.valueInCents
        val remaining = budget - spent
        val over = spent > budget && budget > 0
        val closed = status == EventStatus.CLOSED
        val ratio = ratio(spent, budget)

        return EventDetailUiState(
            id = id,
            title = name,
            subtitle = "${DateLabels.eventRange(startEpochMillis, endEpochMillis)} · ${if (closed) "archived" else "active"}",
            statusEyebrow = if (closed) null else status.name,
            statusInlineChip = if (closed) "CLOSED" else null,
            summary = EventBudgetSummaryState(
                eyebrow = when {
                    closed -> "FINAL · REMAINING"
                    over -> "OVER BUDGET"
                    else -> "REMAINING"
                },
                remainingLabel = MoneyFormatter.format(remaining, currency),
                spentLabel = MoneyFormatter.format(spent, currency),
                budgetLabel = if (budget > 0) MoneyFormatter.format(budget, currency) else null,
                spentCaption = "Spent",
                budgetCaption = "Budget",
                progress = ratio,
                tone = eventBudgetTone(if (budget > 0) spent.toFloat() / budget.toFloat() else 0f),
                overNoticeLabel = if (over) {
                    "Over budget by ${MoneyFormatter.format(spent - budget, currency)}"
                } else {
                    null
                },
                isFinal = closed,
            ),
            linkedCount = records.size,
            linkedExpenses = records
                .sortedByDescending { it.recordedAtEpochMillis }
                .map { it.toLinked(currency) },
            showAddTagged = !closed,
            readOnly = closed,
        )
    }

    private fun FinanceRecord.toLinked(currency: String): EventLinkedExpenseUi {
        val label = expenseCategoryLabel(categoryId)
        return EventLinkedExpenseUi(
            id = id,
            title = note?.takeIf { it.isNotBlank() } ?: label,
            categoryId = categoryId,
            categoryLabel = label,
            amountLabel = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
        )
    }

    private fun ratio(spent: Long, budget: Long): Float =
        if (budget > 0) (spent.toFloat() / budget.toFloat()).coerceIn(0f, 1f) else if (spent > 0) 1f else 0f

    private companion object {
        const val TWO_WEEKS_MILLIS = 14L * 24 * 60 * 60 * 1000
    }
}
