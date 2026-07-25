package com.arduia.expense.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ui.preview.previewPinEntry
import com.arduia.expense.feature.auth.ui.preview.previewPinLock
import com.arduia.expense.feature.auth.ui.preview.previewPinWrong
import com.arduia.expense.ui.design.PinDots
import com.arduia.expense.ui.design.PinKeypadGrid
import com.arduia.expense.ui.design.PinKeypadState
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinEntryScreen(
    state: PinEntryUiState,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onBiometric: () -> Unit,
    onForgot: () -> Unit,
    modifier: Modifier = Modifier,
    headingRes: Int = R.string.pin_entry_heading,
    helperRes: Int = R.string.pin_entry_helper,
    showForgot: Boolean = true,
    onBack: (() -> Unit)? = null,
    backLabel: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val locked = state.mode == PinEntryMode.Locked
    val keypadState =
        when (state.mode) {
            PinEntryMode.Error -> PinKeypadState.Error
            PinEntryMode.Locked -> PinKeypadState.Locked
            PinEntryMode.Default -> PinKeypadState.Default
        }
    val titleColor = if (locked) colors.warning else colors.onSurface
    val helperColor =
        when (state.mode) {
            PinEntryMode.Error -> colors.danger
            PinEntryMode.Locked -> colors.warning
            PinEntryMode.Default -> colors.onSurfaceMuted
        }
    val title =
        if (locked) {
            stringResource(R.string.pin_entry_locked_title)
        } else {
            stringResource(headingRes)
        }
    val helper =
        when (state.mode) {
            PinEntryMode.Error -> stringResource(R.string.pin_entry_wrong)
            PinEntryMode.Locked -> stringResource(R.string.pin_entry_lock_helper)
            PinEntryMode.Default -> stringResource(helperRes)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                // This screen is rendered as an overlay sibling above other screens (e.g. the More
                // hub during the disable-PIN re-verify step). Without consuming taps here, a tap
                // landing on empty space (gaps between keypad buttons, margins) falls through to
                // whatever's rendered underneath, silently triggering its click handler.
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {})
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (onBack != null) {
            ProFlatHeader(
                title = "",
                onBack = onBack,
                backContentDescription = backLabel,
                modifier = Modifier.padding(vertical = dimens.space14),
            )
        }
        Spacer(Modifier.height(dimens.space44 + dimens.space44))
        PinBrandTile()
        Text(
            text = title,
            style = typography.profileScreenTitle,
            color = titleColor,
            modifier = Modifier.padding(top = dimens.space18),
        )
        Text(
            text = helper,
            style = typography.body,
            color = helperColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space8),
        )
        PinDots(
            filledCount = state.filledDots,
            state = keypadState,
            modifier = Modifier.padding(top = dimens.space16),
        )
        if (locked && state.countdownLabel != null) {
            Text(
                text = state.countdownLabel,
                style = typography.monoFigure.copy(fontSize = 30.sp),
                color = colors.warning,
                modifier = Modifier.padding(top = dimens.space16),
            )
            Text(
                text = stringResource(R.string.pin_entry_lock_countdown_helper),
                style = typography.caption,
                color = colors.warning,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = dimens.space6),
            )
        } else if (state.showBiometric) {
            Box(
                modifier =
                    Modifier
                        .padding(top = dimens.space20)
                        .size(dimens.space44 + dimens.space12)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, colors.lineStrong), CircleShape)
                        .proIconClickable(onClick = onBiometric),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Fingerprint,
                    contentDescription = stringResource(R.string.pin_biometric_cd),
                    tint = colors.onSurface,
                    size = dimens.iconNav,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        PinKeypadGrid(
            onDigit = onDigit,
            onBackspace = onBackspace,
            enabled = !locked,
            modifier =
                Modifier.alpha(
                    if (locked) ProExpenseTheme.motion.keypadDisabledOpacity else 1f,
                ),
        )
        if (showForgot) {
            ProTextAction(
                text = stringResource(R.string.pin_forgot),
                onClick = onForgot,
                style = typography.bodyMedium,
                color = colors.primary,
                modifier = Modifier.padding(top = dimens.space12, bottom = dimens.space12),
            )
        }
    }
}

@Composable
private fun PinBrandTile() {
    val colors = ProExpenseTheme.colors
    val size = 52.dp

    Box(
        modifier =
            Modifier
                .size(size)
                .clip(ProExpenseTheme.shapes.tile)
                .background(colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(size),
        )
    }
}

@Preview(
    name = "PIN entry — default",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinEntryPreview() {
    ProExpenseTheme {
        PinEntryScreen(previewPinEntry, {}, {}, {}, {})
    }
}

@Preview(
    name = "PIN entry — wrong",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinWrongPreview() {
    ProExpenseTheme {
        PinEntryScreen(previewPinWrong, {}, {}, {}, {})
    }
}

@Preview(
    name = "PIN entry — locked",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinLockPreview() {
    ProExpenseTheme {
        PinEntryScreen(previewPinLock, {}, {}, {}, {})
    }
}

@Preview(
    name = "PIN entry — verify to disable",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinVerifyDisablePreview() {
    ProExpenseTheme {
        PinEntryScreen(
            state = PinEntryUiState(filledDots = 4, showBiometric = false),
            onDigit = {},
            onBackspace = {},
            onBiometric = {},
            onForgot = {},
            headingRes = R.string.pin_disable_verify_heading,
            helperRes = R.string.pin_disable_verify_helper,
            showForgot = false,
            onBack = {},
            backLabel = "Cancel",
        )
    }
}

@Preview(
    name = "PIN entry — verify to disable (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinVerifyDisableDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        PinEntryScreen(
            state = PinEntryUiState(filledDots = 4, showBiometric = false),
            onDigit = {},
            onBackspace = {},
            onBiometric = {},
            onForgot = {},
            headingRes = R.string.pin_disable_verify_heading,
            helperRes = R.string.pin_disable_verify_helper,
            showForgot = false,
            onBack = {},
            backLabel = "Cancel",
        )
    }
}
