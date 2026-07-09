package com.arduia.expense.ui.design

/**
 * iOS-readiness stub (compiles in the iOS klib gate but still throws). Implement with
 * NSDateFormatter / NSCalendar when the iosApp phase starts.
 */
actual object PlatformDateFormatter : DateFormatter {
    override fun dayLabel(
        epochMillis: Long,
        nowEpochMillis: Long,
    ): String = TODO("iOS date formatting not implemented yet — use NSDateFormatter in the iosApp phase")

    override fun shortDateLabel(
        epochMillis: Long,
        zone: DateZone,
        withYear: Boolean,
    ): String = TODO("iOS date formatting not implemented yet — use NSDateFormatter in the iosApp phase")

    override fun yearOf(
        epochMillis: Long,
        zone: DateZone,
    ): Int = TODO("iOS date formatting not implemented yet — use NSCalendar in the iosApp phase")

    override fun timeLabel(epochMillis: Long): String =
        TODO("iOS date formatting not implemented yet — use NSDateFormatter in the iosApp phase")

    override fun dayKey(
        epochMillis: Long,
        zone: DateZone,
    ): String = TODO("iOS date formatting not implemented yet — use NSCalendar in the iosApp phase")
}
