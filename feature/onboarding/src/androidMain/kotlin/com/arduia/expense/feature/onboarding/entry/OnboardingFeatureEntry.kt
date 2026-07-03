package com.arduia.expense.feature.onboarding.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.feature.onboarding.OnboardingCompleteHandoff
import com.arduia.expense.feature.onboarding.ui.FirstLaunchFlow

interface OnboardingFeatureEntry {
    @Composable
    fun FirstLaunchFlow(
        onComplete: (OnboardingCompleteHandoff) -> Unit,
        modifier: Modifier = Modifier,
        onThemeModeChanged: (ThemeMode) -> Unit = {},
        onLanguageChanged: () -> Unit = {},
    )
}

internal class OnboardingFeatureEntryImpl : OnboardingFeatureEntry {
    @Composable
    override fun FirstLaunchFlow(
        onComplete: (OnboardingCompleteHandoff) -> Unit,
        modifier: Modifier,
        onThemeModeChanged: (ThemeMode) -> Unit,
        onLanguageChanged: () -> Unit,
    ) {
        com.arduia.expense.feature.onboarding.ui.FirstLaunchFlow(
            onComplete = { name, currencyCode ->
                onComplete(OnboardingCompleteHandoff(profileName = name, currencyCode = currencyCode))
            },
            modifier = modifier,
            onThemeModeChanged = onThemeModeChanged,
            onLanguageChanged = onLanguageChanged,
        )
    }
}

object OnboardingFeatureUi : OnboardingFeatureEntry by OnboardingFeatureEntryImpl()
