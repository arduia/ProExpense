package com.arduia.expense.ui.design

import java.util.Calendar
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformDateFormatterTest {
    private fun utcMidnight(
        year: Int,
        month: Int,
        day: Int,
    ): Long =
        Calendar
            .getInstance(TimeZone.getTimeZone("UTC"))
            .apply {
                clear()
                set(year, month, day, 0, 0, 0)
            }.timeInMillis

    @Test
    fun dayKey_withUtcZone_readsTheCalendarDateThePickerEncoded() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)

        assertEquals("2026-135", PlatformDateFormatter.dayKey(mayFifteenUtcMidnight, DateZone.Utc))
    }

    @Test
    fun dayKey_withUtcNegativeLocalDefault_misreadsPickerDateByOneDay() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)
        val originalDefault = TimeZone.getDefault()

        // Regression guard: reading a DateRangePicker's UTC-midnight millis with a device-local
        // (UTC-negative) calendar rolls the date back a day — the bug DateZone.Utc exists to avoid.
        // Callers must pass DateZone.Utc for picker-derived millis, never DeviceLocal.
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
            assertEquals("2026-134", PlatformDateFormatter.dayKey(mayFifteenUtcMidnight, DateZone.DeviceLocal))
        } finally {
            TimeZone.setDefault(originalDefault)
        }
    }

    @Test
    fun shortDateLabel_withUtcZone_matchesTheCalendarDateThePickerEncoded() {
        val mayFifteenUtcMidnight = utcMidnight(2026, Calendar.MAY, 15)

        assertEquals("May 15", PlatformDateFormatter.shortDateLabel(mayFifteenUtcMidnight, DateZone.Utc))
    }

    @Test
    fun dayKey_defaultsToDeviceLocalZone() {
        val localNoon =
            Calendar
                .getInstance()
                .apply {
                    clear()
                    set(2026, Calendar.MAY, 15, 12, 0, 0)
                }.timeInMillis

        assertEquals(
            PlatformDateFormatter.dayKey(localNoon),
            PlatformDateFormatter.dayKey(localNoon, DateZone.DeviceLocal),
        )
    }
}
