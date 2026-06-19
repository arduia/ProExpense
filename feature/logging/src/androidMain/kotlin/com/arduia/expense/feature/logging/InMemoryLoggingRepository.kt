package com.arduia.expense.feature.logging

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.storage.InMemoryDataStore
import java.util.UUID

class InMemoryLoggingRepository(
    private val store: InMemoryDataStore,
) : LoggingRepository {
    override suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord> = try {
        val record = FinanceRecord(
            id = UUID.randomUUID().toString(),
            amount = input.amount,
            currency = input.currency,
            homeCurrencyAmount = input.homeCurrencyAmount,
            categoryId = input.categoryId,
            type = input.type,
            note = input.note,
            recordedAtEpochMillis = input.recordedAtEpochMillis,
            tagType = input.tagType,
            tagId = input.tagId,
        )
        store.insertRecord(record)
        Result.Success(record)
    } catch (error: IllegalArgumentException) {
        Result.Error(error.message ?: "Invalid record")
    }

    override suspend fun updateRecord(record: FinanceRecord): Result<Unit> = try {
        store.updateRecord(record)
        Result.Success(Unit)
    } catch (error: IllegalArgumentException) {
        Result.Error(error.message ?: "Invalid record")
    }

    override suspend fun deleteRecord(id: String): Result<Unit> {
        store.deleteRecord(id)
        return Result.Success(Unit)
    }
}

fun amountFromCents(cents: Long): Amount = Amount(cents)
