package com.arduia.expense.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class FirstLaunchStep {
    Onboarding,
    ProfileSetup,
}

@Composable
fun FirstLaunchFlow(
    onComplete: (name: String, currencyCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var stepName by rememberSaveable { mutableStateOf(FirstLaunchStep.Onboarding.name) }
    var name by rememberSaveable { mutableStateOf("") }
    var selectedCurrencyCode by rememberSaveable { mutableStateOf("USD") }
    var showCurrencySheet by rememberSaveable { mutableStateOf(false) }
    var currencySearchQuery by rememberSaveable { mutableStateOf("") }

    val step = FirstLaunchStep.valueOf(stepName)

    val profileState =
        ProfileSetupState(
            name = name,
            selectedCurrencyCode = selectedCurrencyCode,
            showCurrencySheet = showCurrencySheet,
            currencySearchQuery = currencySearchQuery,
        )

    when (step) {
        FirstLaunchStep.Onboarding -> {
            OnboardingScreenContent(
                modifier = modifier,
                onGetStarted = { stepName = FirstLaunchStep.ProfileSetup.name },
                onSkip = { stepName = FirstLaunchStep.ProfileSetup.name },
            )
        }
        FirstLaunchStep.ProfileSetup -> {
            ProfileSetupScreenContent(
                modifier = modifier,
                state = profileState,
                onNameChange = { name = it },
                onStartTracking = { onComplete(name.trim(), selectedCurrencyCode) },
                onCurrencySelected = { selectedCurrencyCode = it },
                onOpenCurrencySheet = { showCurrencySheet = true },
                onCloseCurrencySheet = {
                    showCurrencySheet = false
                    currencySearchQuery = ""
                },
                onCurrencySearchChange = { currencySearchQuery = it },
            )
        }
    }
}

@Preview(
    name = "First launch — onboarding",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun FirstLaunchOnboardingPreview() {
    ProExpenseTheme {
        FirstLaunchFlow(onComplete = { _, _ -> })
    }
}

@Preview(
    name = "First launch — profile name",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun FirstLaunchProfilePreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(name = "Maya"),
            onNameChange = {},
            onStartTracking = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}
