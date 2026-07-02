package com.arduia.expense.feature.reports

import kotlin.test.Test
import kotlin.test.assertEquals

private const val DAY_MS = 24 * 60 * 60 * 1000L

class DaysElapsedInWeekTest {

    @Test
    fun invoke_returnsElapsedDaysForCurrentWeek() {
        val start = 1_000_000_000_000L
        val end = start + 7 * DAY_MS
        val now = start + 2 * DAY_MS // third day of the week

        val days = daysElapsedInWeek(start, end, now)

        assertEquals(3, days)
    }

    @Test
    fun invoke_returnsSevenForAFullyElapsedPastWeek() {
        val start = 1_000_000_000_000L
        val end = start + 7 * DAY_MS
        val now = end + 5 * DAY_MS // well after the week ended

        val days = daysElapsedInWeek(start, end, now)

        assertEquals(7, days)
    }

    @Test
    fun invoke_returnsSevenForAFutureWeek() {
        val start = 1_000_000_000_000L
        val end = start + 7 * DAY_MS
        val now = start - DAY_MS // before the week starts

        val days = daysElapsedInWeek(start, end, now)

        assertEquals(7, days)
    }

    @Test
    fun invoke_returnsOneOnTheFirstDayOfTheCurrentWeek() {
        val start = 1_000_000_000_000L
        val end = start + 7 * DAY_MS

        val days = daysElapsedInWeek(start, end, start)

        assertEquals(1, days)
    }
}
