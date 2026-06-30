package com.arduia.expense.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun QuickAccessPickerSheetContent(
    selected: Set<QuickAccessTileType>,
    onToggle: (QuickAccessTileType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Text(
            text = stringResource(R.string.quick_access_customize_hint),
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
        QuickAccessTileType.entries.forEach { tile ->
            QuickAccessPickerRow(
                tile = tile,
                checked = tile in selected,
                onClick = { onToggle(tile) },
            )
        }
    }
}

@Composable
private fun QuickAccessPickerRow(
    tile: QuickAccessTileType,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val (icon, labelRes) = remember(tile) { tileIconAndLabel(tile) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.iconBadge)
                .clip(CircleShape)
                .background(colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = icon,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                size = dimens.iconInline,
            )
        }
        Text(
            text = stringResource(labelRes),
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (checked) {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = null,
                tint = colors.primary,
                size = dimens.iconInline,
            )
        }
    }
}

private fun tileIconAndLabel(tile: QuickAccessTileType): Pair<ProIconGlyph, Int> = when (tile) {
    QuickAccessTileType.Reports -> ProIconGlyph.FeatReports to R.string.quick_access_reports
    QuickAccessTileType.Debt -> ProIconGlyph.FeatDebt to R.string.quick_access_debt
    QuickAccessTileType.Split -> ProIconGlyph.FeatSplit to R.string.quick_access_split
    QuickAccessTileType.Events -> ProIconGlyph.FeatEvents to R.string.quick_access_events
}

@Preview(
    name = "Quick access — customize sheet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun QuickAccessPickerSheetPreview() {
    ProExpenseTheme {
        ProBottomSheetHost(
            visible = true,
            title = stringResource(R.string.quick_access_customize_title),
            onClose = {},
        ) {
            QuickAccessPickerSheetContent(
                selected = setOf(QuickAccessTileType.Reports, QuickAccessTileType.Split, QuickAccessTileType.Events),
                onToggle = {},
            )
        }
    }
}
