package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.tagLabel
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.UpdateRecordNoteUseCase
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.dayLabel
import com.arduia.expense.ui.design.dayKey
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
        initialSelectedRowId: String?,
        onEditRecord: (String) -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class HistoryFeatureEntryImpl : HistoryFeatureEntry {
    @Composable
    override fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        initialSelectedRowId: String?,
        onEditRecord: (String) -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val eventRepository: EventRepository = koinInject()
        val debtRepository: DebtRepository = koinInject()
        val sharedCostRepository: SharedCostRepository = koinInject()
        val deleteRecord: DeleteRecordUseCase = koinInject()
        val updateRecordNote: UpdateRecordNoteUseCase = koinInject()
        val noteFallback = stringResource(R.string.journal_note_fallback)

        val records by financeRecordRepository.observeAll().collectAsState(emptyList())
        val events by eventRepository.observeAll().collectAsState(emptyList())
        val debts by debtRepository.observeAll().collectAsState(emptyList())
        val sharedCosts by sharedCostRepository.observeAll().collectAsState(emptyList())
        val eventNames = remember(events) { events.associate { it.id.value to it.name } }
        val debtNames = remember(debts) { debts.associate { it.id.value to it.personName } }
        val sharedCostNames = remember(sharedCosts) { sharedCosts.associate { it.id.value to it.title } }
        val days = remember(records, noteFallback, eventNames, debtNames, sharedCostNames) {
            groupByDay(records, noteFallback, eventNames, debtNames, sharedCostNames)
        }
        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            days = days,
            initialSelectedRowId = initialSelectedRowId,
            onDeleteRecord = { rowId ->
                scope.launch { deleteRecord(rowId) }
            },
            onUpdateNote = { rowId, note ->
                scope.launch { updateRecordNote(rowId, note) }
            },
            onEditRecord = onEditRecord,
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()

private fun groupByDay(
    records: List<FinanceRecord>,
    noteFallback: String,
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
): List<JournalDayUi> {
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
                rows = dayRecords.map { record ->
                    record.toRowModel(noteFallback, eventNames, debtNames, sharedCostNames)
                },
            )
        }
}

private fun FinanceRecord.toRowModel(
    noteFallback: String,
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
): ProTransactionRowModel = ProTransactionRowModel(
    id = id.value,
    categoryId = categoryId.value,
    note = note?.trim().orEmpty().ifEmpty { noteFallback },
    meta = "${expenseCategoryLabel(categoryId.value)} · ${timeLabel(recordedAtEpochMillis)}",
    amount = moneyLabel(money.amount.valueInCents),
    tag = link.tagLabel(eventNames, debtNames, sharedCostNames),
)

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
