package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.db.Event_record as EventRow

internal fun EventRow.toDomain(homeCurrency: CurrencyCode): Event =
    Event(
        id = EventId(id),
        name = name,
        startEpochMillis = start_epoch_millis,
        endEpochMillis = end_epoch_millis,
        budget = Money(Amount(budget_cents), homeCurrency),
        status = EventStatus.valueOf(status),
    )
