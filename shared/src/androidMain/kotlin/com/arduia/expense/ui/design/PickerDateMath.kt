package com.arduia.expense.ui.design

import java.util.Calendar

internal fun startOfDay(epochMillis: Long): Long =
    Calendar
        .getInstance()
        .apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

internal fun addDays(
    epochMillis: Long,
    days: Int,
): Long =
    Calendar
        .getInstance()
        .apply {
            timeInMillis = epochMillis
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis

internal fun calendarAt(epochMillis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
