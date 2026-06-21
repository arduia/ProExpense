package com.arduia.expense.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.auth.R
import com.arduia.expense.ui.design.PinDots
import com.arduia.expense.ui.design.PinKeypadGrid
import com.arduia.expense.ui.design.PinKeypadState
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.feature.auth.ui.preview.PinEntryMode
import com.arduia.expense.feature.auth.ui.preview.PinEntryUiState
import com.arduia.expense.feature.auth.ui.preview.previewPinSetConfirmMismatch
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinSetPinScreen(
    state: PinEntryUiState,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val isError = state.mode == PinEntryMode.Error

    Column(
        modifier = modifier
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
            text = stringResource(R.string.pin_confirm_heading),
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
        PinDots(
            filledCount = state.filledDots,
            state = if (isError) PinKeypadState.Error else PinKeypadState.Default,
            modifier = Modifier.padding(top = dimens.space16),
        )

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
            onDigit = {},
            onBackspace = {},
            onBack = {},
        )
    }
}
