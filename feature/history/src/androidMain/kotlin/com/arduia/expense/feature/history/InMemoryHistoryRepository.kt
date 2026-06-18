package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.storage.InMemoryFinanceStore
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class InMemoryHistoryRepository(
    private val store: InMemoryFinanceStore,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) : HistoryRepository {
    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> {
        val records = store.allRecords().filter { record ->
            (filter.categoryId == null || record.categoryId == filter.categoryId) &&
                (filter.currency == null || record.currency == filter.currency) &&
                (filter.fromEpochMillis == null || record.recordedAtEpochMillis >= filter.fromEpochMillis) &&
                (filter.toEpochMillis == null || record.recordedAtEpochMillis <= filter.toEpochMillis) &&
                (filter.query.isNullOrBlank() ||
                    record.note?.contains(filter.query, ignoreCase = true) == true ||
                    record.categoryId.contains(filter.query, ignoreCase = true))
        }
        return Result.Success(records)
    }

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> {
        val anchor = ZonedDateTime.ofInstant(Instant.ofEpochMilli(anchorEpochMillis), zoneId)
        val (start, end) = periodBounds(period, anchor)
        val records = store.allRecords().filter { record ->
            record.recordedAtEpochMillis in start..end
        }
        val totalCents = records.sumOf { it.homeCurrencyAmount.valueInCents }
        return Result.Success(
            RecordSummary(
                period = period,
                totalInHomeCurrency = Amount(totalCents),
                recordCount = records.size,
            ),
        )
    }

    private fun periodBounds(period: SummaryPeriod, anchor: ZonedDateTime): Pair<Long, Long> {
        val startOfDay = anchor.toLocalDate().atStartOfDay(zoneId)
        return when (period) {
            SummaryPeriod.DAILY -> {
                val start = startOfDay.toInstant().toEpochMilli()
                val end = startOfDay.plusDays(1).minusNanos(1).toInstant().toEpochMilli()
                start to end
            }
            SummaryPeriod.WEEKLY -> {
                val start = startOfDay.minusDays(anchor.dayOfWeek.value.toLong() - 1)
                    .toInstant().toEpochMilli()
                val end = startOfDay.plusDays(7 - anchor.dayOfWeek.value.toLong())
                    .minusNanos(1).toInstant().toEpochMilli()
                start to end
            }
            SummaryPeriod.MONTHLY -> {
                val start = anchor.withDayOfMonth(1).toLocalDate().atStartOfDay(zoneId)
                    .toInstant().toEpochMilli()
                val end = anchor.withDayOfMonth(anchor.toLocalDate().lengthOfMonth())
                    .toLocalDate().atTime(23, 59, 59, 999_000_000)
                    .atZone(zoneId).toInstant().toEpochMilli()
                start to end
            }
        }
    }
}