package com.arduia.expense.ui.design

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val dimens = ProExpenseTheme.dimensions
    Row(
        // Clip + ripple sit outermost so the ripple covers the label + its padding. No 48dp
        // minimum-size box — that ballooned the ripple far past short labels like "Skip"/"Back".
        modifier =
            modifier
                .proClickable(
                    onClick = onClick,
                    shape = ProExpenseTheme.shapes.searchField,
                    enabled = enabled,
                ).padding(horizontal = dimens.space8, vertical = dimens.space6),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        leading?.invoke()
        Text(text = text, style = style, color = color)
        trailing?.invoke()
    }
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

// Discrete press feedback for rapid, repeated taps (keypad). A ripple is a continuous
// animation that lags behind fast sequential presses; an animated background fill snaps to
// each new press state instead, so it never queues or overlaps.
@Composable
fun Modifier.proNoRippleClickable(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
): Modifier =
    clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onClick = onClick,
    )

@Composable
fun Modifier.proPressBackground(
    interactionSource: InteractionSource,
    shape: Shape,
    enabled: Boolean = true,
    restingColor: Color = ProExpenseTheme.colors.surface,
    pressedColor: Color = ProExpenseTheme.colors.keypadKeyPressed,
): Modifier {
    val motion = ProExpenseTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val target = if (pressed && enabled) pressedColor else restingColor
    val color by animateColorAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = motion.tapDurationMillis, easing = motion.standardEasing),
        label = "proPressBackground",
    )
    return background(color, shape)
}

@Composable
fun Modifier.proRippleClickable(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
): Modifier =
    clickable(
        interactionSource = interactionSource,
        indication = proBoundedRipple(),
        enabled = enabled,
        role = role,
        onClick = onClick,
    )

/**
 * [proRippleClickable]'s selected-state counterpart — exposes `selected` to TalkBack via
 * `Modifier.selectable` instead of a plain click, for single-select groups like filter/category
 * chips. Deliberately has no size floor: [proSelectable] below adds
 * `minimumInteractiveComponentSize()`, which inflates compact chips past their siblings (see
 * retrospective 2026-07-02) — use this bare version for anything already sized by its content.
 */
@Composable
fun Modifier.proSelectableClickable(
    selected: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role = Role.Tab,
): Modifier =
    selectable(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        role = role,
        interactionSource = interactionSource,
        indication = proBoundedRipple(),
    )

/** [proClickable]'s selected-state counterpart — same press-scale + clip bundle, but selectable. */
@Composable
fun Modifier.proSelectableClip(
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
        .scale(scale)
        .clip(shape)
        .proSelectableClickable(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
            enabled = enabled,
            role = role,
        )
}

@Composable
fun Modifier.proCircularRippleClickable(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    role: Role? = null,
): Modifier =
    clickable(
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

/**
 * `minimumInteractiveComponentSize()` here puts a 48dp floor on the icon's *layout box*, not just
 * its touch target — nested inside a compact component (chip, pill, dense row) it inflates the
 * parent past its siblings (see retrospective 2026-07-02). For a secondary micro-target inside an
 * already-tappable surface, use `clip(CircleShape)` + [proCircularRippleClickable] instead.
 */
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
