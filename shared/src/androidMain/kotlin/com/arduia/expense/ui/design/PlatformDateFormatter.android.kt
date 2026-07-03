package com.arduia.expense.ui.design

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private fun DateZone.toJavaTimeZone(): TimeZone = when (this) {
    DateZone.DeviceLocal -> TimeZone.getDefault()
    DateZone.Utc -> TimeZone.getTimeZone("UTC")
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

actual object PlatformDateFormatter : DateFormatter {
    override fun dayLabel(epochMillis: Long, nowEpochMillis: Long): String {
        val target = Calendar.getInstance().apply { timeInMillis = epochMillis }
        val today = Calendar.getInstance().apply { timeInMillis = nowEpochMillis }
        if (isSameDay(target, today)) return "Today"

        val yesterday = Calendar.getInstance().apply {
            timeInMillis = nowEpochMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        if (isSameDay(target, yesterday)) return "Yesterday"

        return SimpleDateFormat("EEE · MMM d", Locale.US).format(target.time)
    }

    override fun shortDateLabel(epochMillis: Long, zone: DateZone, withYear: Boolean): String {
        val timeZone = zone.toJavaTimeZone()
        val pattern = if (withYear) "MMM d, yyyy" else "MMM d"
        return SimpleDateFormat(pattern, Locale.US).apply { this.timeZone = timeZone }
            .format(Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }.time)
    }

    override fun yearOf(epochMillis: Long, zone: DateZone): Int =
        Calendar.getInstance(zone.toJavaTimeZone()).apply { timeInMillis = epochMillis }.get(Calendar.YEAR)

    override fun timeLabel(epochMillis: Long): String =
        SimpleDateFormat("h:mm a", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

    override fun dayKey(epochMillis: Long, zone: DateZone): String {
        val calendar = Calendar.getInstance(zone.toJavaTimeZone()).apply { timeInMillis = epochMillis }
        return "%04d-%03d".format(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR))
    }
}
