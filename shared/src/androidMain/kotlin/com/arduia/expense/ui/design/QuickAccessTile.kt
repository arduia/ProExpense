package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun QuickAccessTile(
    label: String,
    icon: ProIconGlyph,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    iconBackground: Color? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val tileShape = ProExpenseTheme.shapes.tile
    val resolvedIconTint = iconTint ?: colors.primaryDeep
    val resolvedIconBackground = iconBackground ?: colors.primaryTint

    Column(
        modifier = modifier
            .proPressScale(interactionSource)
            .clip(tileShape)
            .border(BorderStroke(1.dp, colors.line), tileShape)
            .background(colors.surface)
            .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
            .padding(horizontal = dimens.space4, vertical = dimens.space12),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(resolvedIconBackground),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = icon,
                contentDescription = null,
                tint = resolvedIconTint,
                size = 18.dp,
            )
        }
        Text(
            text = label,
            style = typography.caption.copy(fontSize = 11.sp),
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
