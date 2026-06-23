package com.arduia.expense.domain

sealed interface RecordLink {
    data object None : RecordLink
    data class ToEvent(val eventId: EventId) : RecordLink
    data class ToDebt(val debtId: DebtId) : RecordLink
    data class ToSharedCost(val sharedCostId: SharedCostId) : RecordLink
}
