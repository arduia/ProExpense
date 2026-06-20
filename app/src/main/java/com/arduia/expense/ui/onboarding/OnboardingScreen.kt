package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.OnboardingIllustration
import com.arduia.expense.ui.design.OnboardingPageIndicator
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proRippleClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val illustration: OnboardingIllustration,
    val titleRes: Int,
    val bodyRes: Int,
)

private val onboardingSlides = listOf(
    OnboardingSlide(OnboardingIllustration.Welcome, R.string.onboarding_welcome_title, R.string.onboarding_welcome_subtitle),
    OnboardingSlide(OnboardingIllustration.QuickLog, R.string.onboarding_quick_log_title, R.string.onboarding_quick_log_subtitle),
    OnboardingSlide(OnboardingIllustration.SharedCosts, R.string.onboarding_shared_costs_title, R.string.onboarding_shared_costs_subtitle),
    OnboardingSlide(OnboardingIllustration.EventBudget, R.string.onboarding_event_budget_title, R.string.onboarding_event_budget_subtitle),
    OnboardingSlide(OnboardingIllustration.Journal, R.string.onboarding_journal_title, R.string.onboarding_journal_subtitle),
)

@Composable
fun OnboardingScreenContent(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val pagerState = rememberPagerState(initialPage = initialPage) { onboardingSlides.size }
    val scope = rememberCoroutineScope()
    val lastPage = onboardingSlides.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.screenPadding),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8, bottom = dimens.space16),
        ) {
            if (pagerState.currentPage < lastPage) {
                Text(
                    text = stringResource(R.string.skip),
                    style = typography.navAction,
                    color = colors.muted,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .minimumInteractiveComponentSize()
                        .proRippleClickable(onClick = onSkip),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            val slide = onboardingSlides[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OnboardingIllustration(type = slide.illustration)
                Text(
                    text = stringResource(slide.titleRes),
                    style = typography.onboardingSlideTitle.copy(fontFamily = typography.serifFamily),
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimens.space28),
                )
                Text(
                    text = stringResource(slide.bodyRes),
                    style = typography.onboardingBody,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimens.space10, start = dimens.space8, end = dimens.space8),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.onboardingNavBottomMargin),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pagerState.currentPage > 0) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .proRippleClickable(
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                            },
                        ),
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
                        style = typography.navAction,
                        color = colors.muted,
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f))
            }

            OnboardingPageIndicator(
                pageCount = onboardingSlides.size,
                currentPage = pagerState.currentPage,
            )

            if (pagerState.currentPage < lastPage) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .proRippleClickable(
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = stringResource(R.string.next),
                        style = typography.navAction.copy(
                            fontWeight = typography.bodySemiBold.fontWeight,
                        ),
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
                Box(modifier = Modifier.weight(1f))
            }
        }

        ProButton(
            text = stringResource(R.string.get_started),
            onClick = onGetStarted,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier.padding(bottom = dimens.space18),
        )
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
        OnboardingScreenContent(onGetStarted = {}, onSkip = {})
    }
}

@Preview(
    name = "Onboarding — journal (last)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun OnboardingJournalPreview() {
    ProExpenseTheme {
        OnboardingScreenContent(onGetStarted = {}, onSkip = {})
    }
}
