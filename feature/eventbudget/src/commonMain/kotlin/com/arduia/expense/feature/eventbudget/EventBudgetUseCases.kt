package com.arduia.expense.feature.eventbudget

import com.arduia.expense.data.EventRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money

/** Portable budget-vs-spend math for a single event (design plan §EventBudgetViewModel). */
data class EventProgress(
    val spentCents: Long,
    val budgetCents: Long,
    val remainingCents: Long,
    val progress: Float,
    val isOverBudget: Boolean,
)

class ComputeEventProgressUseCase {
    operator fun invoke(event: Event, spent: Money?): EventProgress {
        val spentCents = spent?.amount?.valueInCents ?: 0L
        val budgetCents = event.budget.amount.valueInCents
        val progress = if (budgetCents > 0) spentCents.toFloat() / budgetCents.toFloat() else 0f
        return EventProgress(
            spentCents = spentCents,
            budgetCents = budgetCents,
            remainingCents = budgetCents - spentCents,
            progress = progress.coerceIn(0f, 1f),
            isOverBudget = spentCents > budgetCents,
        )
    }
}

/** Validates and creates a new event budget. */
class CreateEventUseCase(
    private val eventRepository: EventRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(name: String, rawBudget: String, currencyCode: String = "USD"): Boolean {
        val amount = Amount.parseOrNull(rawBudget) ?: return false
        val now = nowEpochMillis()
        val event = Event(
            id = EventId(newEventId(name, now)),
            name = name,
            startEpochMillis = now,
            endEpochMillis = now,
            budget = Money(amount, CurrencyCode(currencyCode)),
            status = EventStatus.ACTIVE,
        )
        eventRepository.upsert(event)
        return true
    }

    private fun newEventId(name: String, now: Long): String =
        name.trim().lowercase() + "-" + now
}
