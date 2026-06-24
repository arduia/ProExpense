package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.Money
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.EventQueries
import com.arduia.expense.storage.mapping.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * The `event_record` table stores `budget_cents` without a currency column (design §4.1 — no
 * structural schema change). Budgets are therefore interpreted in [homeCurrency], supplied by the
 * composition root from app settings.
 */
class SqlDelightEventRepository(
    private val queries: EventQueries,
    private val homeCurrency: CurrencyCode,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : EventRepository {

    override suspend fun getAll(): Result<List<Event>> = withContext(dispatcher) {
        catchingResult { queries.selectAllEvents().executeAsList().map { it.toDomain(homeCurrency) } }
    }

    override suspend fun getById(id: EventId): Result<Event?> = withContext(dispatcher) {
        catchingResult { queries.selectEventById(id.value).executeAsOneOrNull()?.toDomain(homeCurrency) }
    }

    override suspend fun upsert(event: Event): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            // INSERT OR REPLACE rewrites the whole row, so preserve audit/cache columns this
            // method doesn't own across an edit instead of resetting them.
            val existing = queries.selectEventById(event.id.value).executeAsOneOrNull()
            queries.insertEvent(
                id = event.id.value,
                name = event.name,
                start_epoch_millis = event.startEpochMillis,
                end_epoch_millis = event.endEpochMillis,
                budget_cents = event.budget.amount.valueInCents,
                status = event.status.name,
                created_at = existing?.created_at ?: System.currentTimeMillis(),
                cached_spent_cents = existing?.cached_spent_cents ?: 0,
                cache_updated_at = existing?.cache_updated_at ?: 0,
            )
            Unit
        }
    }

    override suspend fun delete(id: EventId): Result<Unit> = withContext(dispatcher) {
        catchingResult { queries.deleteEvent(id.value); Unit }
    }

    override fun observeAll(): Flow<List<Event>> =
        queries.selectAllEvents()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain(homeCurrency) } }

    override suspend fun getSpent(id: EventId): Result<Money> = withContext(dispatcher) {
        catchingResult {
            val row = queries.selectEventById(id.value).executeAsOneOrNull()
                ?: error("No event with id ${id.value}")
            Money(Amount(row.cached_spent_cents), homeCurrency)
        }
    }
}
