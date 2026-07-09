package com.arduia.expense.feature.reports

/** A period's window bounds and whether it has any data, in the same order as the period list. */
data class ReportsPeriodBounds(
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val empty: Boolean,
)

/**
 * When switching Reports granularity (e.g. Month → Week), finds the period in [newPeriods] to
 * land on so the view stays anchored to what the user was looking at, instead of always jumping
 * to the latest period with data. Prefers the most recent period overlapping the old period's
 * window that has data; if every overlapping period is empty, falls back to the most recent
 * overlapping period. Returns -1 if nothing overlaps (old period predates the new list's range).
 */
fun findGranularitySwitchTargetIndex(
    oldPeriodStartEpochMillis: Long,
    oldPeriodEndEpochMillis: Long,
    newPeriods: List<ReportsPeriodBounds>,
): Int {
    val overlapping =
        newPeriods.indices.filter { i ->
            val period = newPeriods[i]
            period.startEpochMillis < oldPeriodEndEpochMillis && period.endEpochMillis > oldPeriodStartEpochMillis
        }
    if (overlapping.isEmpty()) return -1
    return overlapping.firstOrNull { !newPeriods[it].empty } ?: overlapping.first()
}
