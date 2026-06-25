package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.dayLabel
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.design.timeLabel
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface HistoryFeatureEntry {
    @Composable
    fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class HistoryFeatureEntryImpl : HistoryFeatureEntry {
    @Composable
    override fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val noteFallback = stringResource(R.string.journal_note_fallback)

        val records by financeRecordRepository.observeAll().collectAsState(emptyList())
        val days = remember(records, noteFallback) { groupByDay(records, noteFallback) }
        val recordsById = remember(records) { records.associateBy { it.id.value } }

        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            days = days,
            onDeleteRecord = { rowId ->
                scope.launch { financeRecordRepository.delete(RecordId(rowId)) }
            },
            onUpdateNote = { rowId, note ->
                val record = recordsById[rowId]
                if (record != null) {
                    scope.launch {
                        financeRecordRepository.upsert(record.copy(note = note.ifBlank { null }))
                    }
                }
            },
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()

private fun groupByDay(records: List<FinanceRecord>, noteFallback: String): List<JournalDayUi> {
    val sorted = records.sortedByDescending { it.recordedAtEpochMillis }
    return sorted
        .groupBy { dayKey(it.recordedAtEpochMillis) }
        .toSortedMap(compareByDescending { it })
        .map { (key, dayRecords) ->
            val totalCents = dayRecords.sumOf { it.money.amount.valueInCents }
            JournalDayUi(
                id = key,
                title = dayLabel(dayRecords.first().recordedAtEpochMillis),
                total = moneyLabel(totalCents),
                rows = dayRecords.map { record -> record.toRowModel(noteFallback) },
            )
        }
}

private fun FinanceRecord.toRowModel(noteFallback: String): ProTransactionRowModel = ProTransactionRowModel(
    id = id.value,
    categoryId = categoryId.value,
    note = note?.trim().orEmpty().ifEmpty { noteFallback },
    meta = "${expenseCategoryLabel(categoryId.value)} · ${timeLabel(recordedAtEpochMillis)}",
    amount = moneyLabel(money.amount.valueInCents),
)

private fun dayKey(epochMillis: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
    return "%04d-%03d".format(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR))
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
