package com.arduia.expense.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class FirstLaunchStep {
    Onboarding,
    ProfileSetup,
}

@Composable
fun FirstLaunchFlow(
    onComplete: (name: String, currencyCode: String) -> Unit,
    modifier: Modifier = Modifier,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    onLanguageChanged: () -> Unit = {},
) {
    val themeRepository: ThemeRepository = koinInject()
    val localeRepository: LocaleRepository = koinInject()
    val scope = rememberCoroutineScope()

    var stepName by rememberSaveable { mutableStateOf(FirstLaunchStep.Onboarding.name) }
    var name by rememberSaveable { mutableStateOf("") }
    var selectedCurrencyCode by rememberSaveable { mutableStateOf("USD") }
    var showCurrencySheet by rememberSaveable { mutableStateOf(false) }
    var currencySearchQuery by rememberSaveable { mutableStateOf("") }
    // Dark/English match AppMetaSnapshot.DEFAULT and ThemedExpenseApp's preload placeholder, so
    // the picker opens with the option already in effect pre-selected, not a mismatched default.
    var languageTag by rememberSaveable { mutableStateOf(AppLanguage.DEFAULT.tag) }
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.DARK) }

    LaunchedEffect(Unit) {
        when (val result = themeRepository.getThemeMode()) {
            is Result.Success -> themeMode = result.data
            is Result.Error -> Unit
        }
        when (val result = localeRepository.getLanguageTag()) {
            is Result.Success -> if (result.data.isNotBlank()) languageTag = result.data
            is Result.Error -> Unit
        }
    }

    val step = FirstLaunchStep.valueOf(stepName)

    val profileState = ProfileSetupState(
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
                languageTag = languageTag,
                themeMode = themeMode,
                onLanguageSelected = { tag ->
                    languageTag = tag
                    // Persist first (synchronous SharedPrefs write inside), then recreate the
                    // Activity — attachBaseContext() re-reads the fresh value on the new instance.
                    // stepName/name/etc. are all rememberSaveable, so onboarding resumes where it
                    // left off, just re-rendered in the new language.
                    scope.launch {
                        localeRepository.setLanguageTag(tag)
                        onLanguageChanged()
                    }
                },
                onThemeSelected = { mode ->
                    themeMode = mode
                    onThemeModeChanged(mode)
                    scope.launch { themeRepository.setThemeMode(mode) }
                },
            )
        }
        FirstLaunchStep.ProfileSetup -> {
            ProfileSetupScreenContent(
                modifier = modifier,
                state = profileState,
                onNameChange = { name = it },
                onStartTracking = { onComplete(name.trim(), selectedCurrencyCode) },
                onSkip = { onComplete(name.trim(), selectedCurrencyCode) },
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
            onSkip = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}
