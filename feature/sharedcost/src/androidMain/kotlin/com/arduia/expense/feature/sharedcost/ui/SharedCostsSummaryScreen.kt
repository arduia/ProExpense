package com.arduia.expense.feature.sharedcost.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostParticipantRow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummary
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummaryCustom
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsSummaryScreen(
    state: SharedCostUiState,
    onBack: () -> Unit,
    onSwitchToCustom: () -> Unit,
    onSave: () -> Unit,
    onRecordAsTransactionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backLabel: String = stringResource(R.string.shared_back_split),
    // A previously saved split is immutable except via the actions menu (US-SHC-5 Scenario 2) —
    // viewing it from history must not offer Save or the switch-to-custom edit path, only the
    // new-split flow does. Edit/archive/delete for a saved split live behind `onMore` instead.
    readOnly: Boolean = false,
    homeCurrencySymbol: String = "$",
    onMore: (() -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    // The per-person amount is only meaningful as a single figure when every share is equal —
    // a Custom split has no one "each person pays" number, so it's dropped from the headline
    // entirely rather than showing just the first participant's (possibly unrepresentative) share.
    val perPersonDisplay =
        if (state.mode == SharedSplitMode.Equal) {
            state.participants.firstOrNull()?.shareLabel
        } else {
            null
        }
    // formatMoney (not formatDisplay) drops a zero fraction ("$120" not "$120.00") — matters
    // most for a saved/reloaded split, whose rawTotal always arrives "%.2f"-formatted.
    // totalDisplay stays symbol-less: AmountDisplay (below) prepends homeCurrencySymbol itself,
    // and the Save button re-concatenates it manually — sumDisplay has neither, so it needs the
    // symbol baked in directly.
    val totalDisplay = AmountInput.formatMoney(SharedCostSplitLogic.totalCents(state.rawTotal))
    val sumDisplay =
        AmountInput.formatMoney(SharedCostSplitLogic.customShareSumCents(state.shareRaws), homeCurrencySymbol)
    val modeLabel =
        when (state.mode) {
            SharedSplitMode.Equal -> stringResource(R.string.shared_split_mode_equal)
            SharedSplitMode.Custom -> stringResource(R.string.shared_split_custom).lowercase()
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
    ) {
        ProFlatHeader(
            title = stringResource(R.string.shared_summary_title),
            eyebrow = stringResource(R.string.shared_bill_splitter),
            onBack = onBack,
            backContentDescription = backLabel,
            modifier = Modifier.padding(vertical = dimens.space14),
            trailing = {
                if (onMore != null) {
                    Box(
                        modifier = Modifier.proIconClickable(onClick = onMore),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(dimens.buttonSmallHeight)
                                    .clip(ProExpenseTheme.shapes.tile)
                                    .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.tile)
                                    .background(colors.surface),
                            contentAlignment = Alignment.Center,
                        ) {
                            ProIcon(
                                glyph = ProIconGlyph.More,
                                contentDescription = stringResource(R.string.shared_more_action),
                                tint = colors.onSurfaceVariant,
                                size = dimens.iconInline,
                            )
                        }
                    }
                }
            },
        )

        val noteTitle = state.note.trim()
        if (noteTitle.isNotEmpty()) {
            Text(
                text = noteTitle,
                style = typography.sectionHead,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }

        AmountDisplay(
            amountText = totalDisplay,
            currencySymbol = homeCurrencySymbol,
            isZero = false,
            eyebrowText = stringResource(R.string.shared_total_bill),
            usePrimaryAmount = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space8, bottom = dimens.space8),
        )

        // Rendered below the total rather than as AmountDisplay's trailing — sharing the total's
        // row meant a long, shrunk-to-fit total could crowd or run out of width against these
        // figures. Sum (of individual shares) always shows — it's the one figure that stays
        // meaningful for a Custom split, whose shares aren't rebalanced to the total. Per person
        // is additionally shown only when it's a single representative number, i.e. an Equal split.
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.space16),
            modifier = Modifier.padding(bottom = dimens.space8),
        ) {
            if (perPersonDisplay != null) {
                Column {
                    Text(
                        text = stringResource(R.string.shared_per_person),
                        style = typography.eyebrow,
                        color = colors.onSurfaceMuted,
                    )
                    Text(
                        text = perPersonDisplay,
                        style = typography.detailsAmount,
                        color = colors.onSurface,
                    )
                }
            }
            Column {
                Text(
                    text = stringResource(R.string.shared_split_sum),
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = sumDisplay,
                    style = typography.detailsAmount,
                    color = colors.onSurface,
                )
            }
        }

        Text(
            text =
                stringResource(
                    R.string.shared_summary_meta,
                    SharedCostSplitLogic.formatRawTotal(state.rawTotal, homeCurrencySymbol),
                    state.peopleCount,
                    modeLabel,
                ),
            style = typography.caption,
            color = colors.onSurfaceMuted,
            modifier = Modifier.padding(bottom = dimens.space16),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .border(BorderStroke(1.dp, colors.line), shape)
                    .background(colors.surface)
                    .padding(horizontal = dimens.cardPadding),
        ) {
            val nameTemplate = stringResource(R.string.shared_default_person_name)
            val firstPersonName = stringResource(R.string.shared_default_person_you)
            state.participants.forEachIndexed { index, participant ->
                SharedCostParticipantRow(
                    index = index + 1,
                    name =
                        participant.name
                            .trim()
                            .ifEmpty {
                                SharedCostSplitLogic.defaultParticipantName(index + 1, nameTemplate, firstPersonName)
                            },
                    amount = participant.shareLabel,
                )
            }
        }

        if (!readOnly && state.mode == SharedSplitMode.Equal) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space16),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.shared_uneven_split_prompt),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
                ProTextAction(
                    text = stringResource(R.string.shared_switch_to_custom),
                    onClick = onSwitchToCustom,
                    style = typography.caption,
                    color = colors.primary,
                )
            }
        }

        if (!readOnly) {
            SharedCostRecordAsTransactionRow(
                checked = state.recordAsTransaction,
                onCheckedChange = onRecordAsTransactionChange,
                modifier = Modifier.padding(top = dimens.space16),
            )
            ProButton(
                text = stringResource(R.string.shared_save_split_amount, "$homeCurrencySymbol$totalDisplay"),
                onClick = onSave,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                modifier = Modifier.padding(top = dimens.space20),
            )
        }
    }
}

