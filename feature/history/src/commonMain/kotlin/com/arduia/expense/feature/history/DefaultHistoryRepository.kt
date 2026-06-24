package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord

class DefaultHistoryRepository(
    private val financeRecordRepository: FinanceRecordRepository,
) : HistoryRepository {

    override suspend fun getRecords(
        filter: RecordHistoryFilter,
    ): Result<List<FinanceRecord>> =
        when (val result = financeRecordRepository.getAll()) {
            is Result.Success -> {
                val filtered = result.data.filter { record ->
                    (filter.categoryId == null || record.categoryId == filter.categoryId) &&
                            (filter.currency == null || record.money.currency == filter.currency) &&
                            (filter.fromEpochMillis == null || record.recordedAtEpochMillis >= filter.fromEpochMillis) &&
                            (filter.toEpochMillis == null || record.recordedAtEpochMillis <= filter.toEpochMillis) &&
                            (filter.query.isNullOrBlank() || (record.note?.contains(filter.query!!, ignoreCase = true) ?: false))
                }
                Result.Success(filtered)
            }
            is Result.Error -> result
        }

    override suspend fun getSummary(
        period: SummaryPeriod,
        anchorEpochMillis: Long,
    ): Result<RecordSummary> = Result.Error("Not implemented")
}
