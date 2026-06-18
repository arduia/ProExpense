package com.arduia.expense.ui.design

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

// Buttons — DESIGN-SYSTEM.md §5 (Android Material rendition, android-frame.jsx).
// Every button scales to 0.97 on press.

enum class ProButtonTone { Primary, Secondary }

/**
 * Material filled (pill) button — the main action on a screen.
 * Full-width, 56dp tall, fully rounded (28dp).
 */
@Composable
fun ProFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: ProButtonTone = ProButtonTone.Primary,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val isPrimary = tone == ProButtonTone.Primary
    val background = if (isPrimary) colors.primary else colors.surfaceVariant
    val foreground = if (isPrimary) colors.onPrimary else colors.onSurface
    val shape = RoundedCornerShape(8.dp)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ProExpenseTheme.motion.pressedScale else 1f,
        animationSpec = ProExpenseTheme.motion.tapTween(),
        label = "filledButtonScale",
    )

    Row(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(56.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .then(if (isPrimary) Modifier.shadow(6.dp, shape, clip = false) else Modifier)
            .clip(shape)
            .background(background)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides foreground) {
            leading?.invoke()
            Text(text = text, style = ProExpenseTheme.typography.buttonLarge, color = foreground)
            trailing?.invoke()
        }
    }
}

/**
 * Material text button — tertiary / inline navigation (Skip, Back, Next).
 */
@Composable
fun ProTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: ProButtonTone = ProButtonTone.Primary,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val foreground = if (tone == ProButtonTone.Primary) colors.primaryDeep else colors.onSurfaceVariant
    val shape = RoundedCornerShape(20.dp)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ProExpenseTheme.motion.pressedScale else 1f,
        animationSpec = ProExpenseTheme.motion.tapTween(),
        label = "textButtonScale",
    )

    Row(
        modifier = modifier
            .scale(scale)
            .height(40.dp)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                onClick = onClick,
            )
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides foreground) {
            leading?.invoke()
            Text(text = text, style = ProExpenseTheme.typography.buttonMedium, color = foreground)
            trailing?.invoke()
        }
    }
}
