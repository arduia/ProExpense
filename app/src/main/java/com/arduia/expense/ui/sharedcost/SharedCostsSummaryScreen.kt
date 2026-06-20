package com.arduia.expense.ui.sharedcost

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SharedCostParticipantRow
import com.arduia.expense.ui.design.SharedCostSplitLogic
import com.arduia.expense.ui.design.SharedSplitMode
import com.arduia.expense.ui.preview.SharedCostUiState
import com.arduia.expense.ui.preview.previewSharedSummary
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsSummaryScreen(
    state: SharedCostUiState,
    onBack: () -> Unit,
    onSwitchToCustom: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    val perPersonDisplay = state.participants.firstOrNull()?.shareLabel
        ?.removePrefix("$")
        ?: "0"
    val totalDisplay = AmountInput.formatDisplay(state.rawTotal)
    val modeLabel = when (state.mode) {
        SharedSplitMode.Equal -> stringResource(R.string.shared_split_mode_equal)
        SharedSplitMode.Custom -> stringResource(R.string.shared_split_custom).lowercase()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding)
            .padding(bottom = dimens.space18),
    ) {
        ProTopBar(
            title = stringResource(R.string.shared_summary_title),
            onBack = onBack,
            backLabel = stringResource(R.string.shared_back_split),
        )

        AmountDisplay(
            amountText = perPersonDisplay,
            isZero = false,
            eyebrowText = stringResource(R.string.shared_each_person_pays),
            usePrimaryAmount = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8, bottom = dimens.space8),
        )

        Text(
            text = stringResource(
                R.string.shared_summary_meta,
                SharedCostSplitLogic.formatRawTotal(state.rawTotal),
                state.peopleCount,
                modeLabel,
            ),
            style = typography.caption,
            color = colors.muted,
            modifier = Modifier.padding(bottom = dimens.space16),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(BorderStroke(1.dp, colors.line), shape)
                .background(colors.surface)
                .padding(horizontal = dimens.cardPadding),
        ) {
            state.participants.forEachIndexed { index, participant ->
                SharedCostParticipantRow(
                    index = index + 1,
                    name = participant.name,
                    amount = participant.shareLabel,
                )
            }
        }

        if (state.mode == SharedSplitMode.Equal) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space16),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.shared_uneven_split_prompt),
                    style = typography.caption,
                    color = colors.muted,
                )
                ProTextAction(
                    text = stringResource(R.string.shared_switch_to_custom),
                    onClick = onSwitchToCustom,
                    style = typography.caption,
                    color = colors.primary,
                )
            }
        }

        ProButton(
            text = stringResource(R.string.shared_save_split_amount, "$$totalDisplay"),
            onClick = onSave,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier.padding(top = dimens.space24),
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
        )
    }
}
