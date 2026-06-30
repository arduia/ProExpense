package com.arduia.expense.feature.logging

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType

data class LogRecordInput(
    val money: Money,
    val homeCurrencyMoney: Money,
    val categoryId: CategoryId,
    val type: RecordType = RecordType.EXPENSE,
    val note: String? = null,
    val recordedAtEpochMillis: Long,
    val link: RecordLink = RecordLink.None,
)

interface LoggingRepository {
    suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord>

    suspend fun updateRecord(record: FinanceRecord): Result<Unit>

    suspend fun deleteRecord(id: RecordId): Result<Unit>
}
