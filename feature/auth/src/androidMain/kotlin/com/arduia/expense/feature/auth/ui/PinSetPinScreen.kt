package com.arduia.expense.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ui.preview.previewPinSetConfirmMismatch
import com.arduia.expense.ui.design.PinDots
import com.arduia.expense.ui.design.PinKeypadGrid
import com.arduia.expense.ui.design.PinKeypadState
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinSetPinScreen(
    state: PinEntryUiState,
    headingRes: Int,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val isError = state.mode == PinEntryMode.Error
    var revealed by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ProTopBar(
                title = stringResource(R.string.pin_set_appbar),
                onBack = onBack,
                backLabel = stringResource(R.string.pin_setup_back),
            )
        }

        Text(
            text = stringResource(headingRes),
            style = typography.profileScreenTitle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space24),
        )
        if (isError) {
            Text(
                text = stringResource(R.string.pin_mismatch_helper),
                style = typography.body,
                color = colors.danger,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            PinDots(
                filledCount = state.filledDots,
                state = if (isError) PinKeypadState.Error else PinKeypadState.Default,
                digitsText = if (revealed) state.digits else null,
                modifier = Modifier.padding(top = dimens.space16),
            )
            ProIcon(
                glyph = if (revealed) ProIconGlyph.EyeOff else ProIconGlyph.Eye,
                contentDescription = "Toggle PIN reveal",
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = dimens.space16, end = dimens.space16)
                        .proIconClickable(onClick = { revealed = !revealed }),
                tint = colors.onSurfaceMuted,
            )
        }

        Spacer(Modifier.weight(1f))

        PinKeypadGrid(
            onDigit = onDigit,
            onBackspace = onBackspace,
            modifier = Modifier.padding(bottom = dimens.space24),
        )
    }
}

@Preview(
    name = "PIN set — mismatch",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinSetMismatchPreview() {
    ProExpenseTheme {
        PinSetPinScreen(
            state = previewPinSetConfirmMismatch,
            headingRes = R.string.pin_confirm_heading,
            onDigit = {},
            onBackspace = {},
            onBack = {},
        )
    }
}
