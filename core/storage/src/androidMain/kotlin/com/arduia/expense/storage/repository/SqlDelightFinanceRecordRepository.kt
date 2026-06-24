package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.FinanceRecordQueries
import com.arduia.expense.storage.mapping.tagId
import com.arduia.expense.storage.mapping.tagType
import com.arduia.expense.storage.mapping.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightFinanceRecordRepository(
    private val queries: FinanceRecordQueries,
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
            queries.insertRecord(
                id = record.id.value,
                amount_cents = record.money.amount.valueInCents,
                currency_code = record.money.currency.code,
                home_amount_cents = record.homeCurrencyMoney.amount.valueInCents,
                category_id = record.categoryId.value,
                type = record.type.name,
                note = record.note,
                recorded_at = record.recordedAtEpochMillis,
                tag_type = record.link.tagType(),
                tag_id = record.link.tagId(),
            )
            Unit
        }
    }

    override suspend fun delete(id: RecordId): Result<Unit> = withContext(dispatcher) {
        catchingResult { queries.deleteRecord(id.value); Unit }
    }

    override fun observeAll(): Flow<List<FinanceRecord>> =
        queries.selectAllRecords()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain() } }
}
