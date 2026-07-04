package com.arduia.expense.feature.history.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.feature.history.ui.preview.JournalDetailUiState
import com.arduia.expense.feature.history.ui.preview.JournalFilterUi
import com.arduia.expense.feature.history.ui.preview.JournalLinkedTagUi
import com.arduia.expense.feature.history.ui.preview.JournalListUiState
import com.arduia.expense.feature.history.ui.preview.JournalQuickNoteUiState
import com.arduia.expense.feature.history.ui.preview.journalFilters
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.ui.design.DateZone
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

@Composable
fun JournalFlow(
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    days: List<JournalDayUi> = previewJournalList.days,
    filters: List<JournalFilterUi> = journalFilters,
    categoryNames: Map<String, String> = emptyMap(),
    initialSelectedRowId: String? = null,
    isLoading: Boolean = false,
    onDeleteRecord: (String) -> Unit = {},
    onUpdateNote: (String, String) -> Unit = { _, _ -> },
    onEditRecord: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    // Filter/search state survives config change (rotation, theme switch); transient sheet/dialog
    // visibility does not need to.
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilterId by rememberSaveable { mutableStateOf("all") }
    var selectedRowId by rememberSaveable { mutableStateOf(initialSelectedRowId) }
    var quickNoteRow by remember { mutableStateOf<ProTransactionRowModel?>(null) }
    var quickNoteText by remember { mutableStateOf("") }
    var showActions by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDateRangeSheet by remember { mutableStateOf(false) }
    var dateRangeStart by rememberSaveable { mutableStateOf<Long?>(null) }
    var dateRangeEnd by rememberSaveable { mutableStateOf<Long?>(null) }

    val searchActive = query.isNotBlank()
    // DateRangePickerState reports UTC-midnight millis, not device-local instants — must be
    // read back with the same UTC calendar or the label/filter drift by a day off UTC.
    val dateRangeLabel = if (dateRangeStart != null && dateRangeEnd != null) {
        // Omitting the year is ambiguous once the range isn't entirely within the current
        // year (a past year, or one that crosses a year boundary) — show it on both ends
        // whenever that's the case so "Dec 20 – Jan 5" can't be misread as backwards.
        val currentYear = PlatformDateFormatter.yearOf(System.currentTimeMillis(), DateZone.Utc)
        val startYear = PlatformDateFormatter.yearOf(dateRangeStart!!, DateZone.Utc)
        val endYear = PlatformDateFormatter.yearOf(dateRangeEnd!!, DateZone.Utc)
        val withYear = startYear != currentYear || endYear != currentYear
        "${PlatformDateFormatter.shortDateLabel(dateRangeStart!!, DateZone.Utc, withYear)} – " +
            PlatformDateFormatter.shortDateLabel(dateRangeEnd!!, DateZone.Utc, withYear)
    } else {
        null
    }
    val filteredDays = remember(days, query, selectedFilterId, categoryNames, dateRangeStart, dateRangeEnd) {
        filterJournalDays(
            days = days,
            query = query,
            selectedFilterId = selectedFilterId,
            categoryLabelFor = { id -> categoryNames[id] ?: expenseCategoryLabel(id) },
            startDayKey = dateRangeStart?.let { PlatformDateFormatter.dayKey(it, DateZone.Utc) },
            endDayKey = dateRangeEnd?.let { PlatformDateFormatter.dayKey(it, DateZone.Utc) },
        )
    }
    val listState = JournalListUiState(
        query = query,
        selectedFilterId = selectedFilterId,
        days = filteredDays,
        filters = filters,
        searchActive = searchActive,
        isLoading = isLoading,
    )

    BackHandler(enabled = selectedRowId != null) {
        if (initialSelectedRowId != null && selectedRowId == initialSelectedRowId) {
            onTabSelected(HomeNavTab.Home)
        }
        selectedRowId = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = selectedRowId,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = if (initialState == null) 0 else 1,
                    toIndex = if (targetState == null) 0 else 1,
                    reduceMotion = reduceMotion,
                )
            },
            label = "journalStep",
        ) { rowId ->
            if (rowId == null) {
                JournalListScreen(
                    state = listState,
                    onQueryChange = { query = it },
                    onFilterSelected = { selectedFilterId = it },
                    onRowClick = { selectedRowId = it.id },
                    onRowLongPress = { row ->
                        quickNoteRow = row
                        // Pre-fill with the existing note (US-HIS-4) — an empty text field would
                        // silently overwrite any note already on the record when saved.
                        quickNoteText = row.rawNote.orEmpty()
                    },
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                    dateRangeLabel = dateRangeLabel,
                    onDateRangeClick = { showDateRangeSheet = true },
                    onClearDateRange = {
                        dateRangeStart = null
                        dateRangeEnd = null
                    },
                )
            } else if (days.flatMap { it.rows }.none { it.id == rowId }) {
                // Records load asynchronously — a deep link straight into detail (e.g. from
                // Home) can compose before the Flow emits, and detailStateFor's "food"/blank
                // defaults would otherwise flash before the real row arrives one frame later.
                Box(modifier = Modifier.fillMaxSize().background(colors.paper))
            } else {
                JournalDetailScreen(
                    state = detailStateFor(rowId, days, categoryNames),
                    onBack = {
                        if (initialSelectedRowId != null && rowId == initialSelectedRowId) {
                            onTabSelected(HomeNavTab.Home)
                        }
                        selectedRowId = null
                    },
                    onActions = { showActions = true },
                    onLinkedTagClick = {},
                    onEdit = { onEditRecord(rowId) },
                    onDelete = { showDeleteConfirm = true },
                )
            }
        }

        ProBottomSheetHost(
            visible = quickNoteRow != null,
            title = stringResource(R.string.journal_quick_note_title),
            onClose = { quickNoteRow = null },
        ) {
            val row = quickNoteRow
            if (row != null) {
                JournalQuickNoteSheetContent(
                    state = JournalQuickNoteUiState(row = row, note = quickNoteText),
                    onNoteChange = { quickNoteText = it },
                    onSave = {
                        onUpdateNote(row.id, quickNoteText)
                        quickNoteRow = null
                    },
                )
            }
        }

        ProBottomSheetHost(
            visible = showActions,
            title = null,
            onClose = { showActions = false },
        ) {
            JournalActionsSheetContent(
                onEdit = {
                    showActions = false
                    selectedRowId?.let(onEditRecord)
                },
                onDelete = {
                    showActions = false
                    showDeleteConfirm = true
                },
                onCancel = { showActions = false },
            )
        }

        ProAlertDialog(
            visible = showDeleteConfirm,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.journal_delete_title),
            body = buildAnnotatedString {
                append(stringResource(R.string.journal_delete_body))
            },
            confirmLabel = stringResource(R.string.journal_delete_action),
            onConfirm = {
                showDeleteConfirm = false
                selectedRowId?.let(onDeleteRecord)
                selectedRowId = null
            },
            dismissLabel = stringResource(R.string.journal_action_cancel),
            onDismiss = { showDeleteConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )

        JournalDateRangeSheet(
            visible = showDateRangeSheet,
            initialStartEpochMillis = dateRangeStart,
            initialEndEpochMillis = dateRangeEnd,
            onConfirm = { start, end ->
                dateRangeStart = start
                dateRangeEnd = end
            },
            onClear = {
                dateRangeStart = null
                dateRangeEnd = null
            },
            onDismiss = { showDateRangeSheet = false },
        )
    }
}