@Composable
private fun SharedCostRecordAsTransactionRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            Text(
                text = stringResource(R.string.shared_record_as_transaction_label),
                style = typography.bodyMedium,
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.shared_record_as_transaction_caption),
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = colors.onPrimaryWarm,
                    checkedTrackColor = colors.primary,
                    uncheckedThumbColor = colors.surface,
                    uncheckedTrackColor = colors.lineStrong,
                    uncheckedBorderColor = colors.lineStrong,
                ),
        )
    }
}

@Preview(
    name = "Shared costs — summary",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsSummaryPreview() {
    ProExpenseTheme {
        SharedCostsSummaryScreen(
            state = previewSharedSummary,
            onBack = {},
            onSwitchToCustom = {},
            onSave = {},
            onRecordAsTransactionChange = {},
        )
    }
}

@Preview(
    name = "Shared costs — summary (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsSummaryDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        SharedCostsSummaryScreen(
            state = previewSharedSummary,
            onBack = {},
            onSwitchToCustom = {},
            onSave = {},
            onRecordAsTransactionChange = {},
        )
    }
}

@Preview(
    name = "Shared costs — summary, saved (More menu)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsSummaryReadOnlyPreview() {
    ProExpenseTheme {
        SharedCostsSummaryScreen(
            state = previewSharedSummary,
            onBack = {},
            onSwitchToCustom = {},
            onSave = {},
            onRecordAsTransactionChange = {},
            readOnly = true,
            backLabel = stringResource(R.string.shared_back_history),
            onMore = {},
        )
    }
}

/** Custom split: no single "per person" figure applies, so only Sum shows below the total. */
@Preview(
    name = "Shared costs — summary, custom",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsSummaryCustomPreview() {
    ProExpenseTheme {
        SharedCostsSummaryScreen(
            state = previewSharedSummaryCustom,
            onBack = {},
            onSwitchToCustom = {},
            onSave = {},
            onRecordAsTransactionChange = {},
        )
    }
}

@Preview(
    name = "Shared costs — summary, record as expense on",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsSummaryRecordAsTransactionOnPreview() {
    ProExpenseTheme {
        SharedCostsSummaryScreen(
            state = previewSharedSummary.copy(recordAsTransaction = true),
            onBack = {},
            onSwitchToCustom = {},
            onSave = {},
            onRecordAsTransactionChange = {},
        )
    }
}
