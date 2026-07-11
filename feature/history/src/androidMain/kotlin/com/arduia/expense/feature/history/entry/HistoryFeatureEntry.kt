package com.arduia.expense.feature.history.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordKind
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID
import com.arduia.expense.domain.kind
import com.arduia.expense.domain.linkedRowId
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
import com.arduia.expense.feature.history.visibleUnrecordedDebts
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.expenseCategoryLabel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/** Debounce between the user's last keystroke and the search query reaching the DB (US-HIS §Journal search). */
private const val SEARCH_DEBOUNCE_MILLIS = 250L

/**
 * Journal's loaded pages, filter selections, and change-signal bookkeeping. Opaque to callers —
 * hold it via [HistoryFeatureEntry.rememberJournalTabState] at a scope that survives tab
 * switching (the app shell), so reselecting the Journal tab resumes where the user left off
 * instead of reloading from scratch.
 */
interface JournalTabState

interface HistoryFeatureEntry {
    @Composable
    fun rememberJournalTabState(): JournalTabState

    @Composable
    fun JournalTab(
        state: JournalTabState,
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
        // Split/Debt-kind rows navigate straight to their own feature's detail screen instead of
        // the generic Journal Detail sheet — see ProRowKind.
        onOpenSplit: (String) -> Unit = {},
        onOpenDebt: (String) -> Unit = {},
    )
}

internal class HistoryFeatureEntryImpl : HistoryFeatureEntry {
    @Composable
    override fun rememberJournalTabState(): JournalTabState {
        val loadJournalPage: LoadJournalPageUseCase = koinInject()
        return remember { JournalTabStateImpl(loadJournalPage) }
    }

