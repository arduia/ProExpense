package com.arduia.expense.data

import com.arduia.expense.domain.FinanceRecord

interface FinanceRecordRepository {
    suspend fun getAll(): Result<List<FinanceRecord>>

    suspend fun getById(id: String): Result<FinanceRecord?>

    suspend fun upsert(record: FinanceRecord): Result<Unit>

    suspend fun delete(id: String): Result<Unit>
}
