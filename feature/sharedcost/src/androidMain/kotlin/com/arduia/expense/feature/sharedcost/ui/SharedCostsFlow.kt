package com.arduia.expense.feature.sharedcost.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

private enum class SharedCostStep {
    History,
    Input,
    Summary,
}

private data class SharedCostDraft(
    val rawTotal: String = "",
    val note: String = "",
    val peopleCount: Int = 2,
    val mode: SharedSplitMode = SharedSplitMode.Equal,
    // Deliberately empty, not pre-filled with English "Person N" defaults — every construction
    // site immediately calls .withParticipants(nameTemplate), and syncNames only (re)generates
    // names when the list size doesn't already match peopleCount, so a non-empty default here
    // would permanently freeze the very first render's names in the non-localized fallback.
    val names: List<String> = emptyList(),
    val customShareRaws: List<String> = emptyList(),
    val showZeroValidation: Boolean = false,
    val amountConfirmed: Boolean = false,
) {
    fun toUiState(
        currencySymbol: String,
        nameTemplate: String,
    ): SharedCostUiState {
        val participants =
            SharedCostSplitLogic
                .buildParticipants(
                    rawTotal = rawTotal,
                    peopleCount = peopleCount,
                    mode = mode,
                    names = names,
                    customShareRaws = customShareRaws,
                    currencySymbol = currencySymbol,
                    nameTemplate = nameTemplate,
                ).map { (name, share) -> SharedCostParticipantUi(name, share) }
        return SharedCostUiState(
            rawTotal = rawTotal,
            note = note,
            peopleCount = peopleCount,
            mode = mode,
            participants = participants,
            showZeroValidation = showZeroValidation,
            shareRaws = customShareRaws,
            amountConfirmed = amountConfirmed,
        )
    }
}

/**
 * Flattened into a plain [List] (Bundle-safe primitives only) so the entire in-progress split
 * survives process death — US-SHC-2's NFR promises custom shares survive it "the same as any
 * other draft input", but a bare `remember` (the previous state) is wiped on rotation or the
 * process being killed in the background.
 */
private val SharedCostDraftSaver: Saver<SharedCostDraft, Any> =
    listSaver(
        save = { draft ->
            listOf(
                draft.rawTotal,
                draft.note,
                draft.peopleCount,
                draft.mode.name,
                draft.names.size,
            ) + draft.names + listOf(draft.customShareRaws.size) + draft.customShareRaws +
                listOf(draft.showZeroValidation, draft.amountConfirmed)
        },
        restore = { saved ->
            var i = 0
            val rawTotal = saved[i++] as String
            val note = saved[i++] as String
            val peopleCount = saved[i++] as Int
            val mode = SharedSplitMode.valueOf(saved[i++] as String)
            val namesSize = saved[i++] as Int
            val names = (0 until namesSize).map { saved[i++] as String }
            val sharesSize = saved[i++] as Int
            val customShareRaws = (0 until sharesSize).map { saved[i++] as String }
            val showZeroValidation = saved[i++] as Boolean
            val amountConfirmed = saved[i++] as Boolean
            SharedCostDraft(
                rawTotal = rawTotal,
                note = note,
                peopleCount = peopleCount,
                mode = mode,
                names = names,
                customShareRaws = customShareRaws,
                showZeroValidation = showZeroValidation,
                amountConfirmed = amountConfirmed,
            )
        },
    )

private fun SharedCostDraft.withParticipants(nameTemplate: String): SharedCostDraft =
    copy(
        names = SharedCostSplitLogic.syncNames(names, peopleCount, nameTemplate),
        customShareRaws = SharedCostSplitLogic.syncCustomShares(customShareRaws, peopleCount, rawTotal),
    )

private fun SharedCostUiState.toDraft(nameTemplate: String): SharedCostDraft =
    SharedCostDraft(
        rawTotal = rawTotal,
        note = note,
        peopleCount = peopleCount,
        mode = mode,
        names = participants.map { it.name },
        customShareRaws = shareRaws,
    ).withParticipants(nameTemplate)

