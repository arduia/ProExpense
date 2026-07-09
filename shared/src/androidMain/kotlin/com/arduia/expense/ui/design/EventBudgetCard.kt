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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme

data class EventBudgetCardState(
    val id: String = "",
    val title: String,
    val dateRange: String,
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val isOverBudget: Boolean = false,
    val overAmountLabel: String? = null,
    val dailyAverageLabel: String? = null,
    /** Rounded percent over 100 (US-EVT-3): 0 normal, 1-10 warning (amber), >10 danger (red, bold). */
    val overBudgetPercent: Int = 0,
)

@Composable
fun EventBudgetCard(
    state: EventBudgetCardState,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val isDanger = state.overBudgetPercent > 10
    val isWarning = state.overBudgetPercent in 1..10
    val progressColor =
        when {
            isDanger -> colors.danger
            isWarning -> colors.warning
            else -> colors.primary
        }
    val spentColor = if (state.isOverBudget) colors.danger else colors.onSurface

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .padding(dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.space12),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(dimens.quickAccessIconSize)
                            .clip(ProExpenseTheme.shapes.tile)
                            .background(colors.primaryTint),
                    contentAlignment = Alignment.Center,
                ) {
                    ProIcon(
                        glyph = ProIconGlyph.FeatEvents,
                        contentDescription = null,
                        tint = colors.primaryDeep,
                        size = dimens.iconInline + 4.dp,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space2)) {
                    Text(
                        text = state.title,
                        style = typography.bodySemiBold,
                        color = colors.onSurface,
                    )
                    Text(
                        text = state.dateRange,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
            if (isWarning || isDanger) {
                val chipColor = if (isDanger) colors.danger else colors.warning
                val chipText =
                    if (state.overAmountLabel != null) {
                        stringResource(R.string.event_over_budget_by, state.overAmountLabel, state.overBudgetPercent)
                    } else {
                        stringResource(R.string.event_over_budget)
                    }
                Text(
                    text = chipText,
                    style = if (isDanger) typography.bodySemiBold.copy(fontSize = typography.caption.fontSize) else typography.caption,
                    color = chipColor,
                    modifier =
                        Modifier
                            .border(BorderStroke(1.dp, chipColor), ProExpenseTheme.shapes.chip)
                            .padding(horizontal = dimens.space8, vertical = dimens.space4),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = state.spentLabel,
                style = typography.sectionHead,
                color = spentColor,
                textDecoration = if (state.isOverBudget) TextDecoration.LineThrough else null,
            )
            Column(horizontalAlignment = Alignment.End) {
                if (state.isOverBudget && state.overAmountLabel != null) {
                    Text(
                        text = state.overAmountLabel,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
                Text(
                    text = state.budgetLabel,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
                if (!state.dailyAverageLabel.isNullOrBlank()) {
                    Text(
                        text = state.dailyAverageLabel,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(ProExpenseTheme.shapes.chip)
                    .background(colors.paperAlt),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(state.progress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(ProExpenseTheme.shapes.chip)
                        .background(progressColor)
                        .align(Alignment.CenterStart),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun EventBudgetCardPreview() {
    ProExpenseTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EventBudgetCard(
                EventBudgetCardState(
                    title = "Bali Trip",
                    dateRange = "May 12 — May 26",
                    spentLabel = "$1,240",
                    budgetLabel = "of $2,000",
                    progress = 0.62f,
                ),
            )
            EventBudgetCard(
                EventBudgetCardState(
                    title = "Birthday party",
                    dateRange = "Apr 20 — Apr 20",
                    spentLabel = "$545",
                    budgetLabel = "of $500",
                    progress = 1f,
                    isOverBudget = true,
                    overAmountLabel = "over",
                ),
            )
        }
    }
}
