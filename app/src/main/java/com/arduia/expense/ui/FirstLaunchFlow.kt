package com.arduia.expense.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.di.AppGraph
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.ui.auth.PinSetupScreenContent
import com.arduia.expense.ui.currency.ProfileCurrencyScreenContent
import com.arduia.expense.ui.navigation.PinEntryRouteHost
import com.arduia.expense.ui.navigation.stepTransition
import com.arduia.expense.ui.onboarding.OnboardingScreen
import com.arduia.expense.ui.onboarding.ProfileNameScreen
import com.arduia.expense.ui.onboarding.SplashScreenContent
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.util.toUiPinSetupStep
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DURATION_MS = 1_800L

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
    appGraph: AppGraph,
    modifier: Modifier = Modifier,
    startAtMain: Boolean = false,
    isReturningUser: Boolean = false,
) {
    var step by rememberSaveable {
        mutableStateOf(if (startAtMain) FirstLaunchStep.Main else FirstLaunchStep.Splash)
    }
    var profileName by rememberSaveable { mutableStateOf("") }
    var homeCurrency by rememberSaveable { mutableStateOf(appGraph.homeCurrency()) }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val scope = rememberCoroutineScope()

    val pinSetupViewModel = remember {
        appGraph.createPinSetupViewModel(onComplete = { step = FirstLaunchStep.Main })
    }
    val pinSetupState by pinSetupViewModel.uiState.collectAsState()

    LaunchedEffect(step) {
        if (step == FirstLaunchStep.Splash) {
            delay(SPLASH_DURATION_MS)
            val hasPin = appGraph.securityStateReader.hasPinConfigured()
            step = when {
                isReturningUser || hasPin -> FirstLaunchStep.PinEntry
                else -> FirstLaunchStep.Onboarding
            }
        }
    }

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
            FirstLaunchStep.Splash -> SplashScreenContent(modifier = Modifier)
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
                onContinue = {
                    scope.launch {
                        appGraph.currencyRepository.setHomeCurrency(CurrencyCode(homeCurrency))
                    }
                    step = FirstLaunchStep.PinSetup
                },
                onSkip = { step = FirstLaunchStep.PinSetup },
            )
            FirstLaunchStep.PinSetup -> PinSetupScreenContent(
                modifier = Modifier,
                step = pinSetupState.step.toUiPinSetupStep(),
                filledDots = pinSetupState.filledDots,
                mismatchError = pinSetupState.mismatchError,
                securityAnswer = pinSetupState.securityAnswer,
                onSecurityAnswerChange = pinSetupViewModel::onSecurityAnswerChange,
                onDigit = pinSetupViewModel::onDigit,
                onBackspace = pinSetupViewModel::onBackspace,
                onContinueSecurity = pinSetupViewModel::onContinueSecurity,
                onBiometricAccepted = { pinSetupViewModel.onBiometricOffered(true) },
                onBiometricSkipped = { pinSetupViewModel.onBiometricOffered(false) },
            )
            FirstLaunchStep.PinEntry -> PinEntryRouteHost(
                modifier = Modifier,
                appGraph = appGraph,
                onUnlocked = { step = FirstLaunchStep.Main },
            )
            FirstLaunchStep.Main -> ExpenseApp(
                appGraph = appGraph,
                modifier = Modifier,
                onPinSetupRequested = { step = FirstLaunchStep.PinSetup },
            )
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
