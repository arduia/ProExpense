package com.arduia.expense.data

import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money

interface EventRepository {
    suspend fun getAll(): Result<List<Event>>

    suspend fun getById(id: EventId): Result<Event?>

    suspend fun upsert(event: Event): Result<Unit>

    suspend fun delete(id: EventId): Result<Unit>
}

data class NewEventInput(
    val name: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val budget: Money,
    val status: EventStatus = EventStatus.ACTIVE,
)
