package com.arduia.expense.domain

enum class EventStatus {
    ACTIVE,
    CLOSED,
}

data class Event(
    val id: String,
    val name: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val budgetAmount: Amount,
    val status: EventStatus = EventStatus.ACTIVE,
)
