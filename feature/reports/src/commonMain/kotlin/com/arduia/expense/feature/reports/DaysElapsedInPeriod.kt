package com.arduia.expense.feature.reports

/**
 * US-REP-1 Scenario 3: for the current, in-progress month the daily-average denominator is days
 * elapsed so far; for a fully elapsed past month it is the total days in that month.
 */
fun daysElapsedInPeriod(
    periodYear: Int,
    periodMonth: Int,
    nowYear: Int,
    nowMonth: Int,
    nowDayOfMonth: Int,
    daysInMonth: Int,
): Int {
    val isCurrentMonth = periodYear == nowYear && periodMonth == nowMonth
    return if (isCurrentMonth) nowDayOfMonth else daysInMonth
}
