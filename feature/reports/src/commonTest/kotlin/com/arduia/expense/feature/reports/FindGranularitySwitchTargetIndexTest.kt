package com.arduia.expense.feature.reports

import kotlin.test.Test
import kotlin.test.assertEquals

private const val DAY_MS = 24 * 60 * 60 * 1000L

class FindGranularitySwitchTargetIndexTest {
    @Test
    fun invoke_returnsTheMostRecentOverlappingPeriodWithData() {
        val monthStart = 0L
        val monthEnd = 30 * DAY_MS
        // Weeks newest-first; two of the four weeks fall inside the month, only the older has data.
        val weeks =
            listOf(
                ReportsPeriodBounds(35 * DAY_MS, 42 * DAY_MS, empty = false), // outside the month
                ReportsPeriodBounds(21 * DAY_MS, 28 * DAY_MS, empty = true), // inside, empty
                ReportsPeriodBounds(14 * DAY_MS, 21 * DAY_MS, empty = false), // inside, has data
                ReportsPeriodBounds(0L, 7 * DAY_MS, empty = false), // inside, has data (older)
            )

        val index = findGranularitySwitchTargetIndex(monthStart, monthEnd, weeks)

        assertEquals(2, index)
    }

    @Test
    fun invoke_fallsBackToMostRecentOverlapWhenAllOverlappingPeriodsAreEmpty() {
        val monthStart = 0L
        val monthEnd = 30 * DAY_MS
        val weeks =
            listOf(
                ReportsPeriodBounds(21 * DAY_MS, 28 * DAY_MS, empty = true),
                ReportsPeriodBounds(14 * DAY_MS, 21 * DAY_MS, empty = true),
            )

        val index = findGranularitySwitchTargetIndex(monthStart, monthEnd, weeks)

        assertEquals(0, index)
    }

    @Test
    fun invoke_returnsMinusOneWhenNothingOverlaps() {
        val oldStart = 1_000 * DAY_MS
        val oldEnd = 1_007 * DAY_MS
        val months =
            listOf(
                ReportsPeriodBounds(0L, 30 * DAY_MS, empty = false),
            )

        val index = findGranularitySwitchTargetIndex(oldStart, oldEnd, months)

        assertEquals(-1, index)
    }

    @Test
    fun invoke_matchesTheSingleContainingMonthWhenSwitchingFromAWeek() {
        val weekStart = 40 * DAY_MS
        val weekEnd = 47 * DAY_MS
        val months =
            listOf(
                ReportsPeriodBounds(60 * DAY_MS, 90 * DAY_MS, empty = false),
                ReportsPeriodBounds(30 * DAY_MS, 60 * DAY_MS, empty = false),
                ReportsPeriodBounds(0L, 30 * DAY_MS, empty = false),
            )

        val index = findGranularitySwitchTargetIndex(weekStart, weekEnd, months)

        assertEquals(1, index)
    }
}
