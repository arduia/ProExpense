package com.arduia.expense.feature.history

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.formatWithSymbol

data class ExpenseListItem(
    val id: String,
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    val tag: String? = null,
)

data class ExpenseDayGroup(
    val dayTitle: String,
    val dayTotal: String,
    val transactions: List<ExpenseListItem>,
)

fun groupRecordsByDay(
    records: List<FinanceRecord>,
    formatter: RecordDateFormatter,
    homeCurrencyCode: String,
    categoryLabel: (String) -> String,
    recentLimit: Int? = null,
): List<ExpenseDayGroup> {
    val limited = if (recentLimit != null) records.take(recentLimit) else records
    return limited
        .groupBy { formatter.dayKey(it.recordedAtEpochMillis) }
        .entries
        .sortedByDescending { it.key }
        .map { (dayKey, dayRecords) ->
            val sorted = dayRecords.sortedByDescending { it.recordedAtEpochMillis }
            val totalCents = sorted.sumOf { it.homeCurrencyAmount.valueInCents }
            ExpenseDayGroup(
                dayTitle = formatter.formatDayTitle(dayKey),
                dayTotal = com.arduia.expense.domain.Amount(totalCents).formatWithSymbol(homeCurrencyCode),
                transactions = sorted.map { it.toListItem(formatter, homeCurrencyCode, categoryLabel) },
            )
        }
}

fun FinanceRecord.toListItem(
    formatter: RecordDateFormatter,
    homeCurrencyCode: String,
    categoryLabel: (String) -> String,
): ExpenseListItem {
    val label = categoryLabel(categoryId)
    return ExpenseListItem(
        id = id,
        categoryId = categoryId,
        note = note?.takeIf { it.isNotBlank() } ?: label,
        meta = formatter.formatMeta(recordedAtEpochMillis, label),
        amount = homeCurrencyAmount.formatWithSymbol(homeCurrencyCode),
    )
}
