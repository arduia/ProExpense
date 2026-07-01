package com.arduia.expense.feature.reports

/**
 * US-REP-3 Scenario 2: a month with no data (typically the current, in-progress month) must never
 * be the landing state when an earlier month has data — picks the first non-empty period, in the
 * given (most-recent-first) order, falling back to index 0 if every period is empty.
 */
fun selectInitialPeriodIndex(periodEmptyFlags: List<Boolean>): Int {
    val firstNonEmpty = periodEmptyFlags.indexOfFirst { empty -> !empty }
    return if (firstNonEmpty < 0) 0 else firstNonEmpty
}
