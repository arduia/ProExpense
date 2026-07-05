package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID
import com.arduia.expense.domain.tagLabel
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.HistoryRepository
import com.arduia.expense.feature.history.LoadJournalPageUseCase
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.RecordHistoryFilter
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/** Debounce between the user's last keystroke and the search query reaching the DB (US-HIS §Journal search). */
private const val SEARCH_DEBOUNCE_MILLIS = 250L

interface HistoryFeatureEntry {
    @Composable
    fun JournalTab(
        selectedTab: HomeNavTab,
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        initialSelectedRowId: String?,
        onEditRecord: (String) -> Unit,
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
        val loadJournalPage: LoadJournalPageUseCase = koinInject()
        val historyRepository: HistoryRepository = koinInject()
        val allFilterLabel = stringResource(R.string.journal_filter_all)

        var query by rememberSaveable { mutableStateOf("") }
        var debouncedQuery by remember { mutableStateOf("") }
        var selectedFilterId by rememberSaveable { mutableStateOf("all") }
        var dateRangeStart by rememberSaveable { mutableStateOf<Long?>(null) }
        var dateRangeEnd by rememberSaveable { mutableStateOf<Long?>(null) }
        var hasUncategorized by remember { mutableStateOf(false) }

        // Search-as-you-type now triggers a DB query per change instead of a free in-memory
        // filter, so raw keystrokes are debounced before they reach the page filter.
        LaunchedEffect(query) {
            delay(SEARCH_DEBOUNCE_MILLIS)
            debouncedQuery = query
        }

        val filter = remember(debouncedQuery, selectedFilterId, dateRangeStart, dateRangeEnd) {
            RecordHistoryFilter(
                categoryId = selectedFilterId.takeIf { it != "all" }?.let(::CategoryId),
                fromEpochMillis = dateRangeStart,
                toEpochMillis = dateRangeEnd,
                query = debouncedQuery.takeIf { it.isNotBlank() },
            )
        }
        // The pager instance is remembered once (it must survive recomposition to keep its
        // already-loaded records), but its loadPage lambda must still see the latest filter and
        // use-case on every call — rememberUpdatedState avoids capturing a stale `filter` from
        // whichever composition happened to be running when `remember` first created the pager.
        val currentFilter = rememberUpdatedState(filter)
        val currentLoadJournalPage = rememberUpdatedState(loadJournalPage)
        val pager = remember {
            JournalPager(
                loadPage = { cursor, limit ->
                    currentLoadJournalPage.value(currentFilter.value, cursor, limit)
                },
            )
        }

        LaunchedEffect(filter) {
            pager.loadFirstPage()
        }

        // Deep link into a record that isn't on the first page yet (e.g. tapping an old expense
        // from Budget) — keep paging forward until it's loaded or the table runs out.
        LaunchedEffect(
            initialSelectedRowId,
            pager.records,
            pager.endReached,
            pager.isLoadingFirstPage,
            pager.isLoadingMore,
        ) {
            if (initialSelectedRowId != null &&
                !pager.isLoadingFirstPage &&
                !pager.isLoadingMore &&
                !pager.endReached &&
                pager.records.none { it.id.value == initialSelectedRowId }
            ) {
                pager.loadNextPage()
            }
        }

        // Cheap (count, lastUpdatedAt) signal from the DB — lets Journal notice a mutation made
        // elsewhere (import, edit, delete) without ever re-fetching the whole table. Reloads only
        // as many rows as were already loaded, so the reload cost still tracks scroll depth, not
        // total record count.
        val changeSignal by remember(historyRepository) { historyRepository.observeChangeSignal() }
            .collectAsState(initial = null)
        var handledFirstSignal by remember { mutableStateOf(false) }
        LaunchedEffect(changeSignal) {
            if (!handledFirstSignal) {
                handledFirstSignal = true
            } else {
                pager.loadFirstPage(limit = maxOf(pager.records.size, LoadJournalPageUseCase.DEFAULT_PAGE_SIZE))
            }
            val result = historyRepository.hasAnyRecordIn(CategoryId(UNCATEGORIZED_CATEGORY_ID))
            hasUncategorized = (result as? Result.Success)?.data ?: false
        }

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
        val filters = remember(categories, allFilterLabel, hasUncategorized) {
            val categoryChips = categories.sortedBy { it.sortOrder }.map { JournalFilterUi(it.id.value, it.name) }
            // Uncategorized is never seeded as a real Category row (US-CAT-3), so it needs its
            // own chip here whenever a reassigned record actually exists under it — otherwise
            // those records are visible in the list but unreachable by filter.
            val uncategorizedChip = if (hasUncategorized) {
                listOf(JournalFilterUi(UNCATEGORIZED_CATEGORY_ID, expenseCategoryLabel(UNCATEGORIZED_CATEGORY_ID)))
            } else {
                emptyList()
            }
            listOf(JournalFilterUi("all", allFilterLabel)) + categoryChips + uncategorizedChip
        }
        // Grouped from whatever's been loaded so far (bounded by scroll depth), never the full
        // table — search/category/date-range filtering already happened in SQL via `filter`.
        val days = remember(pager.records, eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles, homeCurrencySymbol) {
            groupByDay(pager.records, eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles, homeCurrencySymbol)
        }

        JournalFlow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            days = days,
            filters = filters,
            categoryNames = categoryNames,
            initialSelectedRowId = initialSelectedRowId,
            isLoading = pager.isLoadingFirstPage,
            isLoadingMore = pager.isLoadingMore,
            onLoadMore = { scope.launch { pager.loadNextPage() } },
            query = query,
            onQueryChange = { query = it },
            selectedFilterId = selectedFilterId,
            onFilterSelected = { selectedFilterId = it },
            dateRangeStart = dateRangeStart,
            dateRangeEnd = dateRangeEnd,
            onDateRangeChange = { start, end ->
                dateRangeStart = start
                dateRangeEnd = end
            },
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

/**
 * Keyset-paginated loader for the Journal list. Holds only what's been loaded so far (bounded by
 * how far the user has scrolled), never the full table — [loadFirstPage] resets to the first page
 * (used on mount and whenever the filter changes), [loadNextPage] appends the next page from the
 * last loaded row's cursor.
 */
private class JournalPager(
    private val loadPage: suspend (cursor: RecordPageCursor?, limit: Int) -> Result<List<FinanceRecord>>,
) {
    var records by mutableStateOf<List<FinanceRecord>>(emptyList())
        private set
    var isLoadingFirstPage by mutableStateOf(true)
        private set
    var isLoadingMore by mutableStateOf(false)
        private set
    var endReached by mutableStateOf(false)
        private set

    suspend fun loadFirstPage(limit: Int = LoadJournalPageUseCase.DEFAULT_PAGE_SIZE) {
        isLoadingFirstPage = true
        val result = loadPage(null, limit)
        records = (result as? Result.Success)?.data.orEmpty()
        endReached = records.size < limit
        isLoadingFirstPage = false
    }

    suspend fun loadNextPage() {
        if (isLoadingMore || isLoadingFirstPage || endReached) return
        val last = records.lastOrNull() ?: return
        isLoadingMore = true
        val cursor = RecordPageCursor(last.recordedAtEpochMillis, last.id)
        val result = loadPage(cursor, LoadJournalPageUseCase.DEFAULT_PAGE_SIZE)
        val page = (result as? Result.Success)?.data.orEmpty()
        records = records + page
        endReached = page.size < LoadJournalPageUseCase.DEFAULT_PAGE_SIZE
        isLoadingMore = false
    }
}

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
