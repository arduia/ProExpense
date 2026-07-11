package com.arduia.expense.feature.sharedcost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostNoteField
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostPeopleCard
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostPerPersonCard
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedCustomLimits
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputConfirmed
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedZeroValidation
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.design.proIconClickable
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
    onContinue: () -> Unit,
    onConfirmAmount: () -> Unit = {},
    onEditAmount: () -> Unit = {},
    onEditPerson: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    showKeypad: Boolean = true,
    homeCurrencySymbol: String = "$",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val keyboardController = LocalSoftwareKeyboardController.current
    val noteFocusRequester = remember { FocusRequester() }
    val displayAmount = AmountInput.formatDisplay(state.rawTotal.ifEmpty { "0" })
    val canProceed = SharedCostSplitLogic.canSave(state.rawTotal)
    val isZero = !canProceed
    val showDetails = state.amountConfirmed && canProceed

    // Fires once per false->true edge (right after the total is confirmed) — not on every
    // recomposition while details stay visible, so it never steals focus back while typing.
    LaunchedEffect(showDetails) {
        if (showDetails && state.note.isEmpty()) {
            noteFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    val participants = state.participants.map { it.name to it.shareLabel }
    val perPersonAmount =
        participants.firstOrNull()?.second
            ?: SharedCostSplitLogic.formatCents(0, homeCurrencySymbol)
    val customSumCents = SharedCostSplitLogic.customShareSumCents(state.shareRaws)
    val customMatchesTotal = customSumCents == SharedCostSplitLogic.totalCents(state.rawTotal)
    val headerEyebrow =
        if (state.mode == SharedSplitMode.Equal) {
            stringResource(R.string.shared_per_person)
        } else {
            stringResource(R.string.shared_custom_shares_label)
        }
    val headerAmount =
        if (state.mode == SharedSplitMode.Equal) {
            perPersonAmount
        } else {
            SharedCostSplitLogic.formatCents(customSumCents, homeCurrencySymbol)
        }
    val headerCaption =
        if (state.mode == SharedSplitMode.Custom) {
            if (customMatchesTotal) {
                stringResource(R.string.shared_custom_matches_total)
            } else {
                stringResource(
                    R.string.shared_custom_vs_total,
                    SharedCostSplitLogic.formatRawTotal(state.rawTotal, homeCurrencySymbol),
                )
            }
        } else {
            null
        }
    val splitModeHint =
        if (state.mode == SharedSplitMode.Equal) {
            stringResource(R.string.shared_split_mode_hint_equal)
        } else {
            stringResource(R.string.shared_split_mode_hint_custom)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                // Name/amount fields below open the system IME — without this, the keyboard
                // simply overlaps the Save button instead of the screen shifting up above it.
                .imePadding()
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            ProTopBar(
                title = stringResource(R.string.shared_costs_title),
                onBack = onBack,
                backLabel = stringResource(R.string.shared_back_more),
            )

            AmountDisplay(
                amountText = displayAmount,
                currencySymbol = homeCurrencySymbol,
                isZero = isZero,
                showZeroValidation = state.showZeroValidation,
                zeroHelperMessage = stringResource(R.string.shared_total_zero_error),
                eyebrowText = stringResource(R.string.shared_total_bill),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8, bottom = if (showDetails) dimens.space16 else dimens.space24),
                trailing =
                    if (showDetails) {
                        {
                            ProIcon(
                                glyph = ProIconGlyph.Edit,
                                contentDescription = stringResource(R.string.shared_edit_amount_cd),
                                tint = colors.primary,
                                size = dimens.iconInline,
                                modifier = Modifier.proIconClickable(onClick = onEditAmount),
                            )
                        }
                    } else {
                        null
                    },
            )

            if (showDetails) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
                    SharedCostNoteField(
                        value = state.note,
                        onValueChange = onNoteChange,
                        placeholder = stringResource(R.string.shared_note_placeholder),
                        focusRequester = noteFocusRequester,
                    )

                    SharedCostPeopleCard(
                        count = state.peopleCount,
                        onDecrement = onDecrementPeople,
                        onIncrement = onIncrementPeople,
                        maxReachedHint = stringResource(R.string.shared_max_people),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(dimens.space6)) {
                        SegmentedToggle(
                            options =
                                listOf(
                                    stringResource(R.string.shared_split_even),
                                    stringResource(R.string.shared_split_custom),
                                ),
                            selectedIndex = if (state.mode == SharedSplitMode.Equal) 0 else 1,
                            onSelected = { index ->
                                onModeSelected(
                                    if (index == 0) SharedSplitMode.Equal else SharedSplitMode.Custom,
                                )
                            },
                            usePrimarySelection = true,
                        )
                        Text(
                            text = splitModeHint,
                            style = ProExpenseTheme.typography.caption,
                            color = colors.onSurfaceMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    val editPersonTemplate = stringResource(R.string.shared_edit_person_cd)
                    val personEditDescriptions =
                        remember(participants, editPersonTemplate) {
                            participants.map { (name, _) -> editPersonTemplate.replace("%1\$s", name) }
                        }

                    SharedCostPerPersonCard(
                        headerEyebrow = headerEyebrow,
                        headerAmount = headerAmount,
                        participants = participants,
                        headerCaption = headerCaption,
                        headerCaptionEmphasized = customMatchesTotal,
                        onHeaderEditClick = { onEditPerson(0) },
                        headerEditContentDescription = stringResource(R.string.shared_edit_split_cd),
                        onPersonEditClick = onEditPerson,
                        personEditContentDescription = { index -> personEditDescriptions.getOrElse(index) { "" } },
                    )
                }
            }
        }

        if (showKeypad && !showDetails) {
            NumericKeypad(
                actionsEnabled = canProceed,
                onKey = onKey,
                onBackspace = onBackspace,
                onSave = onConfirmAmount,
                onNext = onConfirmAmount,
                nextLabel = stringResource(R.string.shared_next),
                showSaveAction = false,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space16),
            )
        } else {
            ProButton(
                text = stringResource(R.string.shared_review),
                onClick = onContinue,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                enabled = canProceed,
                modifier = Modifier.padding(top = dimens.space16),
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
            onContinue = {},
            showKeypad = false,
        )
    }
}

@Preview(
    name = "Shared costs — input confirmed",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsInputConfirmedPreview() {
    ProExpenseTheme {
        SharedCostsInputScreen(
            state = previewSharedInputConfirmed,
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
            onContinue = {},
            showKeypad = false,
        )
    }
}
