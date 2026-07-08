package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.proLineHeight

enum class ProButtonVariant {
    Primary,
    PrimaryDeep,
    Success,
    Danger,
    DangerOutline,
    Warning,
    Dark,
    Secondary,
    Ghost,
}

enum class ProButtonSize {
    Sm,
    Md,
    Lg,
}

@Composable
fun ProButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ProButtonVariant = ProButtonVariant.Primary,
    size: ProButtonSize = ProButtonSize.Md,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
    // Shows a spinner in place of the label and blocks clicks — for actions with a suspend call
    // (PIN setup hashing, saves) where an unchanged button otherwise reads as a frozen screen.
    loading: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val typography = ProExpenseTheme.typography
    val isEnabled = enabled && !loading
    val buttonSize = when (size) {
        ProButtonSize.Sm -> dimens.buttonSm
        ProButtonSize.Md -> dimens.buttonMd
        ProButtonSize.Lg -> dimens.buttonLg
    }
    val shape = when (size) {
        ProButtonSize.Sm -> ProExpenseTheme.shapes.buttonSm
        ProButtonSize.Md -> ProExpenseTheme.shapes.buttonMd
        ProButtonSize.Lg -> ProExpenseTheme.shapes.buttonLg
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed && isEnabled) motion.pressedScale else 1f
    val pressedAlpha = if (pressed && isEnabled) motion.pressedOpacity else 1f
    val textStyle = typography.button.merge(
        TextStyle(
            fontSize = buttonSize.fontSizeSp.sp,
            lineHeight = proLineHeight(buttonSize.fontSizeSp, 1.4f),
        ),
    )
    val contentPadding = PaddingValues(
        horizontal = buttonSize.horizontalPadding,
        vertical = buttonSize.verticalPadding,
    )
    val scaledModifier = modifier
        .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
        .then(
            if (variant == ProButtonVariant.Primary && fillMaxWidth) {
                val shadowColor = colors.primary.copy(alpha = 0.25f)
                Modifier.shadow(
                    elevation = 6.dp,
                    shape = shape,
                    spotColor = shadowColor,
                    ambientColor = shadowColor,
                )
            } else {
                Modifier
            },
        )
        .scale(scale)
        .alpha(pressedAlpha)
        .defaultMinSize(minHeight = buttonSize.height)

    when (variant) {
        ProButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.primary),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimaryWarm,
                    disabledContainerColor = colors.primary.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.onPrimaryWarm.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(
                    text = text,
                    textStyle = textStyle,
                    leading = leading,
                    trailing = trailing,
                    loading = loading,
                )
            }
        }
        ProButtonVariant.PrimaryDeep -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.primaryDeep),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryDeep,
                    contentColor = colors.onPrimaryWarm,
                    disabledContainerColor = colors.primaryDeep.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.onPrimaryWarm.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Success -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.success),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.success,
                    contentColor = colors.onPrimaryWarm,
                    disabledContainerColor = colors.success.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.onPrimaryWarm.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Danger -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.danger),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.danger,
                    contentColor = colors.onPrimaryWarm,
                    disabledContainerColor = colors.danger.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.onPrimaryWarm.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.DangerOutline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.danger),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.danger,
                    disabledContentColor = colors.danger.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Warning -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.warning),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.warning,
                    contentColor = colors.onPrimaryWarm,
                    disabledContainerColor = colors.warning.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.onPrimaryWarm.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Dark -> {
            Button(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.onSurface),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.onSurface,
                    contentColor = colors.paper,
                    disabledContainerColor = colors.onSurface.copy(alpha = motion.disabledOpacity),
                    disabledContentColor = colors.paper.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                border = BorderStroke(dimens.buttonBorderWidth, colors.lineStrong),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onSurface,
                    disabledContentColor = colors.onSurface.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
        ProButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = scaledModifier,
                enabled = isEnabled,
                shape = shape,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onSurface,
                    disabledContentColor = colors.onSurface.copy(alpha = motion.disabledOpacity),
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
            ) {
                ProButtonLabel(text = text, textStyle = textStyle, leading = leading, trailing = trailing, loading = loading)
            }
        }
    }
}

@Composable
private fun ProButtonLabel(
    text: String,
    textStyle: TextStyle,
    leading: (@Composable () -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    loading: Boolean = false,
) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(textStyle.fontSize.value.dp),
            color = LocalContentColor.current,
            strokeWidth = 2.dp,
        )
        return
    }

    // Height is a min-height, not fixed — an unconstrained Text wraps on narrow screens/long
    // labels (e.g. "Mark as settled" + leading icon) and silently grows the button to 2 lines.
    if (leading == null && trailing == null) {
        Text(text = text, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        return
    }

    val dimens = ProExpenseTheme.dimensions
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.space8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Text(text = text, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        trailing?.invoke()
    }
}
