package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProGradientHeader
import com.arduia.expense.ui.preview.MoreHubUiState
import com.arduia.expense.ui.preview.MoreProfileUi
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

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProGradientHeader {
                MoreHeaderContent(profile = state.profile)
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(y = -MoreSheetOverlap)
                        .clip(ProExpenseTheme.shapes.sheet)
                        .background(colors.paper)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dimens.screenPadding)
                        .padding(top = dimens.space16, bottom = dimens.navShellBottomInset),
                verticalArrangement = Arrangement.spacedBy(dimens.space16),
            ) {
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
        }

        HomeBottomNav(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private val MoreSheetOverlap = 14.dp

@Composable
private fun MoreHeaderContent(
    profile: MoreProfileUi,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            Text(
                text = stringResource(R.string.more_eyebrow),
                style = typography.eyebrow,
                color = Color.White.copy(alpha = 0.7f),
            )
            Text(
                text = stringResource(R.string.more_title),
                style = typography.profileScreenTitle,
                color = Color.White,
            )
        }
        MoreProfileCard(
            profile = profile,
            glass = true,
            modifier = Modifier.padding(top = dimens.space18),
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

@Preview(
    name = "More — hub (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreHubDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
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
