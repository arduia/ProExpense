package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.preview.MoreHubUiState
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreHubScreen(
    state: MoreHubUiState,
    onFeatureClick: (String) -> Unit,
    onSettingClick: (String) -> Unit,
    onSettingToggle: (String, Boolean) -> Unit,
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space14, bottom = dimens.navShellBottomInset),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.more_eyebrow),
                    style = typography.eyebrow,
                    color = colors.muted,
                )
                Text(
                    text = stringResource(R.string.more_title),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
            }

            MoreProfileCard(profile = state.profile)

            MoreSection(label = stringResource(R.string.more_features)) {
                MoreGroupCard(items = state.features) { feature ->
                    MoreFeatureRow(feature = feature, onClick = { onFeatureClick(feature.id) })
                }
            }

            MoreSection(label = stringResource(R.string.more_settings)) {
                MoreGroupCard(items = state.settings) { setting ->
                    MoreSettingRow(
                        setting = setting,
                        onClick = { onSettingClick(setting.id) },
                        onToggle = { on -> onSettingToggle(setting.id, on) },
                    )
                }
            }
        }

        HomeBottomNav(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MoreSection(
    label: String,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space10)) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        content()
    }
}

@Preview(
    name = "More — hub",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreHubPreview() {
    ProExpenseTheme {
        MoreHubScreen(
            state = previewMoreHub,
            onFeatureClick = {},
            onSettingClick = {},
            onSettingToggle = { _, _ -> },
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}
