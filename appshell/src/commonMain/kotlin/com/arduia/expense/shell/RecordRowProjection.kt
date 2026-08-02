package com.arduia.expense.shell

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.ProTransactionRowModel

/**
 * Record → row/day-group projection shared by Home and Journal, so a record renders the same way
 * wherever it appears and on whichever platform.
 */
object RecordRowProjection {
    fun toRow(
        record: FinanceRecord,
        categoryNames: Map<String, String>,
        currencySymbol: String,
    ): ProTransactionRowModel {
        val isIncome = record.type == RecordType.INCOME
        val categoryLabel = categoryNames[record.categoryId.value].orEmpty()
        return ProTransactionRowModel(
            id = record.id.value,
            categoryId = record.categoryId.value,
            note = record.note.orEmpty().ifBlank { categoryLabel },
            meta =
                listOf(categoryLabel, PlatformDateFormatter.timeLabel(record.recordedAtEpochMillis))
                    .filter { it.isNotBlank() }
                    .joinToString(" · "),
            amount = AmountInput.formatMoney(record.homeCurrencyMoney.amount.valueInCents, currencySymbol),
            isIncome = isIncome,
            rawNote = record.note,
            detailDateTimeLabel =
                PlatformDateFormatter.dayLabel(record.recordedAtEpochMillis) + " · " +
                    PlatformDateFormatter.timeLabel(record.recordedAtEpochMillis),
            rowKind = if (isIncome) ProRowKind.INCOME else ProRowKind.EXPENSE,
        )
    }

    /**
     * Groups by calendar day, newest first. Day totals count expenses only — income is listed in the
     * group but must not reduce the day's spend figure.
     */
    fun toDayGroups(
        records: List<FinanceRecord>,
        categoryNames: Map<String, String>,
        currencySymbol: String,
        nowEpochMillis: Long,
    ): List<HomeDayGroup> =
        records
            .sortedByDescending { it.recordedAtEpochMillis }
            .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
            .map { (_, dayRecords) ->
                val dayTotalCents =
                    dayRecords
                        .filter { it.type == RecordType.EXPENSE }
                        .sumOf { it.homeCurrencyMoney.amount.valueInCents }
                HomeDayGroup(
                    dayTitle = PlatformDateFormatter.dayLabel(dayRecords.first().recordedAtEpochMillis, nowEpochMillis),
                    dayTotal = AmountInput.formatMoney(dayTotalCents, currencySymbol),
                    rows = dayRecords.map { toRow(it, categoryNames, currencySymbol) },
                )
            }
}
