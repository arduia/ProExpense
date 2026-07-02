package com.arduia.expense.feature.reports

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

/**
 * Weekly counterpart to [daysElapsedInPeriod]: for the current, in-progress week the daily-average
 * denominator is days elapsed so far (inclusive of today); a fully elapsed past week is always 7.
 */
fun daysElapsedInWeek(periodStartEpochMillis: Long, periodEndEpochMillis: Long, nowEpochMillis: Long): Int {
    val isCurrentWeek = nowEpochMillis >= periodStartEpochMillis && nowEpochMillis < periodEndEpochMillis
    return if (isCurrentWeek) {
        ((nowEpochMillis - periodStartEpochMillis) / DAY_MILLIS).toInt() + 1
    } else {
        7
    }
}
