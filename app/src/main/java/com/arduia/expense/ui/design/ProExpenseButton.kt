package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class ProExpenseButtonVariant {
    Primary,
    PrimaryDeep,
    Sage,
    Dark,
    Secondary,
    Ghost,
}

enum class ProExpenseButtonSize {
    Small,
    Medium,
    Large,
}

@Composable
fun ProExpenseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ProExpenseButtonVariant = ProExpenseButtonVariant.Primary,
    size: ProExpenseButtonSize = ProExpenseButtonSize.Medium,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = ProExpenseTheme.colors
    val shapes = ProExpenseTheme.shapes
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed && enabled) motion.pressedScale else 1f

    val (background, content, border) = buttonColors(variant, colors, enabled)
    val (padding, textStyle, shape) = buttonMetrics(size, shapes)

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = 40.dp),
        enabled = enabled,
        shape = shape,
        color = background,
        contentColor = content,
        border = border,
        interactionSource = interactionSource,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(padding),
            style = textStyle,
            color = content,
        )
    }
}

@Composable
private fun buttonColors(
    variant: ProExpenseButtonVariant,
    colors: com.arduia.expense.ui.theme.ProExpenseColors,
    enabled: Boolean,
): Triple<Color, Color, BorderStroke?> {
    val alpha = if (enabled) 1f else 0.4f
    return when (variant) {
        ProExpenseButtonVariant.Primary -> Triple(
            colors.primary.copy(alpha = alpha),
            colors.paperWarm.copy(alpha = alpha),
            BorderStroke(1.4.dp, colors.primary.copy(alpha = alpha)),
        )
        ProExpenseButtonVariant.PrimaryDeep -> Triple(
            colors.primaryDeep.copy(alpha = alpha),
            colors.paperWarm.copy(alpha = alpha),
            BorderStroke(1.4.dp, colors.primaryDeep.copy(alpha = alpha)),
        )
        ProExpenseButtonVariant.Sage -> Triple(
            colors.success.copy(alpha = alpha),
            colors.paperWarm.copy(alpha = alpha),
            BorderStroke(1.4.dp, colors.success.copy(alpha = alpha)),
        )
        ProExpenseButtonVariant.Dark -> Triple(
            colors.ink.copy(alpha = alpha),
            colors.paperWarm.copy(alpha = alpha),
            BorderStroke(1.4.dp, colors.ink.copy(alpha = alpha)),
        )
        ProExpenseButtonVariant.Secondary -> Triple(
            Color.Transparent,
            colors.ink.copy(alpha = alpha),
            BorderStroke(1.4.dp, colors.lineStrong.copy(alpha = alpha)),
        )
        ProExpenseButtonVariant.Ghost -> Triple(
            Color.Transparent,
            colors.ink.copy(alpha = alpha),
            BorderStroke(1.4.dp, Color.Transparent),
        )
    }
}

@Composable
private fun buttonMetrics(
    size: ProExpenseButtonSize,
    shapes: com.arduia.expense.ui.theme.ProExpenseShapes,
): Triple<PaddingValues, TextStyle, androidx.compose.foundation.shape.RoundedCornerShape> {
    val typography = ProExpenseTheme.typography
    return when (size) {
        ProExpenseButtonSize.Small -> Triple(
            PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            typography.buttonSmall,
            shapes.buttonSmall,
        )
        ProExpenseButtonSize.Medium -> Triple(
            PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            typography.buttonMedium,
            shapes.buttonMedium,
        )
        ProExpenseButtonSize.Large -> Triple(
            PaddingValues(horizontal = 22.dp, vertical = 16.dp),
            typography.buttonLarge,
            shapes.buttonLarge,
        )
    }
}
