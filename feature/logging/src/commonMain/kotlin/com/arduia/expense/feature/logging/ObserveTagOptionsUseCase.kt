package com.arduia.expense.feature.logging

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Live tag-link choices (events + debts) for the expense entry tag picker. */
class ObserveTagOptionsUseCase(
    private val eventRepository: EventRepository,
    private val debtRepository: DebtRepository,
) {
    operator fun invoke(): Flow<List<TagOption>> =
        combine(eventRepository.observeAll(), debtRepository.observeAll()) { events, debts ->
            events.map { it.toTagOption() } + debts.map { it.toTagOption() }
        }

    private fun Event.toTagOption() =
        TagOption(
            id = id.value,
            kind = TagOptionKind.EVENT,
            eventName = name,
            eventStartEpochMillis = startEpochMillis,
            eventEndEpochMillis = endEpochMillis,
            eventIsClosed = status == EventStatus.CLOSED,
        )

    private fun Debt.toTagOption() =
        TagOption(
            id = id.value,
            kind = TagOptionKind.DEBT,
            debtPersonName = personName,
            debtIsOwedToMe = direction == DebtDirection.OWED_TO_ME,
            debtAmountCents = money.amount.valueInCents,
        )
}
