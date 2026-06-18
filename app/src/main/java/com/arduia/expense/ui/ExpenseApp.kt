package com.arduia.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.home.TabPlaceholderContent
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ExpenseApp(
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    val dimens = ProExpenseTheme.dimensions

    Scaffold(
        modifier = modifier,
        containerColor = ProExpenseTheme.colors.paper,
        bottomBar = {
            HomeBottomNav(
                modifier = Modifier.navigationBarsPadding(),
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    if (tab != HomeNavTab.Add) {
                        selectedTab = tab
                    }
                },
                onAddClick = { /* Quick Log flow — wired in a follow-up */ },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = dimens.navShellBottomInset),
        ) {
            when (selectedTab) {
                HomeNavTab.Home -> HomeScreenContent()
                HomeNavTab.Budget -> TabPlaceholderContent(title = "Budget")
                HomeNavTab.Journal -> TabPlaceholderContent(title = "Journal")
                HomeNavTab.More -> TabPlaceholderContent(title = "More")
                HomeNavTab.Add -> HomeScreenContent()
            }
        }
    }
}

@Preview(
    name = "ExpenseApp — Home tab",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ExpenseAppHomePreview() {
    ProExpenseTheme {
        ExpenseApp()
    }
}
