package com.arduia.expense.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProfileStepHeader
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

data class PreferencesSetupState(
    val languageTag: String = AppLanguage.DEFAULT.tag,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

@Composable
fun PreferencesSetupScreenContent(
    state: PreferencesSetupState,
    onLanguageSelected: (String) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    val languages = AppLanguage.entries
    val selectedLanguageIndex = languages.indexOf(AppLanguage.fromTag(state.languageTag)).coerceAtLeast(0)

    val themeOptions = listOf(
        ThemeMode.LIGHT to stringResource(R.string.preferences_theme_light),
        ThemeMode.DARK to stringResource(R.string.preferences_theme_dark),
        ThemeMode.SYSTEM to stringResource(R.string.preferences_theme_system),
    )
    val selectedThemeIndex = themeOptions.indexOfFirst { it.first == state.themeMode }.coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.screenPadding)
            .padding(bottom = dimens.space18),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimens.space28),
        ) {
            ProfileStepHeader(
                step = 1,
                totalSteps = 2,
                eyebrow = stringResource(R.string.preferences_setup_step, 1, 2),
                title = stringResource(R.string.preferences_title),
                subtitle = stringResource(R.string.preferences_subtitle),
                onSkip = onSkip,
                skipLabel = stringResource(R.string.skip),
                modifier = Modifier.padding(top = dimens.space8),
            )

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

        ProButton(
            text = stringResource(R.string.next),
            onClick = onContinue,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier.padding(top = dimens.space16),
        )
    }
}

@Preview(
    name = "Preferences setup",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PreferencesSetupPreview() {
    ProExpenseTheme {
        PreferencesSetupScreenContent(
            state = PreferencesSetupState(),
            onLanguageSelected = {},
            onThemeSelected = {},
            onContinue = {},
            onSkip = {},
        )
    }
}

@Preview(
    name = "Preferences setup — Myanmar + Light",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PreferencesSetupMyanmarPreview() {
    ProExpenseTheme {
        PreferencesSetupScreenContent(
            state = PreferencesSetupState(languageTag = AppLanguage.MYANMAR.tag, themeMode = ThemeMode.LIGHT),
            onLanguageSelected = {},
            onThemeSelected = {},
            onContinue = {},
            onSkip = {},
        )
    }
}
