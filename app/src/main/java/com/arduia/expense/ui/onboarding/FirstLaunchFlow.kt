package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.currency.ui.profile.ProfileCurrencyScreen
import com.arduia.expense.feature.currency.ui.profile.ProfileCurrencySheet
import com.arduia.expense.feature.currency.ui.profile.ProfileNameScreen
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.delay

private const val SPLASH_DISPLAY_MS = 1_800L

private enum class FirstLaunchStep {
    Splash,
    Onboarding,
    ProfileName,
    ProfileCurrency,
}

data class ProfileSetupState(
    val name: String = "",
    val homeCurrencyCode: String = "USD",
)

/**
 * Flow 04 — First Launch: Splash → Onboarding → Profile Setup (name + currency) → Home.
 * Mirrors the Hi-Fi Flow 04 artboards and android-onboarding.jsx Material rendition.
 */
@Composable
fun FirstLaunchFlow(
    onComplete: (ProfileSetupState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by rememberSaveable { mutableStateOf(FirstLaunchStep.Splash) }
    var profileName by rememberSaveable { mutableStateOf("") }
    var homeCurrency by rememberSaveable { mutableStateOf("USD") }
    var showCurrencySheet by rememberSaveable { mutableStateOf(false) }
    var currencySearch by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(step) {
        if (step == FirstLaunchStep.Splash) {
            delay(SPLASH_DISPLAY_MS)
            step = FirstLaunchStep.Onboarding
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (step) {
            FirstLaunchStep.Splash -> SplashScreen()
            FirstLaunchStep.Onboarding -> OnboardingScreen(
                onFinish = { step = FirstLaunchStep.ProfileName },
            )
            FirstLaunchStep.ProfileName -> ProfileNameScreen(
                name = profileName,
                onNameChange = { profileName = it },
                onContinue = { step = FirstLaunchStep.ProfileCurrency },
            )
            FirstLaunchStep.ProfileCurrency -> ProfileCurrencyScreen(
                selectedCode = homeCurrency,
                onCurrencySelected = { homeCurrency = it },
                onStartTracking = {
                    onComplete(ProfileSetupState(name = profileName, homeCurrencyCode = homeCurrency))
                },
                onShowAllCurrencies = { showCurrencySheet = true },
            )
        }

        if (showCurrencySheet && step == FirstLaunchStep.ProfileCurrency) {
            ProfileCurrencySheet(
                searchQuery = currencySearch,
                onSearchQueryChange = { currencySearch = it },
                onCurrencySelected = { code ->
                    homeCurrency = code
                    showCurrencySheet = false
                    currencySearch = ""
                },
                onDismiss = {
                    showCurrencySheet = false
                    currencySearch = ""
                },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun FirstLaunchFlowOnboardingPreview() {
    ProExpenseTheme {
        OnboardingScreen(onFinish = {})
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun FirstLaunchFlowProfileNamePreview() {
    ProExpenseTheme {
        ProfileNameScreen(
            name = "Maya",
            onNameChange = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun FirstLaunchFlowProfileCurrencyPreview() {
    ProExpenseTheme {
        ProfileCurrencyScreen(
            selectedCode = "USD",
            onCurrencySelected = {},
            onStartTracking = {},
            onShowAllCurrencies = {},
        )
    }
}
