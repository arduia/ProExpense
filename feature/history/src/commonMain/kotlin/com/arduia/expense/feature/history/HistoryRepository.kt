package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money

data class RecordHistoryFilter(
    val categoryId: CategoryId? = null,
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
    val totalInHomeCurrency: Money,
    val recordCount: Int,
)

interface HistoryRepository {
    suspend fun getRecords(filter: RecordHistoryFilter = RecordHistoryFilter()): Result<List<FinanceRecord>>

    suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary>
}
