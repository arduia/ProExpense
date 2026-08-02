package com.arduia.expense.ui.design

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.timeZoneWithName

private const val MILLIS_PER_SECOND = 1000.0

/** Widths of the Android actual's "%04d-%03d" dayKey format — the two must stay byte-identical. */
private const val YEAR_DIGITS = 4
private const val DAY_OF_YEAR_DIGITS = 3

/** Mirrors the Android actual's `Locale.US` so both platforms render byte-identical labels. */
private val posixLocale: NSLocale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")

private fun DateZone.toNSTimeZone(): NSTimeZone =
    when (this) {
        DateZone.DeviceLocal -> NSTimeZone.localTimeZone
        DateZone.Utc -> NSTimeZone.timeZoneWithName("UTC") ?: NSTimeZone.localTimeZone
    }

private fun Long.toNSDate(): NSDate = NSDate.dateWithTimeIntervalSince1970(this / MILLIS_PER_SECOND)

private fun calendarIn(timeZone: NSTimeZone): NSCalendar =
    NSCalendar.currentCalendar().apply {
        setTimeZone(timeZone)
        setLocale(posixLocale)
    }

private fun formatter(
    pattern: String,
    timeZone: NSTimeZone,
): NSDateFormatter =
    NSDateFormatter().apply {
        setDateFormat(pattern)
        setLocale(posixLocale)
        setTimeZone(timeZone)
    }

/** Device-local formatting in one call — keeps the single-expression overrides under 120 columns. */
private fun localLabel(
    pattern: String,
    epochMillis: Long,
): String = formatter(pattern, NSTimeZone.localTimeZone).stringFromDate(epochMillis.toNSDate())

private fun Int.padTo(width: Int): String = toString().padStart(width, '0')

@OptIn(ExperimentalForeignApi::class)
actual object PlatformDateFormatter : DateFormatter {
    override fun dayLabel(
        epochMillis: Long,
        nowEpochMillis: Long,
    ): String {
        val calendar = calendarIn(NSTimeZone.localTimeZone)
        val target = epochMillis.toNSDate()
        val now = nowEpochMillis.toNSDate()
        // Compare start-of-day rather than raw deltas so "Today"/"Yesterday" track calendar days,
        // matching the Android actual's Calendar.DAY_OF_YEAR comparison.
        val targetDay = calendar.startOfDayForDate(target)
        val today = calendar.startOfDayForDate(now)
        val dayDelta =
            calendar
                .components(NSCalendarUnitDay, fromDate = targetDay, toDate = today, options = 0uL)
                .day
        return when (dayDelta) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> localLabel("EEE · MMM d", epochMillis)
        }
    }

    override fun shortDateLabel(
        epochMillis: Long,
        zone: DateZone,
        withYear: Boolean,
    ): String {
        val pattern = if (withYear) "MMM d, yyyy" else "MMM d"
        return formatter(pattern, zone.toNSTimeZone()).stringFromDate(epochMillis.toNSDate())
    }

    override fun yearOf(
        epochMillis: Long,
        zone: DateZone,
    ): Int =
        calendarIn(zone.toNSTimeZone())
            .component(NSCalendarUnitYear, fromDate = epochMillis.toNSDate())
            .toInt()

    override fun timeLabel(epochMillis: Long): String = localLabel("h:mm a", epochMillis)

    override fun dayKey(
        epochMillis: Long,
        zone: DateZone,
    ): String {
        val calendar = calendarIn(zone.toNSTimeZone())
        val date = epochMillis.toNSDate()
        val year = calendar.component(NSCalendarUnitYear, fromDate = date).toInt()
        val dayOfYear =
            calendar
                .ordinalityOfUnit(NSCalendarUnitDay, inUnit = NSCalendarUnitYear, forDate = date)
                .toInt()
        return "${year.padTo(YEAR_DIGITS)}-${dayOfYear.padTo(DAY_OF_YEAR_DIGITS)}"
    }
}
