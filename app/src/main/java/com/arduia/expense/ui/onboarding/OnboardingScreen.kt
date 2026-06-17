package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProExpenseButton
import com.arduia.expense.ui.design.ProExpenseButtonSize
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun OnboardingScreen(
    currentPage: Int,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val slide = OnboardingSlides[currentPage]
    val isLastPage = currentPage == ONBOARDING_PAGE_COUNT - 1
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        OnboardingTopBar(
            onSkip = onSkip,
            showSkip = !isLastPage,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OnboardingIllustration(
                slideIndex = currentPage,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .height(220.dp),
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = slide.title,
                style = typography.onboardingTitle,
                color = colors.ink,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = slide.body,
                modifier = Modifier.widthIn(max = 280.dp),
                style = typography.onboardingBody,
                color = colors.ink2,
                textAlign = TextAlign.Center,
            )
        }

        OnboardingNavRow(
            currentPage = currentPage,
            pageCount = ONBOARDING_PAGE_COUNT,
            onBack = onBack,
            onNext = onNext,
            showNext = !isLastPage,
        )

        ProExpenseButton(
            text = "Get started",
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 38.dp),
            size = ProExpenseButtonSize.Large,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            OnboardingScreen(
                currentPage = 0,
                onSkip = {},
                onBack = {},
                onNext = {},
                onGetStarted = {},
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun OnboardingJournalScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            OnboardingScreen(
                currentPage = 4,
                onSkip = {},
                onBack = {},
                onNext = {},
                onGetStarted = {},
            )
        }
    }
}
