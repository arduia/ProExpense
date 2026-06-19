package com.arduia.expense.ui.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun proBoundedRipple() = ripple(bounded = true)

@Composable
fun proIconRipple() = ripple(bounded = false)

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
            indication = proBoundedRipple(),
        )
}
