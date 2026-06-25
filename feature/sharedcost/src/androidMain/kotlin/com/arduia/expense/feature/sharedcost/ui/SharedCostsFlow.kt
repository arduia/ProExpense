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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.ui.components.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
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
    val names: List<String> = SharedCostSplitLogic.defaultNames(2),
    val customShareRaws: List<String> = emptyList(),
    val showZeroValidation: Boolean = false,
) {
    fun toUiState(): SharedCostUiState {
        val participants = SharedCostSplitLogic.buildParticipants(
            rawTotal = rawTotal,
            peopleCount = peopleCount,
            mode = mode,
            names = names,
            customShareRaws = customShareRaws,
        ).map { (name, share) -> SharedCostParticipantUi(name, share) }
        return SharedCostUiState(
            rawTotal = rawTotal,
            note = note,
            peopleCount = peopleCount,
            mode = mode,
            participants = participants,
            showZeroValidation = showZeroValidation,
        )
    }
}

private fun SharedCostDraft.withParticipants(): SharedCostDraft = copy(
    names = SharedCostSplitLogic.syncNames(names, peopleCount),
    customShareRaws = SharedCostSplitLogic.syncCustomShares(customShareRaws, peopleCount, rawTotal),
)

private fun SharedCostUiState.toDraft(): SharedCostDraft = SharedCostDraft(
    rawTotal = rawTotal,
    note = note,
    peopleCount = peopleCount,
    mode = mode,
    names = participants.map { it.name },
    customShareRaws = shareRaws,
).withParticipants()

@Composable
fun SharedCostsFlow(
    onDismiss: () -> Unit,
    history: List<SharedCostHistoryItemUi> = previewSharedHistoryItems,
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
    modifier: Modifier = Modifier,
    savedToastMessage: String? = null,
    onSaved: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val startStep = SharedCostStep.History

    var step by rememberSaveable { mutableStateOf(startStep.name) }
    var draft by remember { mutableStateOf(SharedCostDraft().withParticipants()) }
    var viewingId by remember { mutableStateOf<String?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val defaultSplitTitle = stringResource(R.string.shared_split_default_title)

    val currentStep = SharedCostStep.valueOf(step)

    BackHandler(enabled = true) {
        when (currentStep) {
            SharedCostStep.Summary -> {
                if (viewingId != null) {
                    viewingId = null
                    draft = SharedCostDraft().withParticipants()
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
        modifier = modifier
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
                        onNewSplit = {
                            viewingId = null
                            draft = SharedCostDraft().withParticipants()
                            step = SharedCostStep.Input.name
                        },
                        onItemClick = { item ->
                            sharedCostDetails[item.id]?.let { detail ->
                                viewingId = item.id
                                draft = detail.toDraft()
                                step = SharedCostStep.Summary.name
                            }
                        },
                        onBack = onDismiss,
                    )
                }
                SharedCostStep.Input -> {
                    SharedCostsInputScreen(
                        state = draft.toUiState(),
                        onBack = {
                            if (viewingId != null) {
                                step = SharedCostStep.Summary.name
                            } else if (startStep == SharedCostStep.History) {
                                step = SharedCostStep.History.name
                            } else {
                                onDismiss()
                            }
                        },
                        onKey = { key ->
                            draft = draft.copy(
                                rawTotal = AmountInput.applyKey(draft.rawTotal, key),
                                showZeroValidation = false,
                            ).withParticipants()
                        },
                        onBackspace = {
                            draft = draft.copy(
                                rawTotal = AmountInput.applyBackspace(draft.rawTotal),
                                showZeroValidation = false,
                            ).withParticipants()
                        },
                        onNoteChange = { note -> draft = draft.copy(note = note) },
                        onDecrementPeople = {
                            if (draft.peopleCount > 2) {
                                draft = draft.copy(peopleCount = draft.peopleCount - 1).withParticipants()
                            }
                        },
                        onIncrementPeople = {
                            if (draft.peopleCount < 20) {
                                draft = draft.copy(peopleCount = draft.peopleCount + 1).withParticipants()
                            }
                        },
                        onModeSelected = { mode -> draft = draft.copy(mode = mode).withParticipants() },
                        onShareChange = { index, raw ->
                            val updated = draft.customShareRaws.toMutableList()
                            while (updated.size <= index) {
                                updated.add("")
                            }
                            updated[index] = raw
                            draft = draft.copy(customShareRaws = updated, mode = SharedSplitMode.Custom)
                        },
                        onContinue = {
                            if (SharedCostSplitLogic.canSave(draft.rawTotal)) {
                                step = SharedCostStep.Summary.name
                            } else {
                                draft = draft.copy(showZeroValidation = true)
                            }
                        },
                        showKeypad = draft.rawTotal.isEmpty() || !SharedCostSplitLogic.canSave(draft.rawTotal),
                    )
                }
                SharedCostStep.Summary -> {
                    SharedCostsSummaryScreen(
                        state = draft.toUiState(),
                        backLabel = if (viewingId != null) {
                            stringResource(R.string.shared_back_history)
                        } else {
                            stringResource(R.string.shared_back_split)
                        },
                        onBack = {
                            if (viewingId != null) {
                                viewingId = null
                                draft = SharedCostDraft().withParticipants()
                                step = SharedCostStep.History.name
                            } else {
                                step = SharedCostStep.Input.name
                            }
                        },
                        onSwitchToCustom = {
                            draft = draft.copy(mode = SharedSplitMode.Custom).withParticipants()
                            step = SharedCostStep.Input.name
                        },
                        onSave = {
                            val title = draft.note.trim().ifEmpty { defaultSplitTitle }
                            val id = viewingId
                            if (id != null) {
                                onUpdateSplit(id, title, draft.rawTotal, draft.mode, draft.names, draft.customShareRaws)
                            } else {
                                onSaveSplit(title, draft.rawTotal, draft.mode, draft.names, draft.customShareRaws)
                            }
                            toastMessage = savedToastMessage
                            onSaved()
                            viewingId = null
                            draft = SharedCostDraft().withParticipants()
                            step = SharedCostStep.History.name
                        },
                    )
                }
            }
        }

        ProToastHost(
            message = toastMessage,
            onDismiss = { toastMessage = null },
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
            onShareChange = { _, _ -> },
            onContinue = {},
            showKeypad = false,
        )
    }
}
