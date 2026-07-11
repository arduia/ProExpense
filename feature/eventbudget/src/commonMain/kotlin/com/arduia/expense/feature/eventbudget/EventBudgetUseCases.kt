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
    /** Rounded percent over 100 (e.g. 105% spent -> 5); 0 when at or under budget. */
    val overBudgetPercent: Int = 0,
)

class ComputeEventProgressUseCase {
    operator fun invoke(
        event: Event,
        spent: Money?,
    ): EventProgress {
        val spentCents = spent?.amount?.valueInCents ?: 0L
        val budgetCents = event.budget.amount.valueInCents
        val progress = if (budgetCents > 0) spentCents.toFloat() / budgetCents.toFloat() else 0f
        // US-EVT-3's tiers (0-100 normal, 101-110 warning, >110 danger) need the *uncapped*
        // percent over budget — the bar-fill progress below is intentionally capped to 1f.
        val percentOfBudget = if (budgetCents > 0) (spentCents * 100f / budgetCents) else 0f
        val overBudgetPercent = (percentOfBudget - 100f).let { if (it > 0f) kotlin.math.round(it).toInt() else 0 }
        return EventProgress(
            spentCents = spentCents,
            budgetCents = budgetCents,
            remainingCents = budgetCents - spentCents,
            progress = progress.coerceIn(0f, 1f),
            isOverBudget = spentCents > budgetCents,
            overBudgetPercent = overBudgetPercent,
        )
    }
}

/** Validates and creates a new event budget. */
class CreateEventUseCase(
    private val eventRepository: EventRepository,
    private val nowEpochMillis: () -> Long,
) {
    /** Returns the created event's id, or `null` if validation failed and nothing was saved —
     *  the caller uses it to navigate straight to the new event's detail (US-EVT "create then
     *  view" flow) instead of just closing the create sheet. */
    suspend operator fun invoke(
        name: String,
        rawBudget: String,
        currencyCode: String = "USD",
        startEpochMillis: Long? = null,
        endEpochMillis: Long? = null,
    ): EventId? {
        val amount = Amount.parseOrNull(rawBudget) ?: return null
        val now = nowEpochMillis()
        val start = startEpochMillis ?: now
        val end = endEpochMillis ?: now
        if (end < start) return null
        val event =
            Event(
                id = EventId(newEventId(name, now)),
                name = name,
                startEpochMillis = start,
                endEpochMillis = end,
                budget = Money(amount, CurrencyCode(currencyCode)),
                status = EventStatus.ACTIVE,
            )
        eventRepository.upsert(event)
        return event.id
    }

    private fun newEventId(
        name: String,
        now: Long,
    ): String = name.trim().lowercase() + "-" + now
}

/** Validates and applies edits to an existing event's name, dates, and budget. */
class UpdateEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(
        existing: Event,
        name: String,
        rawBudget: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ): Boolean {
        val amount = Amount.parseOrNull(rawBudget) ?: return false
        if (amount.valueInCents <= 0) return false
        if (endEpochMillis < startEpochMillis) return false
        eventRepository.upsert(
            existing.copy(
                name = name,
                budget = Money(amount, existing.budget.currency),
                startEpochMillis = startEpochMillis,
                endEpochMillis = endEpochMillis,
            ),
        )
        return true
    }
}

/** Manually closes an active event; closing never happens automatically from the end date. */
class CloseEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(existing: Event): Boolean {
        if (existing.status == EventStatus.CLOSED) return false
        eventRepository.upsert(existing.copy(status = EventStatus.CLOSED))
        return true
    }
}

private const val CLOSED_EVENT_GRACE_PERIOD_MILLIS = 24L * 60 * 60 * 1000

/**
 * US-EVT-5 lifecycle: Active is always editable; Closed < 24h is a grace period (still editable,
 * no new links); Closed > 24h locks permanently. An event with no recorded closedAtEpochMillis
 * (e.g. closed before this field existed) is treated as past the grace period.
 */
fun isEventReadOnly(
    status: EventStatus,
    closedAtEpochMillis: Long?,
    nowEpochMillis: Long,
): Boolean {
    if (status != EventStatus.CLOSED) return false
    val closedAt = closedAtEpochMillis ?: return true
    return nowEpochMillis - closedAt > CLOSED_EVENT_GRACE_PERIOD_MILLIS
}
