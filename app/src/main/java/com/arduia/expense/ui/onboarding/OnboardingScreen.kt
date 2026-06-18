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
import com.arduia.expense.ui.design.IconBack
import com.arduia.expense.ui.design.IconChevronRight
import com.arduia.expense.ui.design.PageDots
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
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

/** Flow 04 — five-slide value intro ending in "Get started". */
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
            .background(colors.paper),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.topBarHeight)
                .padding(horizontal = dims.space16),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (!isLast) {
                ProButton(
                    text = "Skip",
                    onClick = onFinish,
                    variant = ProButtonVariant.Ghost,
                    size = ProButtonSize.Md,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 0.dp),
        ) { index ->
            OnboardingSlideContent(index)
        }

        PageDots(
            count = onboardingSlides.size,
            activeIndex = page,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space28)
                .padding(bottom = dims.space22),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space16)
                .padding(bottom = dims.space16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.alpha(if (page == 0) 0f else 1f)) {
                ProButton(
                    text = "Back",
                    onClick = { if (page > 0) scope.launch { pagerState.animateScrollToPage(page - 1) } },
                    variant = ProButtonVariant.Ghost,
                    size = ProButtonSize.Md,
                    leading = { IconBack(color = colors.onSurfaceMuted, size = dims.iconSizeDefault) },
                )
            }
            Box(modifier = Modifier.alpha(if (isLast) 0f else 1f)) {
                ProButton(
                    text = "Next",
                    onClick = { if (!isLast) scope.launch { pagerState.animateScrollToPage(page + 1) } },
                    variant = ProButtonVariant.Ghost,
                    size = ProButtonSize.Md,
                    trailing = { IconChevronRight(color = colors.primaryDeep, size = dims.iconSizeDefault) },
                )
            }
        }

        ProButton(
            text = "Get started",
            onClick = onFinish,
            fillMaxWidth = true,
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
            style = ProExpenseTheme.typography.screenTitle,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(dims.space12))
        Text(
            text = slide.body,
            style = ProExpenseTheme.typography.subtitle,
            color = colors.onSurfaceMuted,
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
