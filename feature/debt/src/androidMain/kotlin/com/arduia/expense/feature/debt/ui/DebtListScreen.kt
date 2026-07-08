package com.arduia.expense.feature.debt.ui

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.debt.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtLoading
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe
import com.arduia.expense.feature.debt.ui.preview.previewDebtSettled
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

@Composable
fun DebtListScreen(
    state: DebtListUiState,
    onSideSelected: (DebtSide) -> Unit,
    onAddRecord: () -> Unit,
    onRecordClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.debt_title),
                onBack = onBack,
                backLabel = stringResource(R.string.debt_back_more),
            )
            // A small labeled pill, matching Budget's "+ New event" — the plain icon-only
            // ProTopBarAction.Add tile read as oversized and unlabeled next to it.
            ProButton(
                text = stringResource(R.string.debt_new_record),
                onClick = onAddRecord,
                size = ProButtonSize.Sm,
                modifier = Modifier.align(Alignment.CenterEnd),
                leading = {
                    ProIcon(
                        glyph = ProIconGlyph.Plus,
                        contentDescription = null,
                        tint = colors.onPrimaryWarm,
                        size = dimens.iconInline,
                    )
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            SegmentedToggle(
                options = listOf(
                    stringResource(R.string.debt_tab_lent),
                    stringResource(R.string.debt_tab_owe),
                ),
                selectedIndex = if (state.side == DebtSide.Lent) 0 else 1,
                onSelected = { index ->
                    onSideSelected(if (index == 0) DebtSide.Lent else DebtSide.Owe)
                },
                usePrimarySelection = true,
            )

            // Debts load asynchronously after first composition — without this check, the summary
            // card would briefly show "$0 · 0 active" before real data has had a chance to load.
            if (!state.isLoading) {
                DebtSummaryCard(state = state)

                if (state.active.isNotEmpty()) {
                    DebtSection(label = stringResource(R.string.debt_section_active)) {
                        DebtRecordGroup(
                            records = state.active,
                            side = state.side,
                            onRecordClick = onRecordClick,
                        )
                    }
                }

                if (state.settled.isNotEmpty()) {
                    DebtSection(label = stringResource(R.string.debt_section_settled)) {
                        DebtRecordGroup(
                            records = state.settled,
                            side = state.side,
                            onRecordClick = onRecordClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtSummaryCard(
    state: DebtListUiState,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val accent = if (state.side == DebtSide.Lent) colors.success else colors.danger
    val eyebrow = if (state.side == DebtSide.Lent) {
        stringResource(R.string.debt_youre_owed)
    } else {
        stringResource(R.string.debt_you_owe)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .padding(dimens.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            Text(text = eyebrow, style = typography.eyebrow, color = colors.onSurfaceMuted)
            Text(
                text = state.netLabel,
                style = typography.summaryAmount,
                color = accent,
            )
        }
        Text(
            text = androidx.compose.ui.res.pluralStringResource(
                R.plurals.debt_active_count,
                state.activeCount,
                state.activeCount,
            ),
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
private fun DebtSection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space10),
    ) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        content()
    }
}

@Composable
private fun DebtRecordGroup(
    records: List<DebtRecordUi>,
    side: DebtSide,
    onRecordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface),
    ) {
        records.forEachIndexed { index, record ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.lineSoft),
                )
            }
            DebtRecordRow(
                record = record,
                side = side,
                onClick = { onRecordClick(record.id) },
            )
        }
    }
}

@Composable
private fun DebtRecordRow(
    record: DebtRecordUi,
    side: DebtSide,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val accent = if (side == DebtSide.Lent) colors.success else colors.danger
    val rowAlpha = if (record.settled) 0.5f else 1f
    val meta = if (record.subtitle != null) {
        "${record.dateLabel} · ${record.subtitle}"
    } else {
        record.dateLabel
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(rowAlpha)
            .proClickable(onClick = onClick, shape = androidx.compose.ui.graphics.RectangleShape)
            .padding(horizontal = dimens.space16, vertical = dimens.rowPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        DebtAvatar(
            initial = record.initial,
            tint = if (record.settled) colors.onSurfaceMuted else accent,
            background = when {
                record.settled -> colors.paperAlt
                side == DebtSide.Lent -> colors.successTint
                else -> colors.dangerTint
            },
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.name,
                style = typography.bodySemiBold,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (record.settled) {
                    stringResource(R.string.debt_settled_meta, record.dateLabel)
                } else {
                    meta
                },
                style = typography.caption,
                color = colors.onSurfaceMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        if (record.settled) {
            Text(
                text = stringResource(R.string.debt_settled_chip),
                style = typography.eyebrow,
                color = colors.onSurfaceMuted,
                modifier = Modifier
                    .border(BorderStroke(1.dp, colors.lineStrong), ProExpenseTheme.shapes.chip)
                    .padding(horizontal = dimens.space10, vertical = dimens.space4),
            )
        } else {
            Text(
                text = record.amountLabel,
                style = typography.listAmount,
                color = accent,
            )
        }
    }
}

@Composable
internal fun DebtAvatar(
    initial: String,
    tint: Color,
    background: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = ProExpenseTheme.dimensions.iconBadge,
) {
    val typography = ProExpenseTheme.typography
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = typography.bodySemiBold.centeredGlyph(),
            color = tint,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    name = "Debt — I Lent",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtListLentPreview() {
    ProExpenseTheme {
        DebtListScreen(
            state = previewDebtLent,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Debt — loading",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtListLoadingPreview() {
    ProExpenseTheme {
        DebtListScreen(
            state = previewDebtLoading,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Debt — I Owe",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtListOwePreview() {
    ProExpenseTheme {
        DebtListScreen(
            state = previewDebtOwe,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Debt — settled only",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtListSettledPreview() {
    ProExpenseTheme {
        DebtListScreen(
            state = previewDebtSettled,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }
}
