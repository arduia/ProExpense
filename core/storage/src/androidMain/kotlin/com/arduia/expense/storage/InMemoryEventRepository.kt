package com.arduia.expense.storage

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Event

class InMemoryEventRepository(
    private val store: InMemoryDataStore,
) : EventRepository {
    override suspend fun getAll(): Result<List<Event>> = Result.Success(store.allEvents())

    override suspend fun getById(id: String): Result<Event?> =
        Result.Success(store.allEvents().firstOrNull { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> {
        val existing = store.allEvents().any { it.id == event.id }
        if (existing) store.updateEvent(event) else store.insertEvent(event)
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.deleteEvent(id)
        return Result.Success(Unit)
    }
}
