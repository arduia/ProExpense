package com.arduia.expense.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arduia.expense.R

private val illustrationWidth = 300.dp
private val illustrationHeight = 280.dp

@Composable
private fun HandoffOnboardingIllustration(
    @DrawableRes drawableRes: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(width = illustrationWidth, height = illustrationHeight),
    )
}

@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    HandoffOnboardingIllustration(
        drawableRes = R.drawable.onboarding_illustration_welcome,
        modifier = modifier,
    )
}

@Composable
fun QuickLogIllustration(modifier: Modifier = Modifier) {
    HandoffOnboardingIllustration(
        drawableRes = R.drawable.onboarding_illustration_quick_log,
        modifier = modifier,
    )
}

@Composable
fun SharedCostsIllustration(modifier: Modifier = Modifier) {
    HandoffOnboardingIllustration(
        drawableRes = R.drawable.onboarding_illustration_shared_costs,
        modifier = modifier,
    )
}

@Composable
fun EventBudgetIllustration(modifier: Modifier = Modifier) {
    HandoffOnboardingIllustration(
        drawableRes = R.drawable.onboarding_illustration_event_budget,
        modifier = modifier,
    )
}

@Composable
fun JournalIllustration(modifier: Modifier = Modifier) {
    HandoffOnboardingIllustration(
        drawableRes = R.drawable.onboarding_illustration_journal,
        modifier = modifier,
    )
}
