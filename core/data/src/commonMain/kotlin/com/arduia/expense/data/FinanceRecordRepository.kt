package com.arduia.expense.data

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId

interface FinanceRecordRepository {
    suspend fun getAll(): Result<List<FinanceRecord>>

    suspend fun getById(id: RecordId): Result<FinanceRecord?>

    suspend fun upsert(record: FinanceRecord): Result<Unit>

    suspend fun delete(id: RecordId): Result<Unit>
}
