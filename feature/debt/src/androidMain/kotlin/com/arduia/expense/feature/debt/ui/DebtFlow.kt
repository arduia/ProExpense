package com.arduia.expense.feature.debt.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.debt.R
import com.arduia.expense.feature.debt.ui.preview.DEBT_NOTE_MAX
import com.arduia.expense.feature.debt.ui.preview.DebtAddFormState
import com.arduia.expense.feature.debt.ui.preview.DebtDetailUiState
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe
import com.arduia.expense.ui.design.DatePickerScreen
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.launch

@Composable
fun DebtFlow(
    onDismiss: () -> Unit,
    lentState: DebtListUiState = previewDebtLent,
    oweState: DebtListUiState = previewDebtOwe,
    onSaveRecord: (
        side: DebtSide,
        person: String,
        amountRaw: String,
        dueEpochMillis: Long?,
        note: String,
        recordAsTransaction: Boolean,
    ) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateRecord: (
        id: String,
        person: String,
        amountRaw: String,
        dueEpochMillis: Long?,
        note: String,
        recordAsTransaction: Boolean,
    ) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteRecord: (String) -> Unit = {},
    onSettleRecord: (String) -> Unit = {},
    onCheckConflict: suspend (person: String, side: DebtSide) -> Boolean = { _, _ -> false },
    // Deep-open a specific record's detail on first composition — set by Recents/Journal when a
    // Debt-kind row is tapped, mirroring Shared Costs' `initialViewingId`.
    initialSelectedRecordId: String? = null,
    // Non-null only when initialSelectedRecordId came from a deep link whose origin (Journal, Home
    // Recents) should be returned to directly on back, instead of DebtListScreen.
    deepLinkBackLabel: String? = null,
    onDeepLinkBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val scope = rememberCoroutineScope()

    var side by remember { mutableStateOf(DebtSide.Lent) }
    var selectedRecordId by remember { mutableStateOf(initialSelectedRecordId) }
    // `side` always started as Lent, so a deep link into an Owe record (Recents/Journal tapping a
    // Debt-kind row) searched the wrong list in detailStateFor and rendered a blank detail. Once
    // the target record's real side is known — lentState/oweState load asynchronously — snap
    // `side` to it, once, without fighting the user's own tab taps afterward.
    var hasAppliedInitialSide by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialSelectedRecordId, lentState, oweState) {
        if (hasAppliedInitialSide || initialSelectedRecordId == null) return@LaunchedEffect
        val isOwe = (oweState.active + oweState.settled).any { it.id == initialSelectedRecordId }
        val isLent = (lentState.active + lentState.settled).any { it.id == initialSelectedRecordId }
        if (isOwe || isLent) {
            side = if (isOwe) DebtSide.Owe else DebtSide.Lent
            hasAppliedInitialSide = true
        }
    }
    var showAdd by remember { mutableStateOf(false) }
    var addForm by remember { mutableStateOf(DebtAddFormState()) }
    var showDuePicker by remember { mutableStateOf(false) }
    var conflictPerson by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<DebtRecordUi?>(null) }

    val listState = if (side == DebtSide.Lent) lentState else oweState

    fun commitRecord() {
        val editingId = addForm.editingId
        val note = addForm.note.trim()
        if (editingId != null) {
            onUpdateRecord(
                editingId,
                addForm.person.trim(),
                addForm.amountRaw,
                addForm.dueEpochMillis,
                note,
                addForm.recordAsTransaction,
            )
        } else {
            onSaveRecord(
                addForm.side,
                addForm.person.trim(),
                addForm.amountRaw,
                addForm.dueEpochMillis,
                note,
                addForm.recordAsTransaction,
            )
        }
        side = addForm.side
        showAdd = false
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = selectedRecordId,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = if (initialState == null) 0 else 1,
                    toIndex = if (targetState == null) 0 else 1,
                    reduceMotion = reduceMotion,
                )
            },
            label = "debtStep",
        ) { recordId ->
            if (recordId == null) {
                DebtListScreen(
                    state = listState,
                    onSideSelected = { side = it },
                    onAddRecord = {
                        addForm = DebtAddFormState(side = side)
                        showAdd = true
                    },
                    onRecordClick = { id ->
                        val record = listState.settled.firstOrNull { it.id == id }
                        if (record != null) {
                            deleteTarget = record
                        } else {
                            selectedRecordId = id
                        }
                    },
                    onBack = onDismiss,
                )
            } else if ((listState.active + listState.settled).none { it.id == recordId }) {
                // A deep link (Journal/Recents tapping a Debt-kind row) can compose before the
                // real side is resolved above, or before aggregateDebts emits — detailStateFor's
                // defaults would otherwise flash the guessed `side`'s button color (e.g. the
                // Lent-only green "Mark as settled") before the record's real side arrives one
                // frame later. Mirrors JournalFlow's same-class fix for its own detail screen.
                Box(modifier = Modifier.fillMaxSize().background(colors.paper))
            } else {
                // "Close" (shown once the debt is settled) navigates back exactly like the top
                // bar's back action — there's nothing left to settle, so it's the same exit.
                val navigateBack = {
                    if (onDeepLinkBack != null && recordId == initialSelectedRecordId) {
                        onDeepLinkBack()
                    } else {
                        selectedRecordId = null
                    }
                }
                DebtDetailScreen(
                    state = detailStateFor(recordId, side, listState),
                    backLabel =
                        if (deepLinkBackLabel != null && recordId == initialSelectedRecordId) {
                            deepLinkBackLabel
                        } else {
                            stringResource(R.string.debt_detail_back)
                        },
                    onBack = navigateBack,
                    onClose = navigateBack,
                    onMore = {},
                    onEdit = {
                        val record = (listState.active + listState.settled).firstOrNull { it.id == recordId }
                        if (record != null) {
                            addForm =
                                DebtAddFormState(
                                    side = side,
                                    person = record.name,
                                    // Whole dollars when the cents are exactly zero, otherwise a
                                    // 2-decimal string — plain integer division here used to silently
                                    // truncate any cents on every edit-reload.
                                    amountRaw =
                                        if (record.amountCents % 100 == 0L) {
                                            (record.amountCents / 100).toString()
                                        } else {
                                            String.format(java.util.Locale.US, "%.2f", record.amountCents / 100.0)
                                        },
                                    dueLabel = record.dueEpochMillis?.let { PlatformDateFormatter.shortDateLabel(it) },
                                    editingId = record.id,
                                    dueEpochMillis = record.dueEpochMillis,
                                    note = record.subtitle.orEmpty(),
                                    recordAsTransaction = record.recordAsTransaction,
                                )
                            showAdd = true
                        }
                    },
                    onMarkSettled = {
                        onSettleRecord(recordId)
                        selectedRecordId = null
                    },
                )
            }
        }

        ProBottomSheetHost(
            visible = showAdd,
            title =
                stringResource(
                    if (addForm.editingId != null) R.string.debt_edit_record else R.string.debt_new_record,
                ),
            onClose = { showAdd = false },
            // Person, amount, date, due date, note, and the record-as-transaction toggle already
            // fill almost the whole default cap (see DebtScreenshotTest.debt_add) — fullHeight
            // gives it the same room as Shared Cost's custom split sheet instead of leaving no
            // slack before the keyboard forces the whole form into a scroll.
            fullHeight = true,
        ) {
            DebtAddSheetContent(
                form = addForm,
                onSideSelected = { addForm = addForm.copy(side = it) },
                onPersonChange = { addForm = addForm.copy(person = it) },
                onAmountChange = { addForm = addForm.copy(amountRaw = it) },
                onPickDate = {},
                onPickDue = { showDuePicker = true },
                onNoteChange = { addForm = addForm.copy(note = it.take(DEBT_NOTE_MAX)) },
                onRecordAsTransactionChange = { addForm = addForm.copy(recordAsTransaction = it) },
                onSave = {
                    val person = addForm.person.trim()
                    if (addForm.editingId != null) {
                        // Editing an already-committed record isn't a new conflict to warn about.
                        commitRecord()
                    } else {
                        scope.launch {
                            if (person.isNotBlank() && onCheckConflict(person, addForm.side)) {
                                conflictPerson = person
                            } else {
                                commitRecord()
                            }
                        }
                    }
                },
            )
        }

        DatePickerScreen(
            visible = showDuePicker,
            title = stringResource(R.string.debt_due_date),
            initialEpochMillis = addForm.dueEpochMillis,
            allowClear = true,
            onApply = { millis ->
                addForm = addForm.copy(dueEpochMillis = millis, dueLabel = PlatformDateFormatter.shortDateLabel(millis))
                showDuePicker = false
            },
            onClear = {
                addForm = addForm.copy(dueEpochMillis = null, dueLabel = null)
            },
            onDismiss = { showDuePicker = false },
        )

        ProAlertDialog(
            visible = conflictPerson != null,
            icon = ProIconGlyph.User,
            iconTint = colors.warning,
            iconBackground = colors.warningTint,
            title = stringResource(R.string.debt_conflict_title, conflictPerson.orEmpty()),
            body =
                conflictBody(
                    person = conflictPerson.orEmpty(),
                    addingSide = addForm.side,
                ),
            confirmLabel = stringResource(R.string.debt_continue),
            onConfirm = {
                conflictPerson = null
                commitRecord()
            },
            dismissLabel = stringResource(R.string.debt_cancel),
            onDismiss = { conflictPerson = null },
            confirmVariant = ProButtonVariant.Warning,
        )

        ProAlertDialog(
            visible = deleteTarget != null,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.debt_delete_title),
            body =
                buildAnnotatedString {
                    append(stringResource(R.string.debt_delete_body, deleteTarget?.name.orEmpty()))
                },
            confirmLabel = stringResource(R.string.debt_delete),
            onConfirm = {
                deleteTarget?.let { onDeleteRecord(it.id) }
                deleteTarget = null
            },
            dismissLabel = stringResource(R.string.debt_cancel),
            onDismiss = { deleteTarget = null },
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}

