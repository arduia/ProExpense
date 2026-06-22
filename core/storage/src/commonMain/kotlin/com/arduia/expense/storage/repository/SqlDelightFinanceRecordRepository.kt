package com.arduia.expense.storage.repository

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightFinanceRecordRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), FinanceRecordRepository {

    private val queries get() = database.financeRecordQueries

    override suspend fun getAll(): Result<List<FinanceRecord>> = execute {
        queries.selectAllRecords().executeAsList().map { it.toDomain() }
    }

    override suspend fun getById(id: String): Result<FinanceRecord?> = execute {
        queries.selectRecordById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsert(record: FinanceRecord): Result<Unit> = execute {
        queries.insertRecord(
            id = record.id,
            amount_cents = record.amount.valueInCents,
            currency_code = record.currency.code,
            home_amount_cents = record.homeCurrencyAmount.valueInCents,
            category_id = record.categoryId,
            type = record.type.name,
            note = record.note,
            recorded_at = record.recordedAtEpochMillis,
            tag_type = record.tagType?.name,
            tag_id = record.tagId,
        )
    }

    override suspend fun delete(id: String): Result<Unit> = execute {
        queries.deleteRecord(id)
    }
}
