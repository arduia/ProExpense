package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class DefaultHistoryRepository(
    private val financeRecordRepository: FinanceRecordRepository,
    private val timeZone: TimeZone = TimeZone.UTC,
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
    ): Result<RecordSummary> =
        when (val result = financeRecordRepository.getAll()) {
            is Result.Success -> {
                val (startMillis, endMillis) = periodBounds(period, anchorEpochMillis)
                val inPeriod = result.data.filter { record ->
                    record.type == RecordType.EXPENSE &&
                            record.recordedAtEpochMillis >= startMillis &&
                            record.recordedAtEpochMillis < endMillis
                }
                val homeCurrency = inPeriod.firstOrNull()?.homeCurrencyMoney?.currency ?: CurrencyCode("USD")
                val totalCents = inPeriod.sumOf { it.homeCurrencyMoney.amount.valueInCents }
                Result.Success(
                    RecordSummary(
                        period = period,
                        totalInHomeCurrency = Money(Amount(totalCents), homeCurrency),
                        recordCount = inPeriod.size,
                    ),
                )
            }
            is Result.Error -> result
        }

    override suspend fun getRecordsPage(
        filter: RecordHistoryFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = financeRecordRepository.getRecordsPage(
        filter = RecordPageFilter(
            categoryId = filter.categoryId,
            fromEpochMillis = filter.fromEpochMillis,
            toEpochMillis = filter.toEpochMillis,
            query = filter.query,
        ),
        cursor = cursor,
        limit = limit,
    )

    override suspend fun hasAnyRecordIn(categoryId: CategoryId): Result<Boolean> =
        financeRecordRepository.existsByCategory(categoryId)

    override fun observeChangeSignal(): Flow<RecordChangeSignal> = financeRecordRepository.observeChangeSignal()

    private fun periodBounds(period: SummaryPeriod, anchorEpochMillis: Long): Pair<Long, Long> {
        val anchorInstant = Instant.fromEpochMilliseconds(anchorEpochMillis)
        val anchorDate = anchorInstant.toLocalDateTime(timeZone).date
        return when (period) {
            SummaryPeriod.DAILY -> {
                val start = anchorDate.atStartOfDayIn(timeZone)
                val end = anchorDate.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
                start.toEpochMilliseconds() to end.toEpochMilliseconds()
            }
            SummaryPeriod.WEEKLY -> {
                val daysSinceMonday = anchorDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
                val weekStartDate = anchorDate.minus(daysSinceMonday, DateTimeUnit.DAY)
                val start = weekStartDate.atStartOfDayIn(timeZone)
                val end = weekStartDate.plus(7, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
                start.toEpochMilliseconds() to end.toEpochMilliseconds()
            }
            SummaryPeriod.MONTHLY -> {
                val monthStartDate = LocalDate(anchorDate.year, anchorDate.month, 1)
                val start = monthStartDate.atStartOfDayIn(timeZone)
                val end = monthStartDate.plus(1, DateTimeUnit.MONTH).atStartOfDayIn(timeZone)
                start.toEpochMilliseconds() to end.toEpochMilliseconds()
            }
        }
    }
}
