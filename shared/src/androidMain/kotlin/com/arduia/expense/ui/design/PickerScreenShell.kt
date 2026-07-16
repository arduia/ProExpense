package com.arduia.expense.ui.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.forwardScreenEnter
import com.arduia.expense.ui.theme.forwardScreenExit
import com.arduia.expense.ui.theme.rememberProReduceMotion

/**
 * Full-screen counterpart to [ProBottomSheetHost] for pickers that need real screen space (a
 * month calendar) rather than a bottom-anchored sheet. Rendered as a sibling `Box` overlay per
 * this app's state-driven navigation pattern (see `PinLockFlow`/`PinEntryScreen`) — there is no
 * nav-controller backstack, so the tap-consuming `clickable` below stops touches from falling
 * through to whatever's rendered underneath while this is visible.
 */
@Composable
fun PickerScreenShell(
    visible: Boolean,
    title: String,
    onClose: () -> Unit,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val reduceMotion = rememberProReduceMotion()
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion

    AnimatedVisibility(
        visible = visible,
        enter = motion.forwardScreenEnter(reduceMotion),
        exit = motion.forwardScreenExit(reduceMotion),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(colors.paper)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
        ) {
            PickerScreenHeader(title = title, onClose = onClose)
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding)
                        .padding(top = dimens.space8, bottom = dimens.space16),
                content = content,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding)
                        .padding(bottom = dimens.space28),
            ) {
                footer()
            }
        }
    }
}

@Composable
private fun PickerScreenHeader(
    title: String,
    onClose: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space8),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Close,
            contentDescription = stringResource(R.string.close),
            tint = colors.onSurface,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .proIconClickable(onClick = onClose),
        )
        Text(
            text = title,
            style = typography.appBarTitle,
            color = colors.onSurface,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(
    name = "Picker screen shell",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PickerScreenShellPreview() {
    ProExpenseTheme {
        PickerScreenShell(
            visible = true,
            title = "Date & time",
            onClose = {},
            footer = {
                ProButton(
                    text = "Apply",
                    onClick = {},
                    size = ProButtonSize.Md,
                    fillMaxWidth = true,
                )
            },
        ) {
            Text("Content goes here", color = ProExpenseTheme.colors.onSurface)
        }
    }
}
