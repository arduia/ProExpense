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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.sheetEnter
import com.arduia.expense.ui.theme.sheetExit

/**
 * `fraction` must apply to the room actually left above the keyboard, not to the full screen.
 * Subtracting `imeHeight` from a flat `maxHeight * fraction` cap (the previous approach) shrinks
 * the sheet by the keyboard's entire height on top of its designed margin already excluding that
 * space, so once a keyboard opens the sheet visibly collapses to roughly half the screen instead
 * of filling the space it still has. Computing the fraction against `(maxHeight - imeHeight)`
 * keeps the sheet filling its intended proportion of whatever room remains, while still never
 * overflowing above the screen — `fraction <= 1` guarantees `contentHeight + imeHeight <= maxHeight`.
 */
internal fun sheetHeightCap(
    maxHeight: Dp,
    imeHeight: Dp,
    fraction: Float,
): Dp {
    val availableHeight = (maxHeight - imeHeight).coerceAtLeast(0.dp)
    return availableHeight * fraction
}

@Composable
fun ProBottomSheet(
    title: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    fullHeight: Boolean = false,
    // Real available height of the window this sheet is hosted in. Defaults to the design-artboard
    // constant only for standalone/preview usage (ProDesignSystemSheetContent) — ProBottomSheetHost
    // always overrides this with a BoxWithConstraints-measured value, since the artboard constant
    // is a fixed reference size (Pixel 9 Pro) that doesn't match real device screens: on a shorter
    // device the fraction-of-artboard cap can exceed what's actually on screen, and combined with
    // an open keyboard's imePadding() shift, the sheet's top edge (and its header/content) can end
    // up pushed above the visible screen instead of shrinking to fit.
    maxHeight: Dp = ProExpenseTheme.dimensions.artboardHeight,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val sheetElevation = ProExpenseTheme.elevation.sheet.firstOrNull()
    val imeHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val sheetHeightCap =
        sheetHeightCap(
            maxHeight = maxHeight,
            imeHeight = imeHeight,
            fraction = if (fullHeight) dimens.sheetFullHeightFraction else dimens.sheetMaxHeightFraction,
        )

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
                            Modifier.height(sheetHeightCap)
                        } else {
                            Modifier.heightIn(max = sheetHeightCap)
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
                        // weight(fill = fullHeight) always gives content a real height budget
                        // bounded by this Column's height/heightIn above, instead of an unbounded
                        // one. fullHeight sheets force-fill that budget (unchanged from before).
                        // Non-fullHeight sheets still wrap compact content naturally, but content
                        // that opts into its own internal weight()+verticalScroll() — a sticky
                        // header/footer around a scrollable middle, e.g. Category's icon grid —
                        // can now actually respect the sheet's max-height cap instead of silently
                        // overflowing past it (a Box/Column doesn't clip by default, so an
                        // unbounded budget let tall content render off-screen under the keyboard).
                        .weight(1f, fill = fullHeight)
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

    // Measure the real window height this host occupies rather than assuming the fixed design
    // artboard size — ProBottomSheet uses this to cap sheet height as a fraction of what's
    // actually on screen, so it fits real devices (which vary far more than the artboard) instead
    // of being sized off a Pixel 9 Pro reference constant.
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val containerHeight = maxHeight
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
                maxHeight = containerHeight,
            ) {
                sheetContent()
            }
        }
    }
}
