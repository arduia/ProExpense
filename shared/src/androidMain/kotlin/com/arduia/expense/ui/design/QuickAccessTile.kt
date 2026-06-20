package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun QuickAccessTile(
    label: String,
    icon: ProIconGlyph,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val tileShape = ProExpenseTheme.shapes.tile

    Column(
        modifier = modifier
            .proPressScale(interactionSource)
            .clip(tileShape)
            .border(BorderStroke(1.dp, colors.line), tileShape)
            .background(colors.surface)
            .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
            .padding(dimens.space12),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        ProIcon(
            glyph = icon,
            contentDescription = null,
            tint = colors.primary,
            size = dimens.iconNav,
        )
        Text(
            text = label,
            style = typography.caption,
            color = colors.onSurfaceVariant,
        )
    }
}
