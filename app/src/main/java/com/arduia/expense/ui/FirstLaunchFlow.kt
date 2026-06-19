package com.arduia.expense.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.auth.PinSetupScreenContent
import com.arduia.expense.ui.auth.PinSetupStep
import com.arduia.expense.ui.currency.ProfileCurrencyScreenContent
import com.arduia.expense.ui.onboarding.OnboardingScreen
import com.arduia.expense.ui.onboarding.ProfileNameScreen
import com.arduia.expense.ui.onboarding.SplashScreen
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private enum class FirstLaunchStep {
    Splash,
    Onboarding,
    ProfileName,
    ProfileCurrency,
    PinSetup,
    Main,
}

@Composable
fun FirstLaunchFlow(
    modifier: Modifier = Modifier,
    startAtMain: Boolean = false,
) {
    var step by rememberSaveable {
        mutableStateOf(if (startAtMain) FirstLaunchStep.Main else FirstLaunchStep.Splash)
    }
    var profileName by rememberSaveable { mutableStateOf("") }
    var homeCurrency by rememberSaveable { mutableStateOf("USD") }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }

    when (step) {
        FirstLaunchStep.Splash -> SplashScreen(
            modifier = modifier,
            onFinished = { step = FirstLaunchStep.Onboarding },
        )
        FirstLaunchStep.Onboarding -> OnboardingScreen(
            modifier = modifier,
            onGetStarted = { step = FirstLaunchStep.ProfileName },
            onSkip = { step = FirstLaunchStep.ProfileName },
        )
        FirstLaunchStep.ProfileName -> ProfileNameScreen(
            modifier = modifier,
            initialName = profileName,
            onContinue = { name ->
                profileName = name
                step = FirstLaunchStep.ProfileCurrency
            },
            onSkip = { step = FirstLaunchStep.ProfileCurrency },
        )
        FirstLaunchStep.ProfileCurrency -> ProfileCurrencyScreenContent(
            modifier = modifier,
            selectedCode = homeCurrency,
            showPicker = showCurrencyPicker,
            onOpenPicker = { showCurrencyPicker = true },
            onClosePicker = { showCurrencyPicker = false },
            onCurrencySelected = {
                homeCurrency = it
                showCurrencyPicker = false
            },
            onContinue = { step = FirstLaunchStep.PinSetup },
            onSkip = { step = FirstLaunchStep.PinSetup },
        )
        FirstLaunchStep.PinSetup -> PinSetupScreenContent(
            modifier = modifier,
            step = PinSetupStep.Create,
            filledDots = 0,
            mismatchError = false,
            securityAnswer = "",
            onSecurityAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onContinueSecurity = { step = FirstLaunchStep.Main },
        )
        FirstLaunchStep.Main -> ExpenseApp(modifier = modifier)
    }
}

@Preview(
    name = "FirstLaunchFlow — onboarding",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun FirstLaunchOnboardingPreview() {
    ProExpenseTheme {
        OnboardingScreen(onGetStarted = {}, onSkip = {})
    }
}
