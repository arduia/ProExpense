package com.arduia.expense.feature.currency.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.currency.ui.preview.MoreCurrencyItemUi
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CurrencyCard(
    item: MoreCurrencyItemUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val borderColor = if (selected) colors.primary else colors.line
    val background = if (selected) colors.primaryTint else colors.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.card)
            .background(background)
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.iconBadge)
                .clip(CircleShape)
                .background(if (selected) colors.surface else colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = item.symbol,
                style = typography.bodySemiBold,
                color = if (selected) colors.primary else colors.onSurfaceVariant,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.code, style = typography.bodySemiBold, color = colors.onSurface)
            Text(
                text = item.name,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        if (selected) {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = null,
                tint = colors.primary,
                size = dimens.iconNav,
            )
        }
    }
}
