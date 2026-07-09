package com.arduia.expense.ui.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Centered confirmation dialog with a tinted icon badge, title, body and a
 * dismiss / confirm button pair. Always mounted so the scrim and card animate
 * out — toggle with [visible] rather than wrapping in `if`.
 */
@Composable
fun ProAlertDialog(
    visible: Boolean,
    icon: ProIconGlyph,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    body: AnnotatedString,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmVariant: ProButtonVariant = ProButtonVariant.Primary,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val dialogElevation = ProExpenseTheme.elevation.sheet.firstOrNull()

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(motion.fadeDurationMillis, easing = motion.standardEasing)),
            exit = fadeOut(tween(motion.fadeDurationMillis, easing = motion.standardEasing)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(colors.scrim)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onDismiss,
                        ),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimens.space24),
            enter =
                fadeIn(tween(motion.fadeDurationMillis, easing = motion.standardEasing)) +
                    scaleIn(tween(motion.fadeDurationMillis, easing = motion.standardEasing), initialScale = 0.92f),
            exit =
                fadeOut(tween(motion.fadeDurationMillis, easing = motion.standardEasing)) +
                    scaleOut(tween(motion.fadeDurationMillis, easing = motion.standardEasing), targetScale = 0.92f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(
                                if (dialogElevation != null) {
                                    Modifier.shadow(
                                        elevation = dialogElevation.blur,
                                        shape = ProExpenseTheme.shapes.card,
                                        spotColor = dialogElevation.color,
                                        ambientColor = dialogElevation.color,
                                    )
                                } else {
                                    Modifier
                                },
                            )
                            // Swallow taps on the card so they don't reach the scrim.
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {},
                            ),
                    shape = ProExpenseTheme.shapes.card,
                    color = colors.surface,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(dimens.space24),
                        verticalArrangement = Arrangement.spacedBy(dimens.space16),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(dimens.touchTargetMin)
                                    .clip(CircleShape)
                                    .background(iconBackground),
                            contentAlignment = Alignment.Center,
                        ) {
                            ProIcon(
                                glyph = icon,
                                contentDescription = null,
                                tint = iconTint,
                                size = dimens.iconNav,
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                            Text(
                                text = title,
                                style = typography.sheetTitle,
                                color = colors.onSurface,
                            )
                            Text(
                                text = body,
                                style = typography.body,
                                color = colors.onSurfaceMuted,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
                        ) {
                            ProButton(
                                text = dismissLabel,
                                onClick = onDismiss,
                                variant = ProButtonVariant.Secondary,
                                size = ProButtonSize.Lg,
                                fillMaxWidth = true,
                                modifier = Modifier.weight(1f),
                            )
                            ProButton(
                                text = confirmLabel,
                                onClick = onConfirm,
                                variant = confirmVariant,
                                size = ProButtonSize.Lg,
                                fillMaxWidth = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "ProAlertDialog — warning",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProAlertDialogWarningPreview() {
    ProExpenseTheme {
        ProAlertDialog(
            visible = true,
            icon = ProIconGlyph.User,
            iconTint = ProExpenseTheme.colors.warning,
            iconBackground = ProExpenseTheme.colors.warningTint,
            title = "John already has a record",
            body = AnnotatedString("John is on your \"I Owe\" list. Continue?"),
            confirmLabel = "Continue",
            onConfirm = {},
            dismissLabel = "Cancel",
            onDismiss = {},
            confirmVariant = ProButtonVariant.Warning,
        )
    }
}

@Preview(
    name = "ProAlertDialog — destructive",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProAlertDialogDestructivePreview() {
    ProExpenseTheme {
        ProAlertDialog(
            visible = true,
            icon = ProIconGlyph.Close,
            iconTint = ProExpenseTheme.colors.danger,
            iconBackground = ProExpenseTheme.colors.dangerTint,
            title = "Delete this record?",
            body = AnnotatedString("Removing Aiko's settled record can't be undone."),
            confirmLabel = "Delete",
            onConfirm = {},
            dismissLabel = "Cancel",
            onDismiss = {},
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}
