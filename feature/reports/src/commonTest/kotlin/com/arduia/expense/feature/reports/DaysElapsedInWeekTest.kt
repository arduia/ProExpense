package com.arduia.expense.feature.reports

import kotlin.test.Test
import kotlin.test.assertEquals

class DaysElapsedInWeekTest {
    @Test
    fun invoke_returnsElapsedDaysForCurrentWeek() {
        val days = daysElapsedInWeek(isCurrentWeek = true, daysSinceWeekStart = 2)

        assertEquals(3, days)
    }

    @Test
    fun invoke_returnsSevenForAFullyElapsedPastWeek() {
        val days = daysElapsedInWeek(isCurrentWeek = false, daysSinceWeekStart = 0)

        assertEquals(7, days)
    }

    @Test
    fun invoke_returnsSevenForAFutureWeek() {
        val days = daysElapsedInWeek(isCurrentWeek = false, daysSinceWeekStart = 0)

        assertEquals(7, days)
    }

    @Test
    fun invoke_returnsOneOnTheFirstDayOfTheCurrentWeek() {
        val days = daysElapsedInWeek(isCurrentWeek = true, daysSinceWeekStart = 0)

        assertEquals(1, days)
    }
}
