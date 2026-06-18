package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType

class PreviewHistoryRepository(
    private val records: List<FinanceRecord> = emptyList(),
    private val summaryTotal: Amount = Amount(0),
) : HistoryRepository {
    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        Result.Success(records)

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> =
        Result.Success(
            RecordSummary(
                period = period,
                totalInHomeCurrency = summaryTotal,
                recordCount = records.size,
            ),
        )

    companion object {
        private val sampleRecords = listOf(
            FinanceRecord(
                id = "preview-1",
                amount = Amount(1250),
                currency = CurrencyCode("USD"),
                homeCurrencyAmount = Amount(1250),
                categoryId = "food",
                type = RecordType.EXPENSE,
                note = "Lunch",
                recordedAtEpochMillis = System.currentTimeMillis(),
            ),
        )

        fun empty(): PreviewHistoryRepository = PreviewHistoryRepository()

        fun withSampleData(): PreviewHistoryRepository =
            PreviewHistoryRepository(records = sampleRecords, summaryTotal = Amount(1250))
    }
}
