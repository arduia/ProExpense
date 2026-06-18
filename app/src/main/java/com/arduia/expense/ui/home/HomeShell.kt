package com.arduia.expense.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class HomeTab {
    Home,
    Budget,
    Journal,
    More,
}

@Composable
fun HomeShell(
    homeContent: @Composable () -> Unit,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dims = ProExpenseTheme.dimensions

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = dims.homeFabBottomOffset),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            homeContent()
        }

        HomeBottomNav(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddExpense = onAddExpense,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeShellPreview() {
    ProExpenseTheme {
        HomeShell(
            homeContent = {
                HomeScreen(
                    state = HomeSampleData.empty,
                    onSeeAll = {},
                    onLogFirstExpense = {},
                    onQuickAccess = {},
                )
            },
            selectedTab = HomeTab.Home,
            onTabSelected = {},
            onAddExpense = {},
        )
    }
}
