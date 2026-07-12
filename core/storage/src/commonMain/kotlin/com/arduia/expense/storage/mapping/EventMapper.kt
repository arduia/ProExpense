package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.db.Event_record as EventRow

internal fun EventRow.toDomain(): Event =
    Event(
        id = EventId(id),
        name = name,
        startEpochMillis = start_epoch_millis,
        endEpochMillis = end_epoch_millis,
        budget = Money(Amount(budget_cents), CurrencyCode(currency_code)),
        status = status.toEventStatusFromCode(),
        closedAtEpochMillis = closed_at_epoch_millis,
    )

/**
 * Enum codec for EventStatus — explicit stable codes (ACTIVE=0, CLOSED=1, ARCHIVED=2).
 * Never use Kotlin .ordinal as that changes when enum members are reordered.
 */
internal fun Long.toEventStatusFromCode(): EventStatus =
    when (this) {
        0L -> EventStatus.ACTIVE
        1L -> EventStatus.CLOSED
        2L -> EventStatus.ARCHIVED
        else -> error("Unknown EventStatus code: $this")
    }

internal fun EventStatus.toCode(): Long =
    when (this) {
        EventStatus.ACTIVE -> 0L
        EventStatus.CLOSED -> 1L
        EventStatus.ARCHIVED -> 2L
    }
