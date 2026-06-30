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

/** Display name for this link, or `null` for [RecordLink.None] or a since-deleted target. */
fun RecordLink.tagLabel(
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
): String? = when (this) {
    RecordLink.None -> null
    is RecordLink.ToEvent -> eventNames[eventId.value]
    is RecordLink.ToDebt -> debtNames[debtId.value]
    is RecordLink.ToSharedCost -> sharedCostNames[sharedCostId.value]
}
