package com.arduia.expense.feature.reports

import kotlin.test.Test
import kotlin.test.assertEquals

class DaysElapsedInPeriodTest {

    @Test
    fun invoke_returnsElapsedDaysForCurrentMonth() {
        val days = daysElapsedInPeriod(
            periodYear = 2026,
            periodMonth = 4,
            nowYear = 2026,
            nowMonth = 4,
            nowDayOfMonth = 18,
            daysInMonth = 31,
        )

        assertEquals(18, days)
    }

    @Test
    fun invoke_returnsFullDaysInMonthForPastMonth() {
        val days = daysElapsedInPeriod(
            periodYear = 2026,
            periodMonth = 3,
            nowYear = 2026,
            nowMonth = 4,
            nowDayOfMonth = 18,
            daysInMonth = 30,
        )

        assertEquals(30, days)
    }

    @Test
    fun invoke_returnsFullDaysInMonthForSameMonthDifferentYear() {
        val days = daysElapsedInPeriod(
            periodYear = 2025,
            periodMonth = 4,
            nowYear = 2026,
            nowMonth = 4,
            nowDayOfMonth = 18,
            daysInMonth = 31,
        )

        assertEquals(31, days)
    }
}
