package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordIntegrityVerifier
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.toCode
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.EventQueries
import com.arduia.expense.storage.db.FinanceRecordQueries
import com.arduia.expense.storage.mapping.tagId
import com.arduia.expense.storage.mapping.tagType
import com.arduia.expense.storage.mapping.toDomain
import com.arduia.expense.storage.mapping.toRecordLink
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightFinanceRecordRepository(
    private val queries: FinanceRecordQueries,
    private val eventQueries: EventQueries,
    private val integrityVerifier: RecordIntegrityVerifier = RecordIntegrityVerifier(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FinanceRecordRepository {

    override suspend fun getAll(): Result<List<FinanceRecord>> = withContext(dispatcher) {
        catchingResult { queries.selectAllRecords().executeAsList().map { it.toDomain() } }
    }

    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = withContext(dispatcher) {
        catchingResult { queries.selectRecordById(id.value).executeAsOneOrNull()?.toDomain() }
    }

    override suspend fun upsert(record: FinanceRecord): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            // Always (re)stamp so the persisted checksum matches the persisted content.
            val checksum = integrityVerifier.checksumFor(record)
            val previousLink = queries.selectRecordById(record.id.value).executeAsOneOrNull()
                ?.let { toRecordLink(it.tag_type, it.tag_id) }
            val sameCurrency = record.money.currency == record.homeCurrencyMoney.currency
            queries.transaction {
                queries.insertRecord(
                    id = record.id.value,
                    amount_cents = record.money.amount.valueInCents,
                    currency_code = record.money.currency.code,
                    home_amount_cents = if (sameCurrency) null else record.homeCurrencyMoney.amount.valueInCents,
                    category_id = record.categoryId.value,
                    type = record.type.toCode(),
                    note = record.note,
                    recorded_at = record.recordedAtEpochMillis,
                    updated_at = System.currentTimeMillis(),
                    tag_type = record.link.tagType(),
                    tag_id = record.link.tagId(),
                    integrity_algo = checksum.algorithm,
                    integrity_hash = checksum.value,
                    home_currency_code = if (sameCurrency) null else record.homeCurrencyMoney.currency.code,
                )
                // The link may have moved from one event to another — recompute both caches from
                // the source of truth rather than incrementing/decrementing, so a partial failure
                // can never leave a cache drifted.
                recomputeEventCacheIfLinked(previousLink)
                recomputeEventCacheIfLinked(record.link)
            }
            Unit
        }
    }

    override suspend fun delete(id: RecordId): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            val link = queries.selectRecordById(id.value).executeAsOneOrNull()
                ?.let { toRecordLink(it.tag_type, it.tag_id) }
            queries.transaction {
                queries.deleteRecord(id.value)
                recomputeEventCacheIfLinked(link)
            }
            Unit
        }
    }

    private fun recomputeEventCacheIfLinked(link: RecordLink?) {
        if (link !is RecordLink.ToEvent) return
        val total = queries.sumAmountByEventLink(link.eventId.value).executeAsOne()
        eventQueries.updateEventCache(total, System.currentTimeMillis(), link.eventId.value)
    }

    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = withContext(dispatcher) {
        catchingResult {
            val record = queries.selectRecordById(id.value).executeAsOneOrNull()?.toDomain()
                ?: error("No record with id ${id.value}")
            integrityVerifier.verify(record)
        }
    }

    override fun observeAll(): Flow<List<FinanceRecord>> =
        queries.selectAllRecords()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.mapNotNull { runCatching { it.toDomain() }.getOrNull() } }

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = withContext(dispatcher) {
        catchingResult {
            queries.selectRecordsPage(
                categoryId = filter.categoryId?.value,
                fromMillis = filter.fromEpochMillis,
                toMillis = filter.toEpochMillis,
                query = filter.query?.takeIf { it.isNotBlank() },
                beforeRecordedAt = cursor?.recordedAtEpochMillis,
                beforeId = cursor?.recordId?.value,
                limit = limit.toLong(),
            ).executeAsList().mapNotNull { runCatching { it.toDomain() }.getOrNull() }
        }
    }

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> = withContext(dispatcher) {
        catchingResult { queries.existsByCategory(categoryId.value).executeAsOne().found }
    }

    override fun observeChangeSignal(): Flow<RecordChangeSignal> =
        queries.countAndLastUpdate()
            .asFlow()
            .mapToOne(dispatcher)
            .map { RecordChangeSignal(count = it.total, lastUpdatedAtEpochMillis = it.lastUpdatedAt) }
}
