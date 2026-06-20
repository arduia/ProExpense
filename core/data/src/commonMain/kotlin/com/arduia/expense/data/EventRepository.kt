package com.arduia.expense.data

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus

interface EventRepository {
    suspend fun getAll(): Result<List<Event>>

    suspend fun getById(id: String): Result<Event?>

    suspend fun upsert(event: Event): Result<Unit>

    suspend fun delete(id: String): Result<Unit>
}

data class NewEventInput(
    val name: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val budgetAmount: Amount,
    val status: EventStatus = EventStatus.ACTIVE,
)
