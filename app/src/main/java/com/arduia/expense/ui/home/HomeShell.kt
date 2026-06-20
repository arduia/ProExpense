package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.preview.HomeUiState
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun HomeShell(
    state: HomeUiState,
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPinSetupBanner: Boolean = false,
    onPinBannerTap: () -> Unit = {},
    onPinBannerDismiss: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onDebtClick: () -> Unit = {},
    onSplitClick: () -> Unit = {},
    onEventsClick: () -> Unit = {},
    onActiveEventClick: (String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .navigationBarsPadding(),
    ) {
        HomeScreenContent(
            state = state,
            onReportsClick = onReportsClick,
            onDebtClick = onDebtClick,
            onSplitClick = onSplitClick,
            onEventsClick = onEventsClick,
            showPinSetupBanner = showPinSetupBanner,
            onPinBannerTap = onPinBannerTap,
            onPinBannerDismiss = onPinBannerDismiss,
            onActiveEventClick = onActiveEventClick,
        )

        if (selectedTab == HomeNavTab.Home) {
            HomeBottomNav(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onAddClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(
    name = "Home shell — casual",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeShellCasualPreview() {
    ProExpenseTheme {
        HomeShell(
            state = previewHomeCasual,
            selectedTab = HomeNavTab.Home,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}
