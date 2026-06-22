package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.proLineHeight

/**
 * Compact, wrap-content primary call-to-action with a fully rounded (pill) shape and the
 * primary hero glow — used for centered empty-state actions like "Log your first expense".
 * Unlike [ProButton], it never stretches full-width and its corners are always fully round.
 */
@Composable
fun ProPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.chip
    val heroShadow = ProExpenseTheme.elevation.fab.firstOrNull()
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = typography.button.merge(
        TextStyle(
            fontSize = dimens.buttonLg.fontSizeSp.sp,
            lineHeight = proLineHeight(dimens.buttonLg.fontSizeSp, 1.4f),
        ),
    )

    Row(
        modifier = modifier
            .proPressScale(interactionSource, enabled = enabled)
            .alpha(if (enabled) 1f else motion.disabledOpacity)
            .then(
                if (heroShadow != null) {
                    Modifier.shadow(
                        elevation = heroShadow.blur,
                        shape = shape,
                        spotColor = heroShadow.color,
                        ambientColor = heroShadow.color,
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(colors.primary)
            .proRippleClickable(
                onClick = onClick,
                interactionSource = interactionSource,
                enabled = enabled,
                role = Role.Button,
            )
            .defaultMinSize(minHeight = dimens.buttonLg.height)
            .padding(horizontal = dimens.space26, vertical = dimens.space12),
        horizontalArrangement = Arrangement.spacedBy(dimens.space8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Text(
            text = text,
            style = textStyle,
            color = colors.onPrimaryWarm,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun ProPillButtonPreview() {
    ProExpenseTheme {
        ProPillButton(
            text = "Log your first expense",
            onClick = {},
            modifier = Modifier.padding(ProExpenseTheme.dimensions.space24),
            leading = {
                ProIcon(
                    glyph = ProIconGlyph.Plus,
                    contentDescription = null,
                    tint = ProExpenseTheme.colors.onPrimaryWarm,
                    size = ProExpenseTheme.dimensions.iconInline,
                )
            },
        )
    }
}
