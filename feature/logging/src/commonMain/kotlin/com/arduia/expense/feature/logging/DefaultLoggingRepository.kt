package com.arduia.expense.feature.logging

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.shared.Identifiers

/**
 * Thin composition over the [FinanceRecordRepository] contract (design §3.1): it owns id generation
 * and input→domain mapping, and has zero platform/storage dependency. The real persistence lives in
 * the injected `core:data` implementation.
 */
class DefaultLoggingRepository(
    private val financeRecordRepository: FinanceRecordRepository,
    private val idGenerator: () -> String = Identifiers::newId,
) : LoggingRepository {

    override suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord> {
        val record = FinanceRecord(
            id = RecordId(idGenerator()),
            money = input.money,
            homeCurrencyMoney = input.homeCurrencyMoney,
            categoryId = input.categoryId,
            type = input.type,
            note = input.note,
            recordedAtEpochMillis = input.recordedAtEpochMillis,
            link = input.link,
        )
        return when (val result = financeRecordRepository.upsert(record)) {
            is Result.Success -> Result.Success(record)
            is Result.Error -> result
        }
    }

    override suspend fun updateRecord(record: FinanceRecord): Result<Unit> =
        financeRecordRepository.upsert(record)

    override suspend fun deleteRecord(id: RecordId): Result<Unit> =
        financeRecordRepository.delete(id)
}
