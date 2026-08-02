package com.arduia.expense.storage.sync

import com.arduia.expense.data.YearMonth
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * UTC calendar-month bucket for [epochMillis] — deterministic regardless of device timezone (see
 * docs/user_stories/sync/US-SYNC-3.md Business Rules). Mirrors the `strftime('%Y-%m', ..., 'unixepoch')`
 * SQL expression used by the finance_record sync queries, so Kotlin- and SQL-side bucketing agree.
 */
fun yearMonthOf(epochMillis: Long): YearMonth {
    val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.UTC).date
    val year = date.year.toString().padStart(YEAR_DIGITS, '0')
    val month = date.monthNumber.toString().padStart(MONTH_DIGITS, '0')
    return YearMonth("$year-$month")
}

private const val YEAR_DIGITS = 4
private const val MONTH_DIGITS = 2
