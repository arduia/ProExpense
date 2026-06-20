package com.arduia.expense.ui.sharedcost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.design.SharedCostCustomSplitCard
import com.arduia.expense.ui.design.SharedCostNoteField
import com.arduia.expense.ui.design.SharedCostPeopleCard
import com.arduia.expense.ui.design.SharedCostPerPersonCard
import com.arduia.expense.ui.design.SharedCostSplitLogic
import com.arduia.expense.ui.design.SharedSplitMode
import com.arduia.expense.ui.preview.SharedCostUiState
import com.arduia.expense.ui.preview.previewSharedCustomLimits
import com.arduia.expense.ui.preview.previewSharedInputEqual
import com.arduia.expense.ui.preview.previewSharedZeroValidation
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsInputScreen(
    state: SharedCostUiState,
    onBack: () -> Unit,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    onNoteChange: (String) -> Unit,
    onDecrementPeople: () -> Unit,
    onIncrementPeople: () -> Unit,
    onModeSelected: (SharedSplitMode) -> Unit,
    onShareChange: (Int, String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    showKeypad: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val displayAmount = AmountInput.formatDisplay(state.rawTotal.ifEmpty { "0" })
    val canProceed = SharedCostSplitLogic.canSave(state.rawTotal)
    val isZero = !canProceed
    val showDetails = state.rawTotal.isNotEmpty() && canProceed
    val participants = state.participants.map { it.name to it.shareLabel }
    val perPersonAmount = participants.firstOrNull()?.second ?: SharedCostSplitLogic.formatCents(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding),
    ) {
        ProTopBar(
            title = stringResource(R.string.shared_costs_title),
            onBack = onBack,
            backLabel = stringResource(R.string.shared_back_more),
        )

        AmountDisplay(
            amountText = displayAmount,
            isZero = isZero,
            showZeroValidation = state.showZeroValidation,
            zeroHelperMessage = stringResource(R.string.shared_total_zero_error),
            eyebrowText = stringResource(R.string.shared_total_bill),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8, bottom = if (showDetails) dimens.space16 else dimens.space24),
        )

        if (showDetails) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
                SharedCostNoteField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    placeholder = stringResource(R.string.shared_note_placeholder),
                )

                SharedCostPeopleCard(
                    count = state.peopleCount,
                    onDecrement = onDecrementPeople,
                    onIncrement = onIncrementPeople,
                    maxReachedHint = stringResource(R.string.shared_max_people),
                )

                if (state.mode == SharedSplitMode.Equal) {
                    SegmentedToggle(
                        options = listOf(
                            stringResource(R.string.shared_split_even),
                            stringResource(R.string.shared_split_custom),
                        ),
                        selectedIndex = 0,
                        onSelected = { index ->
                            onModeSelected(
                                if (index == 0) SharedSplitMode.Equal else SharedSplitMode.Custom,
                            )
                        },
                        usePrimarySelection = true,
                    )

                    SharedCostPerPersonCard(
                        perPersonAmount = perPersonAmount,
                        participants = participants,
                        perPersonEyebrow = stringResource(R.string.shared_per_person),
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.shared_custom_split),
                            style = typography.eyebrow,
                            color = colors.muted,
                        )
                        ProTextAction(
                            text = stringResource(R.string.shared_switch_to_equal),
                            onClick = { onModeSelected(SharedSplitMode.Equal) },
                            style = typography.caption,
                            color = colors.primary,
                        )
                    }

                    SharedCostCustomSplitCard(
                        participants = participants,
                        onShareChange = onShareChange,
                    )
                }
            }
        }

        if (showKeypad && !showDetails) {
            NumericKeypad(
                actionsEnabled = canProceed,
                onKey = onKey,
                onBackspace = onBackspace,
                onSave = onContinue,
                onNext = onContinue,
                saveLabel = stringResource(R.string.shared_save_split),
                nextLabel = stringResource(R.string.shared_save_split),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space16, bottom = dimens.space18),
            )
        } else {
            ProButton(
                text = stringResource(R.string.shared_save_split),
                onClick = onContinue,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                enabled = canProceed,
                modifier = Modifier
                    .padding(top = dimens.space16, bottom = dimens.space18),
            )
        }
    }
}

@Preview(
    name = "Shared costs — input equal",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsInputEqualPreview() {
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

@Preview(
    name = "Shared costs — zero validation",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsInputZeroPreview() {
    ProExpenseTheme {
        SharedCostsInputScreen(
            state = previewSharedZeroValidation,
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

@Preview(
    name = "Shared costs — custom limits",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsInputCustomPreview() {
    ProExpenseTheme {
        SharedCostsInputScreen(
            state = previewSharedCustomLimits,
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
