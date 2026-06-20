package com.arduia.expense.ui.onboarding

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val illustration: @Composable () -> Unit,
)

private val onboardingPages = listOf(
    OnboardingPage(
        R.string.onboarding_welcome_title,
        R.string.onboarding_welcome_subtitle,
        { WelcomeIllustration() },
    ),
    OnboardingPage(
        R.string.onboarding_quick_log_title,
        R.string.onboarding_quick_log_subtitle,
        { QuickLogIllustration() },
    ),
    OnboardingPage(
        R.string.onboarding_shared_costs_title,
        R.string.onboarding_shared_costs_subtitle,
        { SharedCostsIllustration() },
    ),
    OnboardingPage(
        R.string.onboarding_event_budget_title,
        R.string.onboarding_event_budget_subtitle,
        { EventBudgetIllustration() },
    ),
    OnboardingPage(
        R.string.onboarding_get_started_title,
        R.string.onboarding_get_started_subtitle,
        { GetStartedIllustration() },
    ),
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, onboardingPages.lastIndex),
        pageCount = { onboardingPages.size },
    )
    val scope = rememberCoroutineScope()
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space8),
        ) {
            if (!isLastPage) {
                Text(
                    text = stringResource(R.string.skip),
                    style = typography.bodyMedium,
                    color = colors.muted,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .proClickable(
                            onClick = onSkip,
                            shape = ProExpenseTheme.shapes.chip,
                        )
                        .padding(dimens.space8),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            val slide = onboardingPages[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.space24),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                slide.illustration()
                Spacer(modifier = Modifier.height(dimens.space32))
                Text(
                    text = stringResource(slide.titleRes),
                    style = typography.screenTitle,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(dimens.space12))
                Text(
                    text = stringResource(slide.subtitleRes),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = dimens.space16),
                )
            }
        }

        OnboardingPageIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = dimens.space16),
        )

        OnboardingPagerControls(
            currentPage = pagerState.currentPage,
            pageCount = onboardingPages.size,
            onBack = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNext = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space24, vertical = dimens.space8),
        )

        ProButton(
            text = stringResource(R.string.get_started),
            onClick = onGetStarted,
            size = ProButtonSize.Lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
        )
    }
}

@Composable
private fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val activeShape = RoundedCornerShape(50)
    val inactiveShape = CircleShape

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val active = index == currentPage
            Box(
                modifier = Modifier
                    .then(
                        if (active) {
                            Modifier
                                .width(24.dp)
                                .height(8.dp)
                        } else {
                            Modifier.size(8.dp)
                        },
                    )
                    .clip(if (active) activeShape else inactiveShape)
                    .background(if (active) colors.primary else colors.lineStrong),
            )
        }
    }
}

@Composable
private fun OnboardingPagerControls(
    currentPage: Int,
    pageCount: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val showBack = currentPage > 0
    val showNext = currentPage < pageCount - 1

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack) {
            Row(
                modifier = Modifier
                    .proClickable(
                        onClick = onBack,
                        shape = ProExpenseTheme.shapes.searchField,
                    )
                    .padding(horizontal = dimens.space8, vertical = dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space4),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Back,
                    contentDescription = null,
                    tint = colors.muted,
                    size = dimens.iconInline,
                )
                Text(
                    text = stringResource(R.string.back),
                    style = typography.bodyMedium,
                    color = colors.muted,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(72.dp))
        }

        if (showNext) {
            Row(
                modifier = Modifier
                    .proClickable(
                        onClick = onNext,
                        shape = ProExpenseTheme.shapes.searchField,
                    )
                    .padding(horizontal = dimens.space8, vertical = dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space4),
            ) {
                Text(
                    text = stringResource(R.string.next),
                    style = typography.bodyMedium,
                    color = colors.onSurface,
                )
                ProIcon(
                    glyph = ProIconGlyph.ChevronRight,
                    contentDescription = null,
                    tint = colors.onSurface,
                    size = dimens.iconInline,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(72.dp))
        }
    }
}

@Preview(
    name = "Onboarding — welcome",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun OnboardingWelcomePreview() {
    ProExpenseTheme {
        OnboardingScreen(onGetStarted = {}, onSkip = {}, initialPage = 0)
    }
}

@Preview(
    name = "Onboarding — get started",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun OnboardingGetStartedPreview() {
    ProExpenseTheme {
        OnboardingScreen(onGetStarted = {}, onSkip = {}, initialPage = 4)
    }
}
