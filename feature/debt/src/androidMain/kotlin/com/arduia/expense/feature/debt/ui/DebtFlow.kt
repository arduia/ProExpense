package com.arduia.expense.feature.debt.ui

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.debt.R
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.feature.debt.ui.preview.DebtAddFormState
import com.arduia.expense.feature.debt.ui.preview.DebtDetailUiState
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtLentDetail
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe
import com.arduia.expense.feature.debt.ui.preview.previewDebtOweDetail
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

@Composable
fun DebtFlow(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    lentList: DebtListUiState = previewDebtLent,
    oweList: DebtListUiState = previewDebtOwe,
    detailFor: (String) -> DebtDetailUiState? = ::previewDebtDetailFor,
    onCreateRecord: (side: DebtSide, person: String, amountRaw: String) -> Unit = { _, _, _ -> },
    onMarkSettled: (String) -> Unit = {},
    onDeleteRecord: (String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var side by remember { mutableStateOf(DebtSide.Lent) }
    var selectedRecordId by remember { mutableStateOf<String?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var addForm by remember { mutableStateOf(DebtAddFormState()) }
    var conflictPerson by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<DebtRecordUi?>(null) }

    val listState = if (side == DebtSide.Lent) lentList else oweList

    fun otherSideName(name: String): Boolean {
        val other = if (addForm.side == DebtSide.Lent) oweList else lentList
        return name.isNotBlank() && other.active.any { it.name.equals(name.trim(), ignoreCase = true) }
    }

    fun commitNewRecord() {
        onCreateRecord(addForm.side, addForm.person.trim(), addForm.amountRaw)
        side = addForm.side
        showAdd = false
    }

    Box(
        modifier = modifier
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
            } else {
                val detail = detailFor(recordId)
                if (detail != null) {
                    DebtDetailScreen(
                        state = detail,
                        onBack = { selectedRecordId = null },
                        onMore = {},
                        onEdit = {},
                        onMarkSettled = {
                            onMarkSettled(recordId)
                            selectedRecordId = null
                        },
                    )
                }
            }
        }

        ProBottomSheetHost(
            visible = showAdd,
            title = stringResource(R.string.debt_new_record),
            onClose = { showAdd = false },
        ) {
            DebtAddSheetContent(
                form = addForm,
                onSideSelected = { addForm = addForm.copy(side = it) },
                onPersonChange = { addForm = addForm.copy(person = it) },
                onAmountChange = { addForm = addForm.copy(amountRaw = it) },
                onPickDate = {},
                onPickDue = {},
                onSave = {
                    if (otherSideName(addForm.person)) {
                        conflictPerson = addForm.person.trim()
                    } else {
                        commitNewRecord()
                    }
                },
            )
        }

        ProAlertDialog(
            visible = conflictPerson != null,
            icon = ProIconGlyph.User,
            iconTint = colors.warning,
            iconBackground = colors.warningTint,
            title = stringResource(R.string.debt_conflict_title, conflictPerson.orEmpty()),
            body = conflictBody(
                person = conflictPerson.orEmpty(),
                addingSide = addForm.side,
            ),
            confirmLabel = stringResource(R.string.debt_continue),
            onConfirm = {
                conflictPerson = null
                commitNewRecord()
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
            body = buildAnnotatedString {
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
private fun conflictBody(person: String, addingSide: DebtSide) = buildAnnotatedString {
    val otherSideLabel = stringResource(
        if (addingSide == DebtSide.Lent) R.string.debt_tab_owe else R.string.debt_tab_lent,
    )
    val addingSideLabel = stringResource(
        if (addingSide == DebtSide.Lent) R.string.debt_tab_lent else R.string.debt_tab_owe,
    )
    val emphasized = setOf("\"$otherSideLabel\"", "\"$addingSideLabel\"")
    val raw = stringResource(
        R.string.debt_conflict_body,
        person,
        "\"$otherSideLabel\"",
        "\"$addingSideLabel\"",
    )
    // Bold the quoted side labels wherever they land in the localized sentence.
    var cursor = 0
    while (cursor < raw.length) {
        val next = emphasized
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

/** Preview/screenshot fallback detail resolver used when no real debt source is wired. */
private fun previewDebtDetailFor(id: String): DebtDetailUiState = when (id) {
    "david" -> previewDebtOweDetail
    else -> previewDebtLentDetail.copy(id = id)
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
