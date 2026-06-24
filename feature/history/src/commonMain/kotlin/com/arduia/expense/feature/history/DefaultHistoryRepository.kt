package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * Read-only history view composed over the [FinanceRecordRepository] contract (design §3.1): it
 * filters records and aggregates spending over calendar periods. [homeCurrencyProvider] supplies the
 * currency for summaries (so an empty period still reports a zero total in the right currency).
 */
class DefaultHistoryRepository(
    private val financeRecordRepository: FinanceRecordRepository,
    private val homeCurrencyProvider: suspend () -> CurrencyCode,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : HistoryRepository {

    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        when (val result = financeRecordRepository.getAll()) {
            is Result.Success -> Result.Success(result.data.filter { it.matches(filter) })
            is Result.Error -> result
        }

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> {
        val records = when (val result = financeRecordRepository.getAll()) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }
        val window = windowFor(period, anchorEpochMillis)
        val currency = homeCurrencyProvider()
        // Spending summary: expenses only (backs budget/Home views).
        val inWindow = records.filter {
            it.type == RecordType.EXPENSE && it.recordedAtEpochMillis in window
        }
        val totalCents = inWindow.sumOf { it.homeCurrencyMoney.amount.valueInCents }
        return Result.Success(
            RecordSummary(
                period = period,
                totalInHomeCurrency = Money(Amount(totalCents), currency),
                recordCount = inWindow.size,
            ),
        )
    }

    private fun FinanceRecord.matches(filter: RecordHistoryFilter): Boolean {
        filter.categoryId?.let { if (categoryId != it) return false }
        filter.currency?.let { if (money.currency != it) return false }
        filter.fromEpochMillis?.let { if (recordedAtEpochMillis < it) return false }
        filter.toEpochMillis?.let { if (recordedAtEpochMillis > it) return false }
        filter.query?.takeIf { it.isNotBlank() }?.let { q ->
            if (note?.contains(q, ignoreCase = true) != true) return false
        }
        return true
    }

    /** Half-open [start, end) epoch-millis range for the calendar period containing the anchor. */
    private fun windowFor(period: SummaryPeriod, anchorEpochMillis: Long): LongRange {
        val anchorDate = Instant.fromEpochMilliseconds(anchorEpochMillis)
            .toLocalDateTime(timeZone).date
        val start: LocalDate
        val endExclusive: LocalDate
        when (period) {
            SummaryPeriod.DAILY -> {
                start = anchorDate
                endExclusive = anchorDate.plus(1, DateTimeUnit.DAY)
            }
            SummaryPeriod.WEEKLY -> {
                start = anchorDate.minus(anchorDate.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber, DateTimeUnit.DAY)
                endExclusive = start.plus(7, DateTimeUnit.DAY)
            }
            SummaryPeriod.MONTHLY -> {
                start = LocalDate(anchorDate.year, anchorDate.monthNumber, 1)
                endExclusive = start.plus(1, DateTimeUnit.MONTH)
            }
        }
        val startMs = start.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val endMs = endExclusive.atStartOfDayIn(timeZone).toEpochMilliseconds()
        return startMs until endMs
    }
}
