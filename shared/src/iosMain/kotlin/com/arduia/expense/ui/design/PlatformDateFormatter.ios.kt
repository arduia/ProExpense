package com.arduia.expense.ui.design

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.timeZoneWithAbbreviation

private const val MILLIS_PER_SECOND = 1000.0

/** Widths of the "yyyy-ddd" bucket key parts — must match the Android actual's "%04d-%03d". */
private const val YEAR_DIGITS = 4
private const val DAY_OF_YEAR_DIGITS = 3

/** Fixed locale so labels match the Android actual's `Locale.US` output character for character. */
private val posixLocale = NSLocale("en_US_POSIX")

private fun dateOf(epochMillis: Long): NSDate = NSDate.dateWithTimeIntervalSince1970(epochMillis / MILLIS_PER_SECOND)

private fun DateZone.toNsTimeZone(): NSTimeZone =
    when (this) {
        DateZone.DeviceLocal -> NSTimeZone.localTimeZone
        DateZone.Utc -> NSTimeZone.timeZoneWithAbbreviation("UTC") ?: NSTimeZone.localTimeZone
    }

private fun calendarIn(zone: NSTimeZone): NSCalendar =
    NSCalendar.currentCalendar.also {
        it.timeZone = zone
        it.locale = posixLocale
    }

private fun format(
    epochMillis: Long,
    pattern: String,
    zone: NSTimeZone = NSTimeZone.localTimeZone,
): String {
    val formatter = NSDateFormatter()
    formatter.locale = posixLocale
    formatter.timeZone = zone
    formatter.dateFormat = pattern
    return formatter.stringFromDate(dateOf(epochMillis))
}

actual object PlatformDateFormatter : DateFormatter {
    override fun dayLabel(
        epochMillis: Long,
        nowEpochMillis: Long,
    ): String {
        val calendar = calendarIn(NSTimeZone.localTimeZone)
        val target = dateOf(epochMillis)
        val now = dateOf(nowEpochMillis)
        // Calendar arithmetic rather than subtracting 24h of millis, so a DST transition can't
        // shift "yesterday" onto the wrong calendar day (matches Calendar.add on Android).
        val yesterday = calendar.dateByAddingUnit(NSCalendarUnitDay, value = -1, toDate = now, options = 0uL)
        return when {
            calendar.isDate(target, inSameDayAsDate = now) -> "Today"
            yesterday != null && calendar.isDate(target, inSameDayAsDate = yesterday) -> "Yesterday"
            else -> format(epochMillis, "EEE · MMM d")
        }
    }

    override fun shortDateLabel(
        epochMillis: Long,
        zone: DateZone,
        withYear: Boolean,
    ): String = format(epochMillis, if (withYear) "MMM d, yyyy" else "MMM d", zone.toNsTimeZone())

    override fun yearOf(
        epochMillis: Long,
        zone: DateZone,
    ): Int = calendarIn(zone.toNsTimeZone()).component(NSCalendarUnitYear, fromDate = dateOf(epochMillis)).toInt()

    override fun timeLabel(epochMillis: Long): String = format(epochMillis, "h:mm a")

    override fun dayKey(
        epochMillis: Long,
        zone: DateZone,
    ): String {
        val calendar = calendarIn(zone.toNsTimeZone())
        val date = dateOf(epochMillis)
        val year = calendar.component(NSCalendarUnitYear, fromDate = date).toInt()
        val dayOfYear =
            calendar
                .ordinalityOfUnit(NSCalendarUnitDay, inUnit = NSCalendarUnitYear, forDate = date)
                .toInt()
        return year.toString().padStart(YEAR_DIGITS, '0') + "-" + dayOfYear.toString().padStart(DAY_OF_YEAR_DIGITS, '0')
    }
}
