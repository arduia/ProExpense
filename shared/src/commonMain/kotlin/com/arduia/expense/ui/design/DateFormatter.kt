package com.arduia.expense.ui.design

import com.arduia.expense.shared.currentEpochMillis

/** Which calendar an epoch-millis value should be interpreted in when formatting. */
enum class DateZone {
    /** The device's current timezone setting. */
    DeviceLocal,

    /** UTC — required when reading back a date-only value a picker encoded as UTC midnight. */
    Utc,
}

/**
 * Single source of truth for record/event date-and-time display formatting. An interface (not a
 * free function set) so it can be implemented per platform: Android uses `java.text`/`java.util`,
 * iOS will use Foundation's `NSDateFormatter`/`NSCalendar` once an iOS target exists (see
 * shared/src/iosMain's readiness stubs). Access the platform implementation via
 * [PlatformDateFormatter]; tests can implement this interface with a fake for deterministic labels.
 */
interface DateFormatter {
    /** "Today" / "Yesterday" / "Tue · Jun 3" relative to [nowEpochMillis]. */
    fun dayLabel(
        epochMillis: Long,
        nowEpochMillis: Long = currentEpochMillis(),
    ): String

    /** "Jun 3" or "Jun 3, 2026" when [withYear]. */
    fun shortDateLabel(
        epochMillis: Long,
        zone: DateZone = DateZone.DeviceLocal,
        withYear: Boolean = false,
    ): String

    fun yearOf(
        epochMillis: Long,
        zone: DateZone = DateZone.DeviceLocal,
    ): Int

    /** "3:45 PM". */
    fun timeLabel(epochMillis: Long): String

    /** Stable "yyyy-dayOfYear" bucket key for grouping records by calendar day. */
    fun dayKey(
        epochMillis: Long,
        zone: DateZone = DateZone.DeviceLocal,
    ): String
}

/** Platform-selected [DateFormatter] singleton — Android today, iOS once a target is added. */
expect object PlatformDateFormatter : DateFormatter
