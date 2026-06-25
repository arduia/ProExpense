package com.arduia.expense.ui.design

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

fun shortDateLabel(epochMillis: Long): String =
    SimpleDateFormat("MMM d", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

fun timeLabel(epochMillis: Long): String =
    SimpleDateFormat("h:mm a", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)
