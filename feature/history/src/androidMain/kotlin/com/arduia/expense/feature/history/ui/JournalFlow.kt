package com.arduia.expense.feature.history.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.arduia.expense.feature.history.ui.preview.JournalListUiState
import com.arduia.expense.feature.history.ui.preview.JournalQuickNoteUiState
import com.arduia.expense.feature.history.ui.preview.previewJournalDetail
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

@Composable
fun JournalFlow(
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    days: List<JournalDayUi> = previewJournalList.days,
    detailFor: (String) -> JournalDetailUiState? = ::previewDetailFor,
    onEditRecord: (String) -> Unit = {},
    onDeleteRecord: (String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var query by remember { mutableStateOf("") }
    var selectedFilterId by remember { mutableStateOf("all") }
    var selectedRowId by remember { mutableStateOf<String?>(null) }
    var quickNoteRow by remember { mutableStateOf<ProTransactionRowModel?>(null) }
    var quickNoteText by remember { mutableStateOf("") }
    var showActions by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val searchActive = query.isNotBlank()
    val visibleDays = filterJournalDays(days, query, selectedFilterId)
    val listState = JournalListUiState(
        query = query,
        selectedFilterId = selectedFilterId,
        days = visibleDays,
        searchActive = searchActive,
    )

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
                        quickNoteText = ""
                    },
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                )
            } else {
                val detail = detailFor(rowId)
                if (detail != null) {
                    JournalDetailScreen(
                        state = detail,
                        onBack = { selectedRowId = null },
                        onActions = { showActions = true },
                        onLinkedTagClick = {},
                        onEdit = { onEditRecord(rowId) },
                        onDelete = { showDeleteConfirm = true },
                    )
                }
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
                    onSave = { quickNoteRow = null },
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
    }
}

/**
 * Client-side journal filtering over the supplied days: free-text search across note + meta, and the
 * category filter chips ("more" = any category outside the named chips). Empty days are dropped.
 */
private fun filterJournalDays(
    days: List<JournalDayUi>,
    query: String,
    selectedFilterId: String,
): List<JournalDayUi> {
    val trimmed = query.trim()
    if (trimmed.isBlank() && selectedFilterId == "all") return days
    return days.mapNotNull { day ->
        val rows = day.rows.filter { row ->
            matchesFilter(row.categoryId, selectedFilterId) &&
                (trimmed.isBlank() ||
                    row.note.contains(trimmed, ignoreCase = true) ||
                    row.meta.contains(trimmed, ignoreCase = true))
        }
        if (rows.isEmpty()) null else day.copy(rows = rows)
    }
}

private val namedJournalFilters = setOf("food", "transport", "bills")

private fun matchesFilter(categoryId: String, selectedFilterId: String): Boolean = when (selectedFilterId) {
    "all" -> true
    "more" -> categoryId !in namedJournalFilters
    else -> categoryId == selectedFilterId
}

/** Preview/screenshot fallback detail resolver used when no real record source is wired. */
private fun previewDetailFor(rowId: String): JournalDetailUiState = when (rowId) {
    "t1" -> previewJournalDetail
    else -> {
        val row = previewJournalList.days
            .flatMap { it.rows }
            .firstOrNull { it.id == rowId }
        JournalDetailUiState(
            id = rowId,
            categoryId = row?.categoryId ?: "food",
            categoryLabel = (row?.categoryId ?: "food").uppercase(),
            amountLabel = row?.amount.orEmpty(),
            dateTimeLabel = row?.meta.orEmpty(),
            note = row?.note.orEmpty(),
            linkedTag = null,
        )
    }
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
