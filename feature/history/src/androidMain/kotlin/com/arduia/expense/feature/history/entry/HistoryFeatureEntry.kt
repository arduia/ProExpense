package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID
import com.arduia.expense.domain.tagLabel
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.UpdateRecordNoteUseCase
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.feature.history.ui.preview.JournalFilterUi
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.expenseCategoryLabel
import java.util.Calendar
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
        records: List<FinanceRecord>,
        isLoading: Boolean,
        categories: List<Category>,
        events: List<Event>,
        debts: List<Debt>,
        sharedCosts: List<SharedCost>,
        modifier: Modifier = Modifier,
        homeCurrencySymbol: String = "$",
        onOpenLinkedEvent: (String) -> Unit = {},
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
        records: List<FinanceRecord>,
        isLoading: Boolean,
        categories: List<Category>,
        events: List<Event>,
        debts: List<Debt>,
        sharedCosts: List<SharedCost>,
        modifier: Modifier,
        homeCurrencySymbol: String,
        onOpenLinkedEvent: (String) -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        val deleteRecord: DeleteRecordUseCase = koinInject()
        val updateRecordNote: UpdateRecordNoteUseCase = koinInject()
        val allFilterLabel = stringResource(R.string.journal_filter_all)

        val eventNames = remember(events) { events.associate { it.id.value to it.name } }
        val debtNames = remember(debts) { debts.associate { it.id.value to it.personName } }
        val sharedCostNames = remember(sharedCosts) { sharedCosts.associate { it.id.value to it.title } }
        val categoryNames = remember(categories) { categories.associate { it.id.value to it.name } }
        // Secondary line for Journal Detail's linked-tag card (US-HIS-5) — an event's date range
        // or a debt's amount, mirroring what feature:logging shows in its own tag picker.
        val eventSubtitles = remember(events) {
            events.associate {
                it.id.value to "${PlatformDateFormatter.shortDateLabel(it.startEpochMillis)} - " +
                    PlatformDateFormatter.shortDateLabel(it.endEpochMillis)
            }
        }
        val debtSubtitles = remember(debts, homeCurrencySymbol) {
            debts.associate { it.id.value to AmountInput.formatMoney(it.money.amount.valueInCents, currencySymbol(it.money.currency.code)) }
        }
        val filters = remember(categories, allFilterLabel, records) {
            val categoryChips = categories.sortedBy { it.sortOrder }.map { JournalFilterUi(it.id.value, it.name) }
            // Uncategorized is never seeded as a real Category row (US-CAT-3), so it needs its
            // own chip here whenever a reassigned record actually exists under it — otherwise
            // those records are visible in the list but unreachable by filter.
            val uncategorizedChip = if (records.any { it.categoryId.value == UNCATEGORIZED_CATEGORY_ID }) {
                listOf(JournalFilterUi(UNCATEGORIZED_CATEGORY_ID, expenseCategoryLabel(UNCATEGORIZED_CATEGORY_ID)))
            } else {
                emptyList()
            }
            listOf(JournalFilterUi("all", allFilterLabel)) + categoryChips + uncategorizedChip
        }
        val days = remember(records, eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles, homeCurrencySymbol) {
            groupByDay(records, eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles, homeCurrencySymbol)
        }
        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            days = days,
            filters = filters,
            categoryNames = categoryNames,
            initialSelectedRowId = initialSelectedRowId,
            isLoading = isLoading,
            onDeleteRecord = { rowId ->
                scope.launch { deleteRecord(rowId) }
            },
            onUpdateNote = { rowId, note ->
                scope.launch { updateRecordNote(rowId, note) }
            },
            onEditRecord = onEditRecord,
            onOpenLinkedEvent = onOpenLinkedEvent,
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()

private fun groupByDay(
    records: List<FinanceRecord>,
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
    eventSubtitles: Map<String, String>,
    debtSubtitles: Map<String, String>,
    homeCurrencySymbol: String,
): List<JournalDayUi> {
    val sorted = records.sortedByDescending { it.recordedAtEpochMillis }
    return sorted
        .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
        .toSortedMap(compareByDescending { it })
        .map { (key, dayRecords) ->
            val totalCents = dayRecords.sumOf { it.homeCurrencyMoney.amount.valueInCents }
            JournalDayUi(
                id = key,
                title = PlatformDateFormatter.dayLabel(dayRecords.first().recordedAtEpochMillis),
                total = AmountInput.formatMoney(totalCents, homeCurrencySymbol),
                rows = dayRecords.map { record ->
                    record.toRowModel(eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles)
                },
            )
        }
}

private fun FinanceRecord.toRowModel(
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
    eventSubtitles: Map<String, String>,
    debtSubtitles: Map<String, String>,
): ProTransactionRowModel = ProTransactionRowModel(
    id = id.value,
    categoryId = categoryId.value,
    note = note?.trim().orEmpty().ifEmpty { expenseCategoryLabel(categoryId.value) },
    meta = "${expenseCategoryLabel(categoryId.value)} · ${PlatformDateFormatter.timeLabel(recordedAtEpochMillis)}",
    amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
    tag = link.tagLabel(eventNames, debtNames, sharedCostNames),
    tagSubtitle = link.tagLabel(eventSubtitles, debtSubtitles, emptyMap()),
    rawNote = note?.trim(),
    detailDateTimeLabel = "${PlatformDateFormatter.dayLabel(recordedAtEpochMillis)} · " +
        PlatformDateFormatter.timeLabel(recordedAtEpochMillis),
    linkedEventId = (link as? RecordLink.ToEvent)?.eventId?.value,
)
