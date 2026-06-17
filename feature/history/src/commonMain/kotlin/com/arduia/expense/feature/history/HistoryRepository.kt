package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord

data class RecordHistoryFilter(
    val categoryId: String? = null,
    val currency: CurrencyCode? = null,
    val fromEpochMillis: Long? = null,
    val toEpochMillis: Long? = null,
    val query: String? = null,
)

enum class SummaryPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
}

data class RecordSummary(
    val period: SummaryPeriod,
    val totalInHomeCurrency: Amount,
    val recordCount: Int,
)

interface HistoryRepository {
    suspend fun getRecords(filter: RecordHistoryFilter = RecordHistoryFilter()): Result<List<FinanceRecord>>

    suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary>
}
