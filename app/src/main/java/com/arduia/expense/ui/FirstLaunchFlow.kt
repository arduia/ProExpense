package com.arduia.expense.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.auth.PinSetupScreenContent
import com.arduia.expense.ui.auth.PinSetupStep
import com.arduia.expense.ui.currency.ProfileCurrencyScreenContent
import com.arduia.expense.ui.navigation.PinEntryRouteHost
import com.arduia.expense.ui.navigation.stepTransition
import com.arduia.expense.ui.onboarding.OnboardingScreen
import com.arduia.expense.ui.onboarding.ProfileNameScreen
import com.arduia.expense.ui.onboarding.SplashScreen
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion

private enum class FirstLaunchStep {
    Splash,
    Onboarding,
    ProfileName,
    ProfileCurrency,
    PinSetup,
    PinEntry,
    Main,
}

@Composable
fun FirstLaunchFlow(
    modifier: Modifier = Modifier,
    startAtMain: Boolean = false,
    isReturningUser: Boolean = false,
) {
    var step by rememberSaveable {
        mutableStateOf(if (startAtMain) FirstLaunchStep.Main else FirstLaunchStep.Splash)
    }
    var profileName by rememberSaveable { mutableStateOf("") }
    var homeCurrency by rememberSaveable { mutableStateOf("USD") }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }
    var pinSetupStep by rememberSaveable { mutableStateOf(PinSetupStep.Create) }
    var pinDots by rememberSaveable { mutableIntStateOf(0) }
    var securityAnswer by rememberSaveable { mutableStateOf("") }
    var mismatchError by rememberSaveable { mutableStateOf(false) }
    var createdPin by remember { mutableStateOf("") }
    var currentPinEntry by remember { mutableStateOf("") }
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    AnimatedContent(
        targetState = step,
        modifier = modifier,
        transitionSpec = {
            stepTransition(
                motion = motion,
                fromIndex = initialState.ordinal,
                toIndex = targetState.ordinal,
                reduceMotion = reduceMotion,
            )
        },
        label = "firstLaunchNav",
    ) { currentStep ->
        when (currentStep) {
            FirstLaunchStep.Splash -> SplashScreen(
                modifier = Modifier,
                onFinished = {
                    step = when {
                        isReturningUser -> FirstLaunchStep.PinEntry
                        else -> FirstLaunchStep.Onboarding
                    }
                },
            )
            FirstLaunchStep.Onboarding -> OnboardingScreen(
                modifier = Modifier,
                onGetStarted = { step = FirstLaunchStep.ProfileName },
                onSkip = { step = FirstLaunchStep.ProfileName },
            )
            FirstLaunchStep.ProfileName -> ProfileNameScreen(
                modifier = Modifier,
                initialName = profileName,
                onContinue = { name ->
                    profileName = name
                    step = FirstLaunchStep.ProfileCurrency
                },
                onSkip = { step = FirstLaunchStep.ProfileCurrency },
            )
            FirstLaunchStep.ProfileCurrency -> ProfileCurrencyScreenContent(
                modifier = Modifier,
                selectedCode = homeCurrency,
                showPicker = showCurrencyPicker,
                onOpenPicker = { showCurrencyPicker = true },
                onClosePicker = { showCurrencyPicker = false },
                onCurrencySelected = {
                    homeCurrency = it
                    showCurrencyPicker = false
                },
                onContinue = { step = FirstLaunchStep.Main },
                onSkip = { step = FirstLaunchStep.Main },
            )
            FirstLaunchStep.PinSetup -> PinSetupScreenContent(
                modifier = Modifier,
                step = pinSetupStep,
                filledDots = pinDots,
                mismatchError = mismatchError,
                securityAnswer = securityAnswer,
                onSecurityAnswerChange = { securityAnswer = it },
                onDigit = { digit ->
                    if (currentPinEntry.length >= 6) return@PinSetupScreenContent
                    currentPinEntry += digit.toString()
                    pinDots = currentPinEntry.length
                    if (currentPinEntry.length == 6) {
                        when (pinSetupStep) {
                            PinSetupStep.Create -> {
                                createdPin = currentPinEntry
                                currentPinEntry = ""
                                pinDots = 0
                                mismatchError = false
                                pinSetupStep = PinSetupStep.Confirm
                            }
                            PinSetupStep.Confirm -> {
                                if (currentPinEntry == createdPin) {
                                    currentPinEntry = ""
                                    pinDots = 0
                                    mismatchError = false
                                    pinSetupStep = PinSetupStep.SecurityQuestion
                                } else {
                                    currentPinEntry = ""
                                    pinDots = 0
                                    mismatchError = true
                                }
                            }
                            PinSetupStep.SecurityQuestion -> Unit
                        }
                    }
                },
                onBackspace = {
                    if (currentPinEntry.isNotEmpty()) {
                        currentPinEntry = currentPinEntry.dropLast(1)
                        pinDots = currentPinEntry.length
                        mismatchError = false
                    }
                },
                onContinueSecurity = { step = FirstLaunchStep.Main },
            )
            FirstLaunchStep.PinEntry -> PinEntryRouteHost(
                modifier = Modifier,
                onUnlocked = { step = FirstLaunchStep.Main },
                demoSecurityAnswer = securityAnswer.ifBlank { "notebook" },
            )
            FirstLaunchStep.Main -> ExpenseApp(modifier = Modifier)
        }
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
