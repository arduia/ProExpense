package com.arduia.expense.ui.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun proBoundedRipple() = ripple(bounded = true)

@Composable
fun proIconRipple() = ripple(bounded = false)

@Composable
fun ProTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = ProExpenseTheme.typography.navAction,
    color: Color = ProExpenseTheme.colors.onSurface,
    enabled: Boolean = true,
) {
    val dimens = ProExpenseTheme.dimensions
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
            .padding(horizontal = dimens.space8, vertical = dimens.space6)
            .minimumInteractiveComponentSize()
            .proClickable(
                onClick = onClick,
                shape = ProExpenseTheme.shapes.searchField,
                enabled = enabled,
            ),
    )
}

@Composable
fun Modifier.proPressScale(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
): Modifier {
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed && enabled) motion.pressedScale else 1f
    return scale(scale)
}

@Composable
fun Modifier.proRippleClickable(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
): Modifier = clickable(
    interactionSource = interactionSource,
    indication = proBoundedRipple(),
    enabled = enabled,
    role = role,
    onClick = onClick,
)

@Composable
fun Modifier.proCircularRippleClickable(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
): Modifier = clickable(
    interactionSource = interactionSource,
    indication = proIconRipple(),
    enabled = enabled,
    role = role,
    onClick = onClick,
)

@Composable
fun Modifier.proClickable(
    onClick: () -> Unit,
    shape: Shape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
    scaleOnPress: Boolean = true,
): Modifier {
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (scaleOnPress && pressed && enabled) motion.pressedScale else 1f
    return this
        .scale(scale)
        .clip(shape)
        .proRippleClickable(
            onClick = onClick,
            interactionSource = interactionSource,
            enabled = enabled,
            role = role,
        )
}

@Composable
fun Modifier.proIconClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
): Modifier {
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed && enabled) motion.pressedScale else 1f
    return this
        .minimumInteractiveComponentSize()
        .scale(scale)
        .clip(CircleShape)
        .proCircularRippleClickable(
            onClick = onClick,
            interactionSource = interactionSource,
            enabled = enabled,
            role = Role.Button,
        )
}

@Composable
fun Modifier.proSelectable(
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role = Role.Tab,
    scaleOnPress: Boolean = true,
    showRipple: Boolean = true,
): Modifier {
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (scaleOnPress && pressed && enabled) motion.pressedScale else 1f
    return this
        .minimumInteractiveComponentSize()
        .scale(scale)
        .clip(shape)
        .selectable(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            role = role,
            interactionSource = interactionSource,
            indication = if (showRipple) proBoundedRipple() else null,
        )
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun ProTextActionPreview() {
    ProExpenseTheme {
        ProTextAction(
            text = "See all",
            onClick = {},
            color = ProExpenseTheme.colors.primary,
        )
    }
}
