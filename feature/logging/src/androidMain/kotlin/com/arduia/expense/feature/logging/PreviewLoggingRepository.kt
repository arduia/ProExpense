package com.arduia.expense.feature.logging

import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord

object PreviewLoggingRepository : LoggingRepository {
    override suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord> = Result.Success(
        FinanceRecord(
            id = "preview-record",
            amount = input.amount,
            currency = input.currency,
            homeCurrencyAmount = input.homeCurrencyAmount,
            categoryId = input.categoryId,
            type = input.type,
            note = input.note,
            recordedAtEpochMillis = input.recordedAtEpochMillis,
        ),
    )

    override suspend fun updateRecord(record: FinanceRecord): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteRecord(id: String): Result<Unit> = Result.Success(Unit)
}
