package com.arduia.expense.ui.design

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** [DateRangePickerState] returns UTC-midnight epoch millis, not device-local instants. */
val UtcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

fun dayLabel(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
    val target = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val today = Calendar.getInstance().apply { timeInMillis = now }
    if (isSameDay(target, today)) return "Today"

    val yesterday = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, -1)
    }
    if (isSameDay(target, yesterday)) return "Yesterday"

    return SimpleDateFormat("EEE · MMM d", Locale.US).format(target.time)
}

fun shortDateLabel(epochMillis: Long, timeZone: TimeZone = TimeZone.getDefault(), withYear: Boolean = false): String {
    val pattern = if (withYear) "MMM d, yyyy" else "MMM d"
    return SimpleDateFormat(pattern, Locale.US).apply { this.timeZone = timeZone }
        .format(Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }.time)
}

fun yearOf(epochMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): Int =
    Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }.get(Calendar.YEAR)

fun timeLabel(epochMillis: Long): String =
    SimpleDateFormat("h:mm a", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

fun dayKey(epochMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): String {
    val calendar = Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }
    return "%04d-%03d".format(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR))
}
