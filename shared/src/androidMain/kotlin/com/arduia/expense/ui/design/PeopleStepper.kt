package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PeopleStepper(
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 2,
    max: Int = 20,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.chip

    Row(
        modifier = modifier
            .clip(shape)
            .border(1.dp, colors.lineStrong, shape)
            .background(colors.surface)
            .padding(dimens.space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        StepperButton(
            glyph = ProIconGlyph.Minus,
            enabled = count > min,
            onClick = onDecrement,
        )
        Text(
            text = count.toString(),
            style = typography.sectionHead,
            color = colors.onSurface,
            modifier = Modifier.padding(horizontal = dimens.space16),
        )
        StepperButton(
            glyph = ProIconGlyph.Plus,
            enabled = count < max,
            onClick = onIncrement,
        )
    }
}

@Composable
private fun StepperButton(
    glyph: ProIconGlyph,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val alpha = if (enabled) 1f else ProExpenseTheme.motion.disabledOpacity

    ProIcon(
        glyph = glyph,
        contentDescription = null,
        tint = colors.onSurface.copy(alpha = alpha),
        modifier = modifier
            .size(dimens.touchTargetMin)
            .clip(ProExpenseTheme.shapes.tile)
            .proIconClickable(onClick = onClick, enabled = enabled),
    )
}
