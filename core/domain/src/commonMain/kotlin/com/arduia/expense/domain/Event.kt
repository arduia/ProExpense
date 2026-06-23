package com.arduia.expense.domain

enum class EventStatus {
    ACTIVE,
    CLOSED,
}

data class Event(
    val id: EventId,
    val name: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val budget: Money,
    val status: EventStatus = EventStatus.ACTIVE,
) {
    init {
        require(name.isNotBlank()) { "Event name must not be blank" }
        require(endEpochMillis >= startEpochMillis) {
            "Event endEpochMillis must not be before startEpochMillis"
        }
    }
}
