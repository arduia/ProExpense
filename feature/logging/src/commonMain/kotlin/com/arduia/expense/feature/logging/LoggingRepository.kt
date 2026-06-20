package com.arduia.expense.feature.logging

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType

data class LogRecordInput(
    val amount: Amount,
    val currency: CurrencyCode,
    val homeCurrencyAmount: Amount,
    val categoryId: String,
    val type: RecordType = RecordType.EXPENSE,
    val note: String? = null,
    val recordedAtEpochMillis: Long,
    val tagType: com.arduia.expense.domain.ExpenseTagType? = null,
    val tagId: String? = null,
)

interface LoggingRepository {
    suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord>

    suspend fun getRecord(id: String): Result<FinanceRecord?>

    suspend fun updateRecord(record: FinanceRecord): Result<Unit>

    suspend fun deleteRecord(id: String): Result<Unit>
}