    @Composable
    override fun JournalTab(
        state: JournalTabState,
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
        onOpenSplit: (String) -> Unit,
        onOpenDebt: (String) -> Unit,
    ) {
        state as JournalTabStateImpl
        val scope = rememberCoroutineScope()
        val deleteRecord: DeleteRecordUseCase = koinInject()
        val updateRecordNote: UpdateRecordNoteUseCase = koinInject()
        val historyRepository: HistoryRepository = koinInject()
        val allFilterLabel = stringResource(R.string.journal_filter_all)
        val pager = state.pager

        // Search-as-you-type now triggers a DB query per change instead of a free in-memory
        // filter, so raw keystrokes are debounced before they reach the page filter.
        LaunchedEffect(state.query) {
            delay(SEARCH_DEBOUNCE_MILLIS)
            state.debouncedQuery = state.query
        }

        // The pager still holds whatever it loaded the last time the tab was shown — reload only
        // when the filter differs from what's actually loaded, so tab revisits are instant.
        val filter = state.filter
        LaunchedEffect(filter) {
            if (state.loadedFilter != filter) {
                state.loadedFilter = filter
                pager.loadFirstPage()
            }
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
        LaunchedEffect(changeSignal) {
            // collectAsState's synthetic null runs this effect before the flow ever emits.
            val signal = changeSignal ?: return@LaunchedEffect
            // Equal signal means nothing changed since the last reload — this is the
            // subscription's initial echo on a tab revisit; reloading would blink for nothing.
            if (signal == state.lastSeenSignal) return@LaunchedEffect
            val isFirstSignal = state.lastSeenSignal == null
            state.lastSeenSignal = signal
            if (!isFirstSignal) {
                pager.loadFirstPage(limit = maxOf(pager.records.size, LoadJournalPageUseCase.DEFAULT_PAGE_SIZE))
            }
            val result = historyRepository.hasAnyRecordIn(CategoryId(UNCATEGORIZED_CATEGORY_ID))
            state.hasUncategorized = (result as? Result.Success)?.data ?: false
        }

        val eventNames = remember(events) { events.associate { it.id.value to it.name } }
        val debtNames = remember(debts) { debts.associate { it.id.value to it.personName } }
        val sharedCostNames = remember(sharedCosts) { sharedCosts.associate { it.id.value to it.title } }
        val categoryNames = remember(categories) { categories.associate { it.id.value to it.name } }
        // Secondary line for Journal Detail's linked-tag card (US-HIS-5) — an event's date range
        // or a debt's amount, mirroring what feature:logging shows in its own tag picker.
        val eventSubtitles =
            remember(events) {
                events.associate {
                    it.id.value to "${PlatformDateFormatter.shortDateLabel(it.startEpochMillis)} - " +
                        PlatformDateFormatter.shortDateLabel(it.endEpochMillis)
                }
            }
        val debtSubtitles =
            remember(debts, homeCurrencySymbol) {
                debts.associate {
                    it.id.value to
                        AmountInput.formatMoney(
                            it.money.amount.valueInCents,
                            currencySymbol(it.money.currency.code),
                        )
                }
            }
        val filters =
            remember(categories, allFilterLabel, state.hasUncategorized) {
                val categoryChips = categories.sortedBy { it.sortOrder }.map { JournalFilterUi(it.id.value, it.name) }
                // Uncategorized is never seeded as a real Category row (US-CAT-3), so it needs its
                // own chip here whenever a reassigned record actually exists under it — otherwise
                // those records are visible in the list but unreachable by filter.
                val uncategorizedChip =
                    if (state.hasUncategorized) {
                        listOf(JournalFilterUi(UNCATEGORIZED_CATEGORY_ID, expenseCategoryLabel(UNCATEGORIZED_CATEGORY_ID)))
                    } else {
                        emptyList()
                    }
                listOf(JournalFilterUi("all", allFilterLabel)) + categoryChips + uncategorizedChip
            }
        // Grouped from whatever's been loaded so far (bounded by scroll depth), never the full
        // table — search/category/date-range filtering already happened in SQL via `filter`.
        val days =
            remember(
                pager.records,
                pager.endReached,
                debts,
                eventNames,
                debtNames,
                sharedCostNames,
                eventSubtitles,
                debtSubtitles,
                homeCurrencySymbol,
            ) {
                groupByDay(
                    pager.records,
                    debts,
                    pager.endReached,
                    JournalLinkLabels(eventNames, debtNames, sharedCostNames, eventSubtitles, debtSubtitles),
                    homeCurrencySymbol,
                )
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
            query = state.query,
            onQueryChange = { state.query = it },
            selectedFilterId = state.selectedFilterId,
            onFilterSelected = { state.selectedFilterId = it },
            dateRangeStart = state.dateRangeStart,
            dateRangeEnd = state.dateRangeEnd,
            onDateRangeChange = { start, end ->
                state.dateRangeStart = start
                state.dateRangeEnd = end
            },
            onDeleteRecord = { rowId ->
                scope.launch { deleteRecord(rowId) }
            },
            onUpdateNote = { rowId, note ->
                scope.launch { updateRecordNote(rowId, note) }
            },
            onEditRecord = onEditRecord,
            onOpenLinkedEvent = onOpenLinkedEvent,
            onOpenSplit = onOpenSplit,
            onOpenDebt = onOpenDebt,
            modifier = modifier,
        )
    }
}

object HistoryFeatureUi : HistoryFeatureEntry by HistoryFeatureEntryImpl()

/**
 * Backing store for [JournalTabState]. Lives above the tab-scoped composition, so everything in
 * here — the pager's loaded pages, search/filter selections, and the change-signal bookkeeping —
 * survives switching away from the Journal tab and back.
 */
private class JournalTabStateImpl(
    loadJournalPage: LoadJournalPageUseCase,
) : JournalTabState {
    var query by mutableStateOf("")
    var debouncedQuery by mutableStateOf("")
    var selectedFilterId by mutableStateOf("all")
    var dateRangeStart by mutableStateOf<Long?>(null)
    var dateRangeEnd by mutableStateOf<Long?>(null)
    var hasUncategorized by mutableStateOf(false)

    // Effect bookkeeping, never read in composition: the filter the pager's records belong to,
    // and the last change signal acted on — both let tab revisits skip redundant reloads.
    var loadedFilter: RecordHistoryFilter? = null
    var lastSeenSignal: RecordChangeSignal? = null

    val filter: RecordHistoryFilter
        get() =
            RecordHistoryFilter(
                categoryId = selectedFilterId.takeIf { it != "all" }?.let(::CategoryId),
                fromEpochMillis = dateRangeStart,
                toEpochMillis = dateRangeEnd,
                query = debouncedQuery.takeIf { it.isNotBlank() },
            )

    // The lambda reads `filter` at call time, so the pager always pages with the live filter
    // even though the pager itself is created once and kept for the state holder's lifetime.
    val pager =
        JournalPager(
            loadPage = { cursor, limit -> loadJournalPage(filter, cursor, limit) },
        )
}

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

private fun RecordKind.toProRowKind(): ProRowKind =
    when (this) {
        RecordKind.EXPENSE -> ProRowKind.EXPENSE
        RecordKind.INCOME -> ProRowKind.INCOME
        RecordKind.SPLIT -> ProRowKind.SPLIT
        RecordKind.DEBT_LENT -> ProRowKind.DEBT_LENT
        RecordKind.DEBT_OWED -> ProRowKind.DEBT_OWED
    }

/** Shared timestamp so [FinanceRecord] and toggle-off [Debt] rows can be merged and sorted together. */
private data class JournalEntry(
    val recordedAtEpochMillis: Long,
    val row: ProTransactionRowModel,
)

/** Bundles the tag-label lookup maps so they cost one parameter, not five, in call sites below. */
private data class JournalLinkLabels(
    val eventNames: Map<String, String>,
    val debtNames: Map<String, String>,
    val sharedCostNames: Map<String, String>,
    val eventSubtitles: Map<String, String>,
    val debtSubtitles: Map<String, String>,
)

private fun groupByDay(
    records: List<FinanceRecord>,
    debts: List<Debt>,
    recordsFullyLoaded: Boolean,
    linkLabels: JournalLinkLabels,
    homeCurrencySymbol: String,
): List<JournalDayUi> {
    val recordEntries =
        records.map { record ->
            JournalEntry(record.recordedAtEpochMillis, record.toRowModel(linkLabels))
        }
    // A toggle-on debt already has a linked FinanceRecord (RecordLink.ToDebt, same id as the
    // debt) inside `records` above — visibleUnrecordedDebts only returns toggle-off ones, so a
    // debt never renders twice.
    val oldestLoadedRecordMillis = records.minOfOrNull { it.recordedAtEpochMillis }
    val debtEntries =
        visibleUnrecordedDebts(debts, oldestLoadedRecordMillis, recordsFullyLoaded)
            .map { debt -> JournalEntry(debt.recordedAtEpochMillis, debt.toDebtRowModel()) }

    val recordTotalCentsByDay =
        records
            .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
            .mapValues { (_, dayRecords) -> dayRecords.sumOf { it.homeCurrencyMoney.amount.valueInCents } }

    val sorted = (recordEntries + debtEntries).sortedByDescending { it.recordedAtEpochMillis }
    return sorted
        .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
        .toSortedMap(compareByDescending { it })
        .map { (key, dayEntries) ->
            JournalDayUi(
                id = key,
                title = PlatformDateFormatter.dayLabel(dayEntries.first().recordedAtEpochMillis),
                // Only the FinanceRecord subset counts toward the day total — a toggle-off debt is
                // visible but never moves it.
                total = AmountInput.formatMoney(recordTotalCentsByDay[key] ?: 0L, homeCurrencySymbol),
                rows = dayEntries.map { it.row },
            )
        }
}

private fun FinanceRecord.toRowModel(linkLabels: JournalLinkLabels): ProTransactionRowModel {
    val rowKind = kind().toProRowKind()
    val linkedId = linkedRowId()
    // The row's own badge/note already convey "this is a split/debt" — an "@ tag" chip repeating
    // the same title underneath would be redundant (unlike a plain expense manually tagged to an
    // event/debt, where the tag is genuinely secondary context).
    val suppressTag = rowKind == ProRowKind.SPLIT || rowKind == ProRowKind.DEBT_LENT || rowKind == ProRowKind.DEBT_OWED
    val tagLabel =
        if (suppressTag) {
            null
        } else {
            link.tagLabel(linkLabels.eventNames, linkLabels.debtNames, linkLabels.sharedCostNames)
        }
    val tagSubtitleLabel =
        if (suppressTag) {
            null
        } else {
            link.tagLabel(linkLabels.eventSubtitles, linkLabels.debtSubtitles, emptyMap())
        }
    return ProTransactionRowModel(
        id = id.value,
        categoryId = categoryId.value,
        note = note?.trim().orEmpty().ifEmpty { expenseCategoryLabel(categoryId.value) },
        meta = "${expenseCategoryLabel(categoryId.value)} · ${PlatformDateFormatter.timeLabel(recordedAtEpochMillis)}",
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        isIncome = type == RecordType.INCOME,
        tag = tagLabel,
        tagSubtitle = tagSubtitleLabel,
        rawNote = note?.trim(),
        detailDateTimeLabel =
            "${PlatformDateFormatter.dayLabel(recordedAtEpochMillis)} · " +
                PlatformDateFormatter.timeLabel(recordedAtEpochMillis),
        linkedEventId = (link as? RecordLink.ToEvent)?.eventId?.value,
        rowKind = rowKind,
        linkedId = linkedId,
    )
}

private fun Debt.toDebtRowModel(): ProTransactionRowModel {
    val rowKind = if (direction == DebtDirection.OWED_TO_ME) ProRowKind.DEBT_LENT else ProRowKind.DEBT_OWED
    val actionLabel = if (direction == DebtDirection.OWED_TO_ME) "Lent" else "Borrowed"
    return ProTransactionRowModel(
        id = id.value,
        categoryId = "",
        note = personName,
        meta = "$actionLabel · ${PlatformDateFormatter.timeLabel(recordedAtEpochMillis)}",
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        rawNote = note,
        detailDateTimeLabel =
            "${PlatformDateFormatter.dayLabel(recordedAtEpochMillis)} · " +
                PlatformDateFormatter.timeLabel(recordedAtEpochMillis),
        rowKind = rowKind,
        linkedId = id.value,
    )
}
