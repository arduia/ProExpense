package com.proexpense.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.proexpense.designsystem.theme.DarkGray

/**
 * Soft two-layer card shadow — far lighter than M3 defaults; surfaces stay
 * pure white with NO tonal tint. See tokens.md §3.
 */
fun Modifier.softShadow(elevation: Dp = 6.dp, shape: Shape): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    spotColor = DarkGray.copy(alpha = 0.18f),
    ambientColor = DarkGray.copy(alpha = 0.12f),
)

/**
 * Tap feedback used everywhere: scale to 0.97 on press (80ms), no ripple —
 * the standard Pro Expense touch dip.
 */
fun Modifier.pressScale(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(80),
        label = "pressScale",
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
}
