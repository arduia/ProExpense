package com.arduia.expense.feature.reports

/**
 * Weekly counterpart to [daysElapsedInPeriod]: for the current, in-progress week the daily-average
 * denominator is days elapsed so far (inclusive of today); a fully elapsed past week is always 7.
 *
 * [daysSinceWeekStart] must be a calendar-day count (e.g. via `Calendar.DAY_OF_YEAR` differencing),
 * not a fixed-24h-millis division — the latter is wrong by one on a DST transition day.
 */
fun daysElapsedInWeek(
    isCurrentWeek: Boolean,
    daysSinceWeekStart: Int,
): Int = if (isCurrentWeek) daysSinceWeekStart + 1 else 7