@Composable
private fun conflictBody(
    person: String,
    addingSide: DebtSide,
) = buildAnnotatedString {
    val otherSideLabel =
        stringResource(
            if (addingSide == DebtSide.Lent) R.string.debt_tab_owe else R.string.debt_tab_lent,
        )
    val addingSideLabel =
        stringResource(
            if (addingSide == DebtSide.Lent) R.string.debt_tab_lent else R.string.debt_tab_owe,
        )
    val emphasized = setOf("\"$otherSideLabel\"", "\"$addingSideLabel\"")
    val raw =
        stringResource(
            R.string.debt_conflict_body,
            person,
            "\"$otherSideLabel\"",
            "\"$addingSideLabel\"",
        )
    // Bold the quoted side labels wherever they land in the localized sentence.
    var cursor = 0
    while (cursor < raw.length) {
        val next =
            emphasized
                .mapNotNull { token -> raw.indexOf(token, cursor).takeIf { it >= 0 }?.let { it to token } }
                .minByOrNull { it.first }
        if (next == null) {
            append(raw.substring(cursor))
            break
        }
        val (start, token) = next
        append(raw.substring(cursor, start))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(token) }
        cursor = start + token.length
    }
}

private fun detailStateFor(
    id: String,
    side: DebtSide,
    listState: DebtListUiState,
): DebtDetailUiState {
    val record = (listState.active + listState.settled).firstOrNull { it.id == id }
    return DebtDetailUiState(
        id = id,
        side = side,
        name = record?.name.orEmpty(),
        amountLabel = record?.amountLabel.orEmpty(),
        dateRecordedLabel =
            record
                ?.recordedAtEpochMillis
                ?.takeIf { it > 0L }
                ?.let { PlatformDateFormatter.shortDateLabel(it) }
                .orEmpty(),
        dueLabel = record?.dueEpochMillis?.let { PlatformDateFormatter.shortDateLabel(it) } ?: "No due date",
        statusLabel = if (record?.settled == true) "Settled" else "Active",
        settled = record?.settled ?: false,
        note = record?.subtitle,
    )
}

@Preview(
    name = "Debt flow — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtFlowPreview() {
    ProExpenseTheme {
        DebtFlow(onDismiss = {})
    }
}
