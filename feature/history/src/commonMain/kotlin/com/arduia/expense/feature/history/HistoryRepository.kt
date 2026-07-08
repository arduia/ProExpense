package com.arduia.expense.feature.history

import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import kotlinx.coroutines.flow.Flow

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

    /** Keyset-paginated, filter-pushed-down Journal page — `cursor = null` requests the first page. */
    suspend fun getRecordsPage(
        filter: RecordHistoryFilter = RecordHistoryFilter(),
        cursor: RecordPageCursor? = null,
        limit: Int,
    ): Result<List<FinanceRecord>>

    /** Whether any record is currently assigned to [categoryId] — backs the Uncategorized filter chip. */
    suspend fun hasAnyRecordIn(categoryId: CategoryId): Result<Boolean>

    /** Live (count, lastUpdatedAt) signal a paged view can watch to know when to reload. */
    fun observeChangeSignal(): Flow<RecordChangeSignal>
}
