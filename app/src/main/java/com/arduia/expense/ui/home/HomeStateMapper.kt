package com.arduia.expense.ui.home

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.formatWithSymbol
import com.arduia.expense.feature.logging.logCategoryById
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object HomeStateMapper {
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    private val dayTitleFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM")

    fun mapRecords(
        records: List<FinanceRecord>,
        homeCurrencyCode: String,
        zoneId: ZoneId,
        highlightRecordId: String? = null,
    ): List<HomeDayGroup> {
        if (records.isEmpty()) return emptyList()

        return records
            .groupBy { record ->
                Instant.ofEpochMilli(record.recordedAtEpochMillis)
                    .atZone(zoneId)
                    .toLocalDate()
            }
            .entries
            .sortedByDescending { it.key }
            .map { (date, dayRecords) ->
                val zoned = date.atStartOfDay(zoneId)
                val totalMinor = dayRecords.sumOf { it.homeCurrencyAmount.valueInCents }
                HomeDayGroup(
                    title = zoned.format(dayTitleFormatter),
                    totalLabel = com.arduia.expense.domain.Amount(totalMinor)
                        .formatWithSymbol(homeCurrencyCode),
                    items = dayRecords
                        .sortedByDescending { it.recordedAtEpochMillis }
                        .map { record -> toTransactionItem(record, homeCurrencyCode, zoneId) },
                )
            }
    }

    fun monthLabel(zoneId: ZoneId = ZoneId.systemDefault()): String =
        java.time.ZonedDateTime.now(zoneId).format(monthFormatter)

    fun spendHelperText(isEmpty: Boolean): String? =
        if (isEmpty) "No spending logged yet this month" else null

    private fun toTransactionItem(
        record: FinanceRecord,
        homeCurrencyCode: String,
        zoneId: ZoneId,
    ): HomeTransactionItem {
        val category = logCategoryById(record.categoryId)
        val timeLabel = Instant.ofEpochMilli(record.recordedAtEpochMillis)
            .atZone(zoneId)
            .format(timeFormatter)
        return HomeTransactionItem(
            id = record.id,
            categoryId = record.categoryId,
            note = record.note?.takeIf { it.isNotBlank() } ?: category.label,
            metaLabel = "${category.label} · $timeLabel",
            amountLabel = record.homeCurrencyAmount.formatWithSymbol(homeCurrencyCode),
        )
    }
}
