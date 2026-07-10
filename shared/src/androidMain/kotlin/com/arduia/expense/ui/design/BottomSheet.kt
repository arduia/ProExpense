package com.arduia.expense.ui.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.sheetEnter
import com.arduia.expense.ui.theme.sheetExit

@Composable
fun ProBottomSheet(
    title: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    fullHeight: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val sheetElevation = ProExpenseTheme.elevation.sheet.firstOrNull()

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (sheetElevation != null) {
                        Modifier.shadow(
                            elevation = sheetElevation.blur,
                            shape = ProExpenseTheme.shapes.sheet,
                            spotColor = sheetElevation.color,
                            ambientColor = sheetElevation.color,
                        )
                    } else {
                        Modifier
                    },
                )
                // Sheet is bottom-aligned with no IME handling by default — without this, the
                // keyboard simply overlaps whatever input sits near the bottom of the sheet (e.g.
                // New Event's amount field) instead of the sheet shifting up above it.
                .imePadding(),
        shape = ProExpenseTheme.shapes.sheet,
        color = colors.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (fullHeight) {
                            Modifier.height(dimens.artboardHeight * dimens.sheetFullHeightFraction)
                        } else {
                            Modifier.heightIn(max = dimens.artboardHeight * dimens.sheetMaxHeightFraction)
                        },
                    ).padding(bottom = dimens.space24),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space10, bottom = dimens.space14),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(dimens.sheetHandleWidth)
                            .height(dimens.sheetHandleHeight)
                            .clip(RoundedCornerShape(999.dp))
                            .background(colors.onSurface.copy(alpha = 0.18f)),
                )
            }
            if (title != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.space20, vertical = dimens.space8),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = title,
                        style = typography.sectionHead,
                        color = colors.onSurface,
                    )
                    ProIcon(
                        glyph = ProIconGlyph.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = colors.onSurface,
                        modifier = Modifier.proIconClickable(onClick = onClose),
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .then(if (fullHeight) Modifier.weight(1f) else Modifier)
                        .padding(horizontal = dimens.space18, vertical = dimens.space8),
            ) {
                content()
            }
        }
    }
}

@Composable
fun ProBottomSheetHost(
    visible: Boolean,
    title: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    fullHeight: Boolean = false,
    sheetContent: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter =
                fadeIn(
                    animationSpec =
                        tween(
                            durationMillis = motion.fadeDurationMillis,
                            easing = motion.standardEasing,
                        ),
                ),
            exit =
                fadeOut(
                    animationSpec =
                        tween(
                            durationMillis = motion.fadeDurationMillis,
                            easing = motion.standardEasing,
                        ),
                ),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(colors.scrim)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onClose,
                        ),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            enter = motion.sheetEnter(reduceMotion),
            exit = motion.sheetExit(reduceMotion),
        ) {
            ProBottomSheet(
                title = title,
                onClose = onClose,
                fullHeight = fullHeight,
            ) {
                sheetContent()
            }
        }
    }
}
