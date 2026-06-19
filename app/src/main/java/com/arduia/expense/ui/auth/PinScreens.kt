package com.arduia.expense.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.arduia.expense.R
import com.arduia.expense.ui.design.PinKeypad
import com.arduia.expense.ui.design.PinKeypadState
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class PinSetupStep {
    Create,
    Confirm,
    SecurityQuestion,
}

@Composable
fun PinSetupScreenContent(
    step: PinSetupStep,
    filledDots: Int,
    mismatchError: Boolean,
    securityAnswer: String,
    onSecurityAnswerChange: (String) -> Unit,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onContinueSecurity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val keypadState = if (mismatchError) PinKeypadState.Error else PinKeypadState.Default

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.space18),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(dimens.space32))
        Text(
            text = when (step) {
                PinSetupStep.Create -> stringResource(R.string.pin_setup_title)
                PinSetupStep.Confirm -> stringResource(R.string.pin_confirm_title)
                PinSetupStep.SecurityQuestion -> stringResource(R.string.pin_security_title)
            },
            style = typography.screenTitle,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = when (step) {
                PinSetupStep.Create -> stringResource(R.string.pin_setup_subtitle)
                PinSetupStep.Confirm -> stringResource(R.string.pin_setup_subtitle)
                PinSetupStep.SecurityQuestion -> stringResource(R.string.pin_security_subtitle)
            },
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space12),
        )

        if (step == PinSetupStep.SecurityQuestion) {
            ProfileNameField(
                value = securityAnswer,
                onValueChange = onSecurityAnswerChange,
                placeholder = stringResource(R.string.pin_security_hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space32),
            )
            Spacer(modifier = Modifier.weight(1f))
            ProButton(
                text = stringResource(R.string.continue_label),
                onClick = onContinueSecurity,
                enabled = securityAnswer.isNotBlank(),
                size = ProButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space24),
            )
        } else {
            if (mismatchError) {
                Text(
                    text = stringResource(R.string.pin_mismatch),
                    style = typography.caption,
                    color = colors.danger,
                    modifier = Modifier.padding(top = dimens.space12),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            PinKeypad(
                filledDots = filledDots,
                onDigit = onDigit,
                onBackspace = onBackspace,
                state = keypadState,
                modifier = Modifier.padding(bottom = dimens.space24),
            )
        }
    }
}

enum class PinEntryMode {
    Entry,
    Lockout,
    Wrong,
    Recovery,
}

@Composable
fun PinEntryScreenContent(
    mode: PinEntryMode,
    filledDots: Int,
    lockoutSeconds: Int,
    recoveryAnswer: String,
    onRecoveryAnswerChange: (String) -> Unit,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onForgotPin: () -> Unit,
    onUnlockRecovery: () -> Unit,
    onBiometricUnlock: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val keypadState = when (mode) {
        PinEntryMode.Lockout -> PinKeypadState.Locked
        PinEntryMode.Wrong -> PinKeypadState.Error
        else -> PinKeypadState.Default
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.space18),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(dimens.space44))
        Text(
            text = when (mode) {
                PinEntryMode.Recovery -> stringResource(R.string.pin_recovery_title)
                else -> stringResource(R.string.pin_entry_title)
            },
            style = typography.screenTitle,
            color = colors.onSurface,
        )
        if (mode != PinEntryMode.Recovery) {
            Text(
                text = stringResource(R.string.pin_entry_subtitle),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }

        if (mode == PinEntryMode.Wrong) {
            Text(
                text = stringResource(R.string.pin_wrong),
                style = typography.caption,
                color = colors.danger,
                modifier = Modifier.padding(top = dimens.space12),
            )
        }

        if (mode == PinEntryMode.Recovery) {
            ProfileNameField(
                value = recoveryAnswer,
                onValueChange = onRecoveryAnswerChange,
                placeholder = stringResource(R.string.pin_security_hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space32),
            )
            Spacer(modifier = Modifier.weight(1f))
            ProButton(
                text = stringResource(R.string.continue_label),
                onClick = onUnlockRecovery,
                enabled = recoveryAnswer.isNotBlank(),
                size = ProButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space24),
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
            if (mode == PinEntryMode.Entry) {
                ProIcon(
                    glyph = ProIconGlyph.Fingerprint,
                    contentDescription = stringResource(R.string.pin_entry_subtitle),
                    tint = colors.primary,
                    modifier = Modifier
                        .padding(bottom = dimens.space16)
                        .proClickable(onClick = onBiometricUnlock, shape = ProExpenseTheme.shapes.chip)
                        .padding(dimens.space8),
                )
            }
            PinKeypad(
                filledDots = filledDots,
                onDigit = onDigit,
                onBackspace = onBackspace,
                state = keypadState,
                lockoutMessage = if (mode == PinEntryMode.Lockout) {
                    stringResource(R.string.pin_lockout, lockoutSeconds)
                } else {
                    null
                },
                modifier = Modifier.padding(bottom = dimens.space16),
            )
            Text(
                text = stringResource(R.string.pin_forgot),
                style = typography.bodyMedium,
                color = colors.primary,
                modifier = Modifier
                    .proClickable(onClick = onForgotPin, shape = ProExpenseTheme.shapes.chip)
                    .padding(bottom = dimens.space24),
            )
        }
    }
}

@Preview(name = "PIN setup", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun PinSetupPreview() {
    ProExpenseTheme {
        PinSetupScreenContent(
            step = PinSetupStep.Create,
            filledDots = 3,
            mismatchError = false,
            securityAnswer = "",
            onSecurityAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onContinueSecurity = {},
        )
    }
}

@Preview(name = "PIN security", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun PinSecurityPreview() {
    ProExpenseTheme {
        PinSetupScreenContent(
            step = PinSetupStep.SecurityQuestion,
            filledDots = 0,
            mismatchError = false,
            securityAnswer = "",
            onSecurityAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onContinueSecurity = {},
        )
    }
}

@Preview(name = "PIN entry", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun PinEntryPreview() {
    ProExpenseTheme {
        PinEntryScreenContent(
            mode = PinEntryMode.Entry,
            filledDots = 4,
            lockoutSeconds = 0,
            recoveryAnswer = "",
            onRecoveryAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onForgotPin = {},
            onUnlockRecovery = {},
        )
    }
}

@Preview(name = "PIN lockout", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun PinLockoutPreview() {
    ProExpenseTheme {
        PinEntryScreenContent(
            mode = PinEntryMode.Lockout,
            filledDots = 0,
            lockoutSeconds = 30,
            recoveryAnswer = "",
            onRecoveryAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onForgotPin = {},
            onUnlockRecovery = {},
        )
    }
}
