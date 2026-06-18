package com.arduia.expense.ui.design

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.arduia.expense.ui.theme.ProExpenseTheme

// Buttons — design_handoff_v2 / components.yaml → ProButton

enum class ProButtonVariant {
    Primary,
    PrimaryDeep,
    Success,
    Dark,
    Secondary,
    Ghost,
}

enum class ProButtonSize { Sm, Md, Lg }

/** @deprecated Use [ProButtonVariant] */
typealias ProButtonTone = ProButtonVariant

@Composable
fun ProButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ProButtonVariant = ProButtonVariant.Primary,
    size: ProButtonSize = ProButtonSize.Lg,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val typography = ProExpenseTheme.typography

    val (background, foreground, borderColor) = buttonColors(variant, colors)
    val (height, horizontalPadding, textStyle, shape) = buttonMetrics(size, dims, shapes, typography)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ProExpenseTheme.motion.pressedScale else 1f,
        animationSpec = ProExpenseTheme.motion.tapTween(),
        label = "buttonScale",
    )

    Row(
        modifier = modifier
            .scale(scale)
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height)
            .alpha(if (enabled) 1f else ProExpenseTheme.motion.disabledOpacity)
            .clip(shape)
            .background(background)
            .border(dims.buttonBorderWidth, borderColor, shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(dims.buttonContentGap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides foreground) {
            leading?.invoke()
            Text(text = text, style = textStyle, color = foreground)
            trailing?.invoke()
        }
    }
}

@Composable
fun ProFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: ProButtonVariant = ProButtonVariant.Primary,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ProButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = tone,
        size = ProButtonSize.Lg,
        enabled = enabled,
        fillMaxWidth = true,
        leading = leading,
        trailing = trailing,
    )
}

@Composable
fun ProTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: ProButtonVariant = ProButtonVariant.Ghost,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ProButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = if (tone == ProButtonVariant.Secondary) ProButtonVariant.Ghost else tone,
        size = ProButtonSize.Md,
        leading = leading,
        trailing = trailing,
    )
}

private data class ButtonMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val textStyle: androidx.compose.ui.text.TextStyle,
    val shape: androidx.compose.foundation.shape.RoundedCornerShape,
)

@Composable
private fun buttonMetrics(
    size: ProButtonSize,
    dims: com.arduia.expense.ui.theme.ProExpenseDimensions,
    shapes: com.arduia.expense.ui.theme.ProExpenseShapes,
    typography: com.arduia.expense.ui.theme.ProExpenseTypography,
): ButtonMetrics = when (size) {
    ProButtonSize.Sm -> ButtonMetrics(
        dims.buttonSmallHeight,
        dims.buttonPaddingHSm,
        typography.buttonSmall,
        shapes.buttonSm,
    )
    ProButtonSize.Md -> ButtonMetrics(
        dims.buttonOutlinedHeight,
        dims.space18,
        typography.buttonMedium,
        shapes.buttonMd,
    )
    ProButtonSize.Lg -> ButtonMetrics(
        dims.buttonFilledHeight,
        dims.buttonPaddingHLg,
        typography.buttonLarge,
        shapes.buttonLg,
    )
}

@Composable
private fun buttonColors(
    variant: ProButtonVariant,
    colors: com.arduia.expense.ui.theme.ProExpenseColors,
): Triple<Color, Color, Color> = when (variant) {
    ProButtonVariant.Primary -> Triple(colors.primary, colors.paperWarm, colors.primary)
    ProButtonVariant.PrimaryDeep -> Triple(colors.primaryDeep, colors.paperWarm, colors.primaryDeep)
    ProButtonVariant.Success -> Triple(colors.success, colors.paperWarm, colors.success)
    ProButtonVariant.Dark -> Triple(colors.ink, colors.paperWarm, colors.ink)
    ProButtonVariant.Secondary -> Triple(Color.Transparent, colors.ink, colors.lineStrong)
    ProButtonVariant.Ghost -> Triple(Color.Transparent, colors.ink2, Color.Transparent)
}
