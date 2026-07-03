package com.arduia.expense.feature.onboarding.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.design.OnboardingIllustration
import com.arduia.expense.ui.design.OnboardingPageIndicator
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.SegmentedToggle
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
    languageTag: String = AppLanguage.DEFAULT.tag,
    themeMode: ThemeMode = ThemeMode.DARK,
    onLanguageSelected: (String) -> Unit = {},
    onThemeSelected: (ThemeMode) -> Unit = {},
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
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space16),
        ) {
            if (pagerState.currentPage < lastPage) {
                ProTextAction(
                    text = stringResource(R.string.skip),
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    style = typography.navAction,
                    color = colors.onSurfaceMuted,
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
                    style = typography.onboardingSlideTitle,
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
                // Set on the very first screen a new user sees — applies immediately, no restart,
                // and both remain changeable later in Settings (US-MORE-3).
                if (page == 0) {
                    OnboardingPreferencesPicker(
                        languageTag = languageTag,
                        themeMode = themeMode,
                        onLanguageSelected = onLanguageSelected,
                        onThemeSelected = onThemeSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.screenPadding, vertical = dimens.space20),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.onboardingNavBottomMargin),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pagerState.currentPage > 0) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProTextAction(
                        text = stringResource(R.string.back),
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        },
                        style = typography.navAction,
                        color = colors.onSurfaceMuted,
                        leading = {
                            ProIcon(
                                glyph = ProIconGlyph.Back,
                                contentDescription = null,
                                tint = colors.onSurfaceMuted,
                                size = dimens.iconInline,
                            )
                        },
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
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProTextAction(
                        text = stringResource(R.string.next),
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        },
                        style = typography.navAction.copy(
                            fontWeight = typography.bodySemiBold.fontWeight,
                        ),
                        color = colors.onSurface,
                        trailing = {
                            ProIcon(
                                glyph = ProIconGlyph.ChevronRight,
                                contentDescription = null,
                                tint = colors.onSurface,
                                size = dimens.iconInline,
                            )
                        },
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
            modifier = Modifier
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
        )
    }
}

@Composable
private fun OnboardingPreferencesPicker(
    languageTag: String,
    themeMode: ThemeMode,
    onLanguageSelected: (String) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    val languages = AppLanguage.entries
    val selectedLanguageIndex = languages.indexOf(AppLanguage.fromTag(languageTag)).coerceAtLeast(0)

    val themeOptions = listOf(
        ThemeMode.LIGHT to stringResource(R.string.preferences_theme_light),
        ThemeMode.DARK to stringResource(R.string.preferences_theme_dark),
        ThemeMode.SYSTEM to stringResource(R.string.preferences_theme_system),
    )
    val selectedThemeIndex = themeOptions.indexOfFirst { it.first == themeMode }.coerceAtLeast(0)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            Text(
                text = stringResource(R.string.preferences_language_label),
                style = typography.eyebrow,
                color = colors.primary,
            )
            SegmentedToggle(
                options = languages.map { it.displayName },
                selectedIndex = selectedLanguageIndex,
                onSelected = { index -> onLanguageSelected(languages[index].tag) },
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            Text(
                text = stringResource(R.string.preferences_theme_label),
                style = typography.eyebrow,
                color = colors.primary,
            )
            SegmentedToggle(
                options = themeOptions.map { it.second },
                selectedIndex = selectedThemeIndex,
                onSelected = { index -> onThemeSelected(themeOptions[index].first) },
            )
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
        OnboardingScreenContent(onGetStarted = {}, onSkip = {})
    }
}

@Preview(
    name = "Onboarding — welcome, Myanmar + Light",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun OnboardingWelcomeMyanmarPreview() {
    ProExpenseTheme {
        OnboardingScreenContent(
            onGetStarted = {},
            onSkip = {},
            languageTag = AppLanguage.MYANMAR.tag,
            themeMode = ThemeMode.LIGHT,
        )
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
