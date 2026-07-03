package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProColors
import com.arduia.expense.ui.theme.ProExpenseTheme

fun ProColors.eventBudgetBarColor(tone: EventBudgetTone): Color = when (tone) {
    EventBudgetTone.OnTrack -> primary
    EventBudgetTone.OverBudget -> warning
    EventBudgetTone.SignificantlyOver -> danger
}

data class EventBudgetSummaryState(
    val eyebrow: String,
    val remainingLabel: String,
    val spentLabel: String? = null,
    val budgetLabel: String? = null,
    val spentCaption: String? = null,
    val budgetCaption: String? = null,
    val progress: Float,
    val tone: EventBudgetTone = EventBudgetTone.OnTrack,
    val footerStartLabel: String? = null,
    val footerEndLabel: String? = null,
    val overNoticeLabel: String? = null,
    val isFinal: Boolean = false,
)

@Composable
fun EventBudgetSummaryCard(
    state: EventBudgetSummaryState,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    val accent = when {
        state.isFinal -> colors.onSurfaceMuted
        else -> colors.eventBudgetBarColor(state.tone)
    }
    val containerColor = if (state.isFinal) colors.paperAlt else colors.surface
    val borderColor = when {
        state.isFinal -> Color.Transparent
        state.tone == EventBudgetTone.OnTrack -> colors.line
        else -> accent
    }
    val trackColor = if (state.isFinal) colors.lineStrong else colors.paperAlt
    val barFill = if (state.isFinal) colors.muted2 else accent

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.card)
                } else {
                    Modifier
                },
            )
            .background(containerColor)
            .padding(dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.space10),
            ) {
                Text(
                    text = state.eyebrow,
                    style = typography.eyebrow,
                    color = if (state.isFinal || state.tone == EventBudgetTone.OnTrack) {
                        colors.onSurfaceMuted
                    } else {
                        accent
                    },
                )
                Text(
                    text = state.remainingLabel,
                    style = typography.summaryAmount,
                    color = if (state.isFinal) colors.onSurfaceVariant else accent,
                )
            }
            if (state.spentLabel != null && state.budgetLabel != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(dimens.space2),
                ) {
                    Text(
                        text = state.spentCaption.orEmpty(),
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                    Text(
                        text = state.spentLabel,
                        style = typography.bodySemiBold,
                        color = colors.onSurface,
                    )
                    Text(
                        text = state.budgetCaption.orEmpty(),
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                        modifier = Modifier.padding(top = dimens.space6),
                    )
                    Text(
                        text = state.budgetLabel,
                        style = typography.bodySemiBold,
                        color = colors.onSurface,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(ProExpenseTheme.shapes.chip)
                .background(trackColor),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(state.progress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(ProExpenseTheme.shapes.chip)
                    .background(barFill)
                    .align(Alignment.CenterStart),
            )
        }

        if (state.overNoticeLabel != null) {
            Text(
                text = state.overNoticeLabel,
                style = typography.bodySemiBold,
                color = accent,
            )
        } else if (state.footerStartLabel != null || state.footerEndLabel != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = state.footerStartLabel.orEmpty(),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = state.footerEndLabel.orEmpty(),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun EventBudgetSummaryCardPreview() {
    ProExpenseTheme {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EventBudgetSummaryCard(
                EventBudgetSummaryState(
                    eyebrow = "REMAINING",
                    remainingLabel = "$1,240",
                    spentLabel = "$760",
                    budgetLabel = "$2,000",
                    spentCaption = "Spent",
                    budgetCaption = "Budget",
                    progress = 0.38f,
                    tone = EventBudgetTone.OnTrack,
                    footerStartLabel = "38% spent",
                    footerEndLabel = "$88/day pace",
                ),
            )
            EventBudgetSummaryCard(
                EventBudgetSummaryState(
                    eyebrow = "OVER BUDGET",
                    remainingLabel = "−$22",
                    spentLabel = "$422",
                    budgetLabel = "$400",
                    spentCaption = "Spent",
                    budgetCaption = "Budget",
                    progress = 1f,
                    tone = EventBudgetTone.OverBudget,
                    overNoticeLabel = "Over budget by $22 (105%)",
                ),
            )
            EventBudgetSummaryCard(
                EventBudgetSummaryState(
                    eyebrow = "FINAL · REMAINING",
                    remainingLabel = "$340",
                    progress = 0.575f,
                    isFinal = true,
                ),
            )
        }
    }
}
