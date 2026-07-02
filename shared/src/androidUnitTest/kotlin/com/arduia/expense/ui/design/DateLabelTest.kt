package com.arduia.expense.ui.design

import java.util.Calendar
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class DateLabelTest {

    private fun utcMidnight(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance(UtcTimeZone).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.timeInMillis

    @Test
    fun dayKey_withUtcTimeZone_readsTheCalendarDateThePickerEncoded() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)

        assertEquals("2026-135", dayKey(mayFifteenUtcMidnight, UtcTimeZone))
    }

    @Test
    fun dayKey_withUtcNegativeLocalTimeZone_misreadsPickerDateByOneDay() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)
        val losAngeles = TimeZone.getTimeZone("America/Los_Angeles")

        // Regression guard: reading a DateRangePicker's UTC-midnight millis with a device-local
        // (UTC-negative) calendar rolls the date back a day — the bug UtcTimeZone exists to avoid.
        // Callers must pass UtcTimeZone for picker-derived millis, never the local default.
        assertEquals("2026-134", dayKey(mayFifteenUtcMidnight, losAngeles))
    }

    @Test
    fun shortDateLabel_withUtcTimeZone_matchesTheCalendarDateThePickerEncoded() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)

        assertEquals("May 15", shortDateLabel(mayFifteenUtcMidnight, UtcTimeZone))
    }

    @Test
    fun dayKey_defaultTimeZone_bucketsARecordedInstantByLocalCalendarDay() {
        val localNoon = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.MAY, 15, 12, 0, 0)
        }.timeInMillis

        assertEquals(dayKey(localNoon), dayKey(localNoon, TimeZone.getDefault()))
    }
}
