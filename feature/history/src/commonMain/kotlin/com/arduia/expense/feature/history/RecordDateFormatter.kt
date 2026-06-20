package com.arduia.expense.feature.history

interface RecordDateFormatter {
    fun dayKey(epochMillis: Long): Long

    fun formatDayTitle(dayKey: Long): String

    fun formatMonthYear(epochMillis: Long): String

    fun formatMeta(epochMillis: Long, categoryLabel: String): String

    fun nowEpochMillis(): Long

    fun daysInMonth(epochMillis: Long): Int

    fun minusMonths(epochMillis: Long, months: Int): Long
}
