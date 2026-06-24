package com.arduia.expense.domain

sealed interface RecordLink {
    data object None : RecordLink
    data class ToEvent(val eventId: EventId) : RecordLink
    data class ToDebt(val debtId: DebtId) : RecordLink
    data class ToSharedCost(val sharedCostId: SharedCostId) : RecordLink
}

/** Stable textual form of the link, used in canonical integrity payloads. */
fun RecordLink.canonical(): String = when (this) {
    RecordLink.None -> "none"
    is RecordLink.ToEvent -> "EVENT:${eventId.value}"
    is RecordLink.ToDebt -> "DEBT:${debtId.value}"
    is RecordLink.ToSharedCost -> "SHARED_COST:${sharedCostId.value}"
}
