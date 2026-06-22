package com.arduia.expense.storage.repository

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Event
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightEventRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), EventRepository {

    private val queries get() = database.eventQueries

    override suspend fun getAll(): Result<List<Event>> = execute {
        queries.selectAllEvents().executeAsList().map { it.toDomain() }
    }

    override suspend fun getById(id: String): Result<Event?> = execute {
        queries.selectEventById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsert(event: Event): Result<Unit> = execute {
        queries.insertEvent(
            id = event.id,
            name = event.name,
            start_epoch_millis = event.startEpochMillis,
            end_epoch_millis = event.endEpochMillis,
            budget_cents = event.budgetAmount.valueInCents,
            status = event.status.name,
        )
    }

    override suspend fun delete(id: String): Result<Unit> = execute {
        queries.deleteEvent(id)
    }
}
