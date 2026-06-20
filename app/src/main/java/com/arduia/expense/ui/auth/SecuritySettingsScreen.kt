package com.arduia.expense.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SecuritySettingsScreenContent(
    isPinEnabled: Boolean,
    isBiometricEnabled: Boolean,
    biometricAvailable: Boolean,
    message: String?,
    onBack: () -> Unit,
    onChangePin: () -> Unit,
    onDisablePin: () -> Unit,
    onBiometricToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.more_security),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            if (isPinEnabled) {
                ProButton(
                    text = stringResource(R.string.security_change_pin),
                    onClick = onChangePin,
                    size = ProButtonSize.Lg,
                    modifier = Modifier.fillMaxWidth(),
                )
                ProButton(
                    text = stringResource(R.string.security_disable_pin),
                    onClick = onDisablePin,
                    variant = ProButtonVariant.Ghost,
                    size = ProButtonSize.Lg,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (biometricAvailable && isPinEnabled) {
                ProButton(
                    text = if (isBiometricEnabled) {
                        stringResource(R.string.security_biometric_disable)
                    } else {
                        stringResource(R.string.security_biometric_enable)
                    },
                    onClick = { onBiometricToggled(!isBiometricEnabled) },
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Lg,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (message != null) {
                Text(text = message, style = typography.caption, color = colors.danger)
            }
        }
    }
}

@Preview(
    name = "Security settings",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SecuritySettingsPreview() {
    ProExpenseTheme {
        SecuritySettingsScreenContent(
            isPinEnabled = true,
            isBiometricEnabled = false,
            biometricAvailable = true,
            message = null,
            onBack = {},
            onChangePin = {},
            onDisablePin = {},
            onBiometricToggled = {},
        )
    }
}
