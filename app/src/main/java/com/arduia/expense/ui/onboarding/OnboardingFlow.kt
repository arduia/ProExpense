package com.arduia.expense.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

private const val SPLASH_DISPLAY_MS = 1_800L

@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(showSplash) {
        if (showSplash) {
            delay(SPLASH_DISPLAY_MS)
            showSplash = false
        }
    }

    if (showSplash) {
        SplashScreen(modifier = modifier)
    } else {
        OnboardingScreen(
            modifier = modifier,
            currentPage = currentPage,
            onSkip = onComplete,
            onBack = { currentPage = (currentPage - 1).coerceAtLeast(0) },
            onNext = { currentPage = (currentPage + 1).coerceAtMost(ONBOARDING_PAGE_COUNT - 1) },
            onGetStarted = onComplete,
        )
    }
}
