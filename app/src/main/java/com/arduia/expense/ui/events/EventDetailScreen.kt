package com.arduia.expense.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.EventBudgetSummaryCard
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.preview.EventDetailUiState
import com.arduia.expense.ui.preview.EventLinkedExpenseUi
import com.arduia.expense.ui.preview.previewEventDetail
import com.arduia.expense.ui.preview.previewEventDetailClosed
import com.arduia.expense.ui.preview.previewEventDetailWarn
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun EventDetailScreen(
    state: EventDetailUiState,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onAddTagged: () -> Unit,
    onExpenseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.space12)) {
            ProTopBar(
                title = "",
                onBack = onBack,
                backLabel = stringResource(R.string.events_back),
                action = ProTopBarAction.More,
                onAction = onMore,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space20),
        ) {
            EventDetailHeader(state)

            EventBudgetSummaryCard(state = state.summary)

            if (state.readOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space12),
                    verticalAlignment = Alignment.Top,
                ) {
                    ProIcon(
                        glyph = ProIconGlyph.Eye,
                        contentDescription = null,
                        tint = colors.muted,
                        size = dimens.iconInline,
                    )
                    Text(
                        text = stringResource(R.string.event_readonly_notice),
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                }
            }

            if (state.linkedExpenses.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space10)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.event_linked_expenses_count, state.linkedCount),
                            style = typography.eyebrow,
                            color = colors.onSurfaceVariant,
                        )
                        if (state.showAddTagged) {
                            ProTextAction(
                                text = stringResource(R.string.event_add_tagged),
                                onClick = onAddTagged,
                                style = typography.bodyMedium,
                                color = colors.primary,
                                leading = {
                                    ProIcon(
                                        glyph = ProIconGlyph.Plus,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        size = dimens.iconInline,
                                    )
                                },
                            )
                        }
                    }
                    EventLinkedExpenseCard(
                        expenses = state.linkedExpenses,
                        onExpenseClick = onExpenseClick,
                    )
                }
            }
        }

        if (state.readOnly) {
            ProButton(
                text = stringResource(R.string.event_add_expense),
                onClick = {},
                variant = ProButtonVariant.Secondary,
                size = ProButtonSize.Lg,
                enabled = false,
                fillMaxWidth = true,
                modifier = Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
            )
        }
    }
}

@Composable
private fun EventDetailHeader(state: EventDetailUiState) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        if (state.statusEyebrow != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space6),
            ) {
                Box(
                    modifier = Modifier
                        .size(dimens.space8)
                        .clip(CircleShape)
                        .background(colors.success),
                )
                Text(
                    text = state.statusEyebrow,
                    style = typography.eyebrow,
                    color = colors.success,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space10),
        ) {
            Text(
                text = state.title,
                style = typography.profileScreenTitle,
                color = colors.onSurface,
            )
            if (state.statusInlineChip != null) {
                Text(
                    text = state.statusInlineChip,
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, colors.lineStrong), ProExpenseTheme.shapes.chip)
                        .padding(horizontal = dimens.space8, vertical = dimens.space4),
                )
            }
        }
        Text(
            text = state.subtitle,
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
private fun EventLinkedExpenseCard(
    expenses: List<EventLinkedExpenseUi>,
    onExpenseClick: (String) -> Unit,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface),
    ) {
        expenses.forEachIndexed { index, expense ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.lineSoft),
                )
            }
            EventLinkedExpenseRow(
                expense = expense,
                onClick = { onExpenseClick(expense.id) },
            )
        }
    }
}

@Composable
private fun EventLinkedExpenseRow(
    expense: EventLinkedExpenseUi,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space16, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        LogCategoryBadge(categoryId = expense.categoryId, size = dimens.iconBadge)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = expense.categoryLabel,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        Text(
            text = expense.amountLabel,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Preview(
    name = "Event detail — on track",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventDetailPreview() {
    ProExpenseTheme {
        EventDetailScreen(
            state = previewEventDetail,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }
}

@Preview(
    name = "Event detail — over budget",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventDetailWarnPreview() {
    ProExpenseTheme {
        EventDetailScreen(
            state = previewEventDetailWarn,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }
}

@Preview(
    name = "Event detail — closed read-only",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventDetailClosedPreview() {
    ProExpenseTheme {
        EventDetailScreen(
            state = previewEventDetailClosed,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }
}
