package com.arduia.expense.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class PinKeypadState {
    Default,
    Locked,
    Error,
}

private const val PIN_LENGTH = 6

private val pinRows = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(null, "0", "backspace"),
)

@Composable
fun PinDots(
    filledCount: Int,
    modifier: Modifier = Modifier,
    state: PinKeypadState = PinKeypadState.Default,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val shakeOffset = remember { Animatable(0f) }
    val dotColor = when (state) {
        PinKeypadState.Error -> colors.danger
        else -> colors.primary
    }

    LaunchedEffect(state) {
        if (state == PinKeypadState.Error) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = motion.shakeDurationMillis
                    0f at 0
                    6f at (motion.shakeDurationMillis * 0.25f).toInt()
                    (-6f) at (motion.shakeDurationMillis * 0.5f).toInt()
                    4f at (motion.shakeDurationMillis * 0.75f).toInt()
                    0f at motion.shakeDurationMillis
                },
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp)
            .padding(vertical = dimens.space8),
        horizontalArrangement = Arrangement.spacedBy(dimens.space12, Alignment.CenterHorizontally),
    ) {
        repeat(PIN_LENGTH) { index ->
            val filled = index < filledCount.coerceIn(0, PIN_LENGTH)
            Box(
                modifier = Modifier
                    .size(dimens.space12)
                    .clip(CircleShape)
                    .then(
                        if (filled) {
                            Modifier.background(dotColor)
                        } else {
                            Modifier.border(BorderStroke(1.5.dp, colors.lineStrong), CircleShape)
                        },
                    ),
            )
        }
    }
}

@Composable
fun PinKeypad(
    filledDots: Int,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    state: PinKeypadState = PinKeypadState.Default,
    lockoutMessage: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val keypadAlpha = if (state == PinKeypadState.Locked) motion.keypadDisabledOpacity else 1f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        PinDots(filledCount = filledDots, state = state)
        if (lockoutMessage != null && state == PinKeypadState.Locked) {
            Text(
                text = lockoutMessage,
                style = ProExpenseTheme.typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(keypadAlpha),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            pinRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    row.forEach { key ->
                        if (key == null) {
                            Box(modifier = Modifier.weight(1f))
                        } else {
                            PinKey(
                                label = key,
                                enabled = state != PinKeypadState.Locked,
                                onClick = {
                                    if (key == "backspace") onBackspace() else onDigit(key.toInt())
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed && enabled) motion.pressedScale else 1f

    Box(
        modifier = modifier
            .aspectRatio(1.45f)
            .scale(scale)
            .clip(ProExpenseTheme.shapes.keypadKey)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.keypadKey)
            .background(colors.surface)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = dimens.touchTargetMin),
        contentAlignment = Alignment.Center,
    ) {
        if (label == "backspace") {
            ProIcon(
                glyph = ProIconGlyph.Back,
                contentDescription = "Backspace",
                tint = colors.onSurface,
                size = dimens.iconNav,
            )
        } else {
            Text(
                text = label,
                style = typography.keypadKey,
                color = colors.onSurface,
            )
        }
    }
}
