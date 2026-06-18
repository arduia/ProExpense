package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.IconArrowLeft
import com.arduia.expense.ui.design.IconChevronRight
import com.arduia.expense.ui.design.ProButtonTone
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.design.ProTextButton
import com.arduia.expense.ui.design.PageDots
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch

private data class OnboardingSlide(val title: String, val body: String)

private val onboardingSlides = listOf(
    OnboardingSlide("Welcome", "Your personal finance notebook."),
    OnboardingSlide("Quick Log", "Log expenses in seconds — amount, category, done."),
    OnboardingSlide("Shared Costs", "Split bills instantly — total, people, done."),
    OnboardingSlide("Event Budget", "Plan and track any event budget — trips, weddings, parties."),
    OnboardingSlide("Journal", "Review your spending like a diary, day by day."),
)

/**
 * First-launch onboarding — a five-slide swipeable intro.
 * Mirrors android-onboarding.jsx · AndOnboarding (Material 3 rendition).
 *
 * @param onFinish invoked when the user skips or taps "Get started".
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { onboardingSlides.size },
    )
    val scope = rememberCoroutineScope()
    val page = pagerState.currentPage
    val isLast = page == onboardingSlides.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        // Top bar — Skip (hidden on the last slide)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.topBarHeight)
                .padding(horizontal = dims.space8),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (!isLast) {
                ProTextButton(text = "Skip", tone = ProButtonTone.Secondary, onClick = onFinish)
            }
        }

        // Hero pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 0.dp),
        ) { index ->
            OnboardingSlideContent(index)
        }

        // Page indicator
        PageDots(
            count = onboardingSlides.size,
            activeIndex = page,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space28)
                .padding(bottom = dims.space22),
        )

        // Stepper row: Back · Next (lifted clear of the CTA)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space16)
                .padding(bottom = dims.space16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.alpha(if (page == 0) 0f else 1f)) {
                ProTextButton(
                    text = "Back",
                    tone = ProButtonTone.Secondary,
                    onClick = { if (page > 0) scope.launch { pagerState.animateScrollToPage(page - 1) } },
                    leading = { IconArrowLeft(color = colors.onSurfaceVariant) },
                )
            }
            Box(modifier = Modifier.alpha(if (isLast) 0f else 1f)) {
                ProTextButton(
                    text = "Next",
                    onClick = { if (!isLast) scope.launch { pagerState.animateScrollToPage(page + 1) } },
                    trailing = { IconChevronRight(color = colors.primaryDeep) },
                )
            }
        }

        // Primary CTA
        ProFilledButton(
            text = "Get started",
            onClick = onFinish,
            modifier = Modifier
                .padding(horizontal = dims.space20)
                .padding(bottom = dims.space22),
        )
    }
}

@Composable
private fun OnboardingSlideContent(index: Int) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val slide = onboardingSlides[index]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .width(dims.onboardingIllustrationFrameWidth)
                .height(dims.onboardingIllustrationHeight),
            contentAlignment = Alignment.Center,
        ) {
            onboardingIllustrations[index](
                Modifier.size(
                    width = dims.onboardingIllustrationWidth,
                    height = dims.onboardingIllustrationHeight,
                ),
            )
        }
        Spacer(Modifier.height(dims.space30))
        Text(
            text = slide.title,
            style = ProExpenseTheme.typography.onboardingTitle,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(dims.space12))
        Text(
            text = slide.body,
            style = ProExpenseTheme.typography.onboardingBody,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = dims.onboardingBodyMaxWidth),
        )
    }
}

private const val PREVIEW_WIDTH = 414
private const val PREVIEW_HEIGHT = 868

@Preview(showBackground = true, widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun OnboardingScreenWelcomePreview() {
    ProExpenseTheme {
        OnboardingScreen(onFinish = {}, initialPage = 0)
    }
}

@Preview(showBackground = true, widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun OnboardingScreenJournalPreview() {
    ProExpenseTheme {
        OnboardingScreen(onFinish = {}, initialPage = onboardingSlides.lastIndex)
    }
}

@Preview(showBackground = true, widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun OnboardingQuickLogSlidePreview() {
    ProExpenseTheme {
        OnboardingSlideContent(1)
    }
}
