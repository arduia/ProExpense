package com.arduia.expense.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingIllustration(
    slideIndex: Int,
    modifier: Modifier = Modifier,
) {
    when (slideIndex) {
        0 -> WelcomeIllustration(modifier = modifier)
        1 -> QuickLogIllustration(modifier = modifier)
        2 -> SharedCostsIllustration(modifier = modifier)
        3 -> EventBudgetIllustration(modifier = modifier)
        4 -> JournalIllustration(modifier = modifier)
    }
}
