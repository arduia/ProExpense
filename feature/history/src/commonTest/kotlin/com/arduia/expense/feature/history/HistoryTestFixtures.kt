package com.arduia.expense.feature.history

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.FinanceRecord

internal class HistoryFakeHistoryRepository(
    private val records: List<FinanceRecord>,
) : HistoryRepository {
    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        Result.Success(records)

    override suspend fun getById(id: String): Result<FinanceRecord?> =
        Result.Success(records.firstOrNull { it.id == id })

    override suspend fun deleteRecord(id: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> {
        val total = records.sumOf { it.homeCurrencyAmount.valueInCents }
        return Result.Success(
            RecordSummary(
                period = period,
                totalInHomeCurrency = Amount(total),
                recordCount = records.size,
            ),
        )
    }
}

internal class HistoryFakeBudgetRepository(
    private val budget: Amount?,
) : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Amount?> = Result.Success(budget)

    override suspend fun setMonthlyBudget(amount: Amount?): Result<Unit> = Result.Success(Unit)
}

internal class HistoryFakeRecordDateFormatter : RecordDateFormatter {
    override fun dayKey(epochMillis: Long): Long = epochMillis

    override fun formatDayTitle(dayKey: Long): String = "May 12"

    override fun formatMonthYear(epochMillis: Long): String = "May 2025"

    override fun formatMeta(epochMillis: Long, categoryLabel: String): String = categoryLabel

    override fun nowEpochMillis(): Long = 1_700_000_000_000L

    override fun daysInMonth(epochMillis: Long): Int = 30
}

internal class HistoryFakeSecurityState(
    private val pinConfigured: Boolean,
) : com.arduia.expense.data.SecurityStateReader {
    override suspend fun hasPinConfigured(): Boolean = pinConfigured
}