@Composable
fun SharedCostsFlow(
    onDismiss: () -> Unit,
    history: List<SharedCostHistoryItemUi> = previewSharedHistoryItems,
    isLoading: Boolean = false,
    sharedCostDetails: Map<String, SharedCostUiState> = emptyMap(),
    onSaveSplit: (
        title: String,
        rawTotal: String,
        mode: SharedSplitMode,
        names: List<String>,
        customShareRaws: List<String>,
    ) -> Unit = { _, _, _, _, _ -> },
    onUpdateSplit: (
        id: String,
        title: String,
        rawTotal: String,
        mode: SharedSplitMode,
        names: List<String>,
        customShareRaws: List<String>,
    ) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteSplit: (id: String) -> Unit = {},
    modifier: Modifier = Modifier,
    savedToastMessage: String? = null,
    onSaved: () -> Unit = {},
    homeCurrencySymbol: String = "$",
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val startStep = SharedCostStep.History
    val nameTemplate = stringResource(R.string.shared_default_person_name)

    var step by rememberSaveable { mutableStateOf(startStep.name) }
    // Not seeding via withParticipants() here: rawTotal is empty at this point, and
    // syncCustomShares only reseeds while the share list size differs from peopleCount — an
    // early seed off an empty/zero total would freeze custom shares at "0" forever, never
    // reflecting the total once it's actually entered. onConfirmAmount seeds for real.
    var draft by rememberSaveable(stateSaver = SharedCostDraftSaver) {
        mutableStateOf(SharedCostDraft())
    }
    var viewingId by remember { mutableStateOf<String?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<SharedCostHistoryItemUi?>(null) }
    // editingIndex is deliberately not cleared when the sheet closes (Done / last-person Next) —
    // ProBottomSheetHost's exit fade keeps rendering `sheetContent()` for its duration, and
    // nulling the index immediately would flash person 0 mid-fade. editSheetVisible drives
    // visibility; onEditPerson sets a fresh editingIndex before the sheet opens again.
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editSheetVisible by remember { mutableStateOf(false) }
    val defaultSplitTitle = stringResource(R.string.shared_split_default_title)

    val currentStep = SharedCostStep.valueOf(step)

    fun updateParticipantName(
        index: Int,
        name: String,
    ) {
        val updated = SharedCostSplitLogic.syncNames(draft.names, draft.peopleCount, nameTemplate).toMutableList()
        if (index in updated.indices) {
            updated[index] = name
            draft = draft.copy(names = updated)
        }
    }

    fun applyCustomShareEdit(
        index: Int,
        transform: (String) -> String,
    ) {
        val updated = SharedCostSplitLogic.syncCustomShares(draft.customShareRaws, draft.peopleCount, draft.rawTotal).toMutableList()
        while (updated.size <= index) {
            updated.add("")
        }
        updated[index] = transform(updated[index])
        draft = draft.copy(customShareRaws = updated)
    }

    BackHandler(enabled = true) {
        if (editSheetVisible) {
            editSheetVisible = false
            return@BackHandler
        }
        when (currentStep) {
            SharedCostStep.Summary -> {
                if (viewingId != null) {
                    // Not resetting `draft` or `viewingId` here: the outgoing Summary content
                    // still reads them while AnimatedContent's exit transition plays. Clearing
                    // `draft` would flash an empty split; clearing `viewingId` would flip
                    // `readOnly` to false mid-transition, flashing the Save button on a split
                    // that's supposed to be view-only. `onNewSplit` and `onItemClick` already set
                    // a fresh `viewingId`/`draft` before the next Input/Summary show.
                    step = SharedCostStep.History.name
                } else {
                    step = SharedCostStep.Input.name
                }
            }
            SharedCostStep.Input -> {
                if (viewingId != null) {
                    step = SharedCostStep.Summary.name
                } else if (startStep == SharedCostStep.History) {
                    step = SharedCostStep.History.name
                } else {
                    onDismiss()
                }
            }
            SharedCostStep.History -> {
                onDismiss()
            }
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = initialState.ordinal,
                    toIndex = targetState.ordinal,
                    reduceMotion = reduceMotion,
                )
            },
            label = "sharedCostStep",
        ) { target ->
            when (target) {
                SharedCostStep.History -> {
                    SharedCostsHistoryScreen(
                        items = history,
                        isLoading = isLoading,
                        onNewSplit = {
                            // Not seeding via withParticipants() here — see the initial `draft`
                            // declaration above for why an empty-total seed would stick forever.
                            viewingId = null
                            draft = SharedCostDraft()
                            step = SharedCostStep.Input.name
                        },
                        onItemClick = { item ->
                            sharedCostDetails[item.id]?.let { detail ->
                                viewingId = item.id
                                draft = detail.toDraft(nameTemplate)
                                step = SharedCostStep.Summary.name
                            }
                        },
                        onBack = onDismiss,
                        onDeleteRequested = { deleteTarget = it },
                    )
                }
                SharedCostStep.Input -> {
                    SharedCostsInputScreen(
                        state = draft.toUiState(homeCurrencySymbol, nameTemplate),
                        homeCurrencySymbol = homeCurrencySymbol,
                        onBack = {
                            if (viewingId != null) {
                                step = SharedCostStep.Summary.name
                            } else if (startStep == SharedCostStep.History) {
                                step = SharedCostStep.History.name
                            } else {
                                onDismiss()
                            }
                        },
                        // Total is still being typed here — the amount isn't final yet, so this
                        // must not seed names/shares (withParticipants) off a partial rawTotal.
                        // syncCustomShares only reseeds while list size differs from peopleCount,
                        // so an early seed off e.g. "1" (mid-keystroke toward "120") would freeze
                        // custom shares at the wrong amount forever — reseeding happens once, in
                        // onConfirmAmount, off the finished total instead.
                        onKey = { key ->
                            draft =
                                draft.copy(
                                    rawTotal = AmountInput.applyKey(draft.rawTotal, key),
                                    showZeroValidation = false,
                                )
                        },
                        onBackspace = {
                            draft =
                                draft.copy(
                                    rawTotal = AmountInput.applyBackspace(draft.rawTotal),
                                    showZeroValidation = false,
                                )
                        },
                        onNoteChange = { note -> draft = draft.copy(note = note) },
                        onDecrementPeople = {
                            if (draft.peopleCount > 2) {
                                draft = draft.copy(peopleCount = draft.peopleCount - 1).withParticipants(nameTemplate)
                            }
                        },
                        onIncrementPeople = {
                            if (draft.peopleCount < 20) {
                                draft = draft.copy(peopleCount = draft.peopleCount + 1).withParticipants(nameTemplate)
                            }
                        },
                        onModeSelected = { mode -> draft = draft.copy(mode = mode).withParticipants(nameTemplate) },
                        onEditPerson = { index ->
                            editingIndex = index
                            editSheetVisible = true
                        },
                        onConfirmAmount = {
                            if (SharedCostSplitLogic.canSave(draft.rawTotal)) {
                                draft = draft.copy(amountConfirmed = true).withParticipants(nameTemplate)
                            } else {
                                draft = draft.copy(showZeroValidation = true)
                            }
                        },
                        onEditAmount = { draft = draft.copy(amountConfirmed = false) },
                        onContinue = {
                            if (SharedCostSplitLogic.canSave(draft.rawTotal)) {
                                step = SharedCostStep.Summary.name
                            } else {
                                draft = draft.copy(showZeroValidation = true)
                            }
                        },
                        showKeypad = true,
                    )
                }
                SharedCostStep.Summary -> {
                    SharedCostsSummaryScreen(
                        state = draft.toUiState(homeCurrencySymbol, nameTemplate),
                        homeCurrencySymbol = homeCurrencySymbol,
                        readOnly = viewingId != null,
                        backLabel =
                            if (viewingId != null) {
                                stringResource(R.string.shared_back_history)
                            } else {
                                stringResource(R.string.shared_back_split)
                            },
                        onBack = {
                            if (viewingId != null) {
                                // Not resetting `draft` or `viewingId` here: the outgoing Summary
                                // content still reads them while AnimatedContent's exit transition
                                // plays. Clearing `draft` would flash a zero/empty split; clearing
                                // `viewingId` would flip `readOnly` to false mid-transition,
                                // flashing the Save button on a split that's supposed to be
                                // view-only. `onNewSplit` and `onItemClick` already set a fresh
                                // `viewingId`/`draft` before the next Input/Summary show.
                                step = SharedCostStep.History.name
                            } else {
                                step = SharedCostStep.Input.name
                            }
                        },
                        onSwitchToCustom = {
                            draft = draft.copy(mode = SharedSplitMode.Custom).withParticipants(nameTemplate)
                            step = SharedCostStep.Input.name
                        },
                        onSave = {
                            val title = draft.note.trim().ifEmpty { defaultSplitTitle }
                            // Cleared name fields fall back to "Person N" — blank participant
                            // names must never be persisted (US-SHC-1 default-naming rule).
                            val names = SharedCostSplitLogic.resolveNames(draft.names, draft.peopleCount, nameTemplate)
                            val id = viewingId
                            if (id != null) {
                                onUpdateSplit(id, title, draft.rawTotal, draft.mode, names, draft.customShareRaws)
                            } else {
                                onSaveSplit(title, draft.rawTotal, draft.mode, names, draft.customShareRaws)
                            }
                            toastMessage = savedToastMessage
                            onSaved()
                            // Not resetting `draft` here: the outgoing Summary content still reads
                            // it while AnimatedContent's exit transition plays, so clearing it now
                            // would flash an empty split for the transition's duration. `onNewSplit`
                            // and `onItemClick` already set a fresh `draft` before the next show.
                            viewingId = null
                            step = SharedCostStep.History.name
                        },
                    )
                }
            }
        }

        ProBottomSheetHost(
            visible = editSheetVisible,
            title = stringResource(R.string.shared_edit_person_title),
            onClose = { editSheetVisible = false },
            fullHeight = true,
        ) {
            val index = editingIndex ?: 0
            val equalShareLabel =
                SharedCostSplitLogic.formatCents(
                    SharedCostSplitLogic.equalShareCents(draft.rawTotal, draft.peopleCount),
                    homeCurrencySymbol,
                )
            SharedCostsEditPersonSheetContent(
                people = draft.toUiState(homeCurrencySymbol, nameTemplate).participants,
                activeIndex = index,
                mode = draft.mode,
                activeAmountRaw = draft.customShareRaws.getOrElse(index) { "" },
                equalShareLabel = equalShareLabel,
                onPickPerson = { editingIndex = it },
                onNameChange = { name -> updateParticipantName(index, name) },
                onAmountKey = { key, freshEntry ->
                    applyCustomShareEdit(index) { current ->
                        AmountInput.applyKey(if (freshEntry) "" else current, key)
                    }
                },
                onAmountBackspace = { freshEntry ->
                    applyCustomShareEdit(index) { current ->
                        if (freshEntry) "" else AmountInput.applyBackspace(current)
                    }
                },
                onDone = { editSheetVisible = false },
                onNext = {
                    if (index < draft.peopleCount - 1) {
                        editingIndex = index + 1
                    } else {
                        editSheetVisible = false
                        step = SharedCostStep.Summary.name
                    }
                },
                homeCurrencySymbol = homeCurrencySymbol,
            )
        }

        ProToastHost(
            message = toastMessage,
            onDismiss = { toastMessage = null },
        )

        ProAlertDialog(
            visible = deleteTarget != null,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.shared_delete_title),
            body =
                buildAnnotatedString {
                    append(stringResource(R.string.shared_delete_body, deleteTarget?.title.orEmpty()))
                },
            confirmLabel = stringResource(R.string.shared_delete_confirm),
            onConfirm = {
                deleteTarget?.let { onDeleteSplit(it.id) }
                deleteTarget = null
            },
            dismissLabel = stringResource(R.string.shared_delete_cancel),
            onDismiss = { deleteTarget = null },
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}

@Preview(
    name = "Shared costs flow — history",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsFlowHistoryPreview() {
    ProExpenseTheme {
        SharedCostsFlow(onDismiss = {})
    }
}

@Preview(
    name = "Shared costs flow — input",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsFlowInputPreview() {
    ProExpenseTheme {
        SharedCostsInputScreen(
            state = previewSharedInputEqual,
            onBack = {},
            onKey = {},
            onBackspace = {},
            onNoteChange = {},
            onDecrementPeople = {},
            onIncrementPeople = {},
            onModeSelected = {},
            onContinue = {},
            showKeypad = false,
        )
    }
}