/**
 * Matches search against note, category label, and amount; combined with the selected category
 * filter and an optional inclusive date range (compared via [JournalDayUi.id], the same
 * year/day-of-year `dayKey` string the day was grouped by — lexicographic comparison sorts
 * correctly since the key is fixed-width zero-padded).
 */
fun filterJournalDays(
    days: List<JournalDayUi>,
    query: String,
    selectedFilterId: String,
    categoryLabelFor: (String) -> String,
    startDayKey: String? = null,
    endDayKey: String? = null,
): List<JournalDayUi> {
    val searchActive = query.isNotBlank()
    return days.mapNotNull { day ->
        if (startDayKey != null && endDayKey != null && day.id !in startDayKey..endDayKey) return@mapNotNull null
        val matches = day.rows.filter { row ->
            val matchesFilter = selectedFilterId == "all" || row.categoryId == selectedFilterId
            val matchesQuery = !searchActive ||
                row.note.contains(query, ignoreCase = true) ||
                categoryLabelFor(row.categoryId).contains(query, ignoreCase = true) ||
                row.amount.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
        if (matches.isEmpty()) null else day.copy(rows = matches)
    }
}

private fun detailStateFor(
    rowId: String,
    days: List<JournalDayUi>,
    categoryNames: Map<String, String>,
): JournalDetailUiState {
    val row = days.flatMap { it.rows }.firstOrNull { it.id == rowId }
    val categoryId = row?.categoryId ?: "food"
    return JournalDetailUiState(
        id = rowId,
        categoryId = categoryId,
        // Real category name for custom categories (US-HIS-5) — the raw id (e.g. a generated
        // "coffee-<timestamp>" slug) is not a label.
        categoryLabel = (categoryNames[categoryId] ?: expenseCategoryLabel(categoryId)).uppercase(),
        amountLabel = row?.amount.orEmpty(),
        dateTimeLabel = row?.detailDateTimeLabel.orEmpty(),
        note = row?.note.orEmpty(),
        linkedTag = row?.tag?.let { title ->
            JournalLinkedTagUi(title = title, meta = row.tagSubtitle.orEmpty())
        },
    )
}

@Preview(
    name = "Journal flow — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalFlowPreview() {
    ProExpenseTheme {
        JournalFlow(
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}
