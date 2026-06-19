package com.arduia.expense.feature.history

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class AndroidRecordDateFormatter(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val locale: Locale = Locale.getDefault(),
) : RecordDateFormatter {
    override fun dayKey(epochMillis: Long): Long {
        val date = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    override fun formatDayTitle(dayKey: Long): String {
        val date = Instant.ofEpochMilli(dayKey).atZone(zoneId).toLocalDate()
        val today = Instant.now().atZone(zoneId).toLocalDate()
        val yesterday = today.minusDays(1)
        val dateText = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))
        return when (date) {
            today -> "Today, $dateText"
            yesterday -> "Yesterday, $dateText"
            else -> dateText
        }
    }

    override fun formatMeta(epochMillis: Long, categoryLabel: String): String {
        val time = Instant.ofEpochMilli(epochMillis)
            .atZone(zoneId)
            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))
        return "$categoryLabel · $time"
    }

    override fun nowEpochMillis(): Long = System.currentTimeMillis()

    override fun daysInMonth(epochMillis: Long): Int {
        val date = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        return date.lengthOfMonth()
    }
}
