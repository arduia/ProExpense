package com.arduia.expense.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.budget.previewEvents
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.journal.JournalScreenContent
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.more.MoreHubScreenContent
import com.arduia.expense.ui.navigation.AppNavigator
import com.arduia.expense.ui.navigation.AppRouteHost
import com.arduia.expense.ui.navigation.AppRoutes
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.preview.previewJournalList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ExpenseApp(
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var backStack by rememberSaveable { mutableStateOf(listOf<String>()) }
    var quickLogOpen by rememberSaveable { mutableStateOf(false) }
    val dimens = ProExpenseTheme.dimensions

    val navigator = remember(backStack, selectedTab) {
        AppNavigator(
            backStack = backStack,
            onBackStackChange = { backStack = it },
            onTabChange = { selectedTab = it },
        )
    }

    if (quickLogOpen) {
        QuickLogFlow(
            modifier = modifier.fillMaxSize(),
            onDismiss = { quickLogOpen = false },
            onSaved = { quickLogOpen = false },
        )
        return
    }

    val currentRoute = backStack.lastOrNull()
    val showBottomNav = currentRoute == null

    BackHandler(enabled = navigator.canPop) {
        navigator.pop()
    }

    Scaffold(
        modifier = modifier,
        containerColor = ProExpenseTheme.colors.paper,
        bottomBar = {
            if (showBottomNav) {
                HomeBottomNav(
                    modifier = Modifier.navigationBarsPadding(),
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab != HomeNavTab.Add) {
                            selectedTab = tab
                        }
                    },
                    onAddClick = { quickLogOpen = true },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (showBottomNav) {
                        Modifier.padding(bottom = dimens.navShellBottomInset)
                    } else {
                        Modifier
                    },
                ),
        ) {
            if (currentRoute != null) {
                AppRouteHost(
                    route = currentRoute,
                    navigator = navigator,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                when (selectedTab) {
                    HomeNavTab.Home -> HomeScreenContent(
                        state = previewHomeCasual,
                        onReportsClick = { navigator.push(AppRoutes.REPORTS) },
                        onDebtClick = { navigator.push(AppRoutes.DEBT_TRACKER) },
                        onSplitClick = { navigator.push(AppRoutes.SHARED_INPUT) },
                        onEventsClick = { navigator.switchTab(HomeNavTab.Budget) },
                    )
                    HomeNavTab.Budget -> BudgetScreenContent(
                        events = previewEvents,
                        onNewEvent = { navigator.push(AppRoutes.EVENT_CREATE) },
                        onEventClick = { title -> navigator.push(AppRoutes.eventDetail(title)) },
                    )
                    HomeNavTab.Journal -> JournalScreenContent(
                        searchQuery = "",
                        onSearchChange = {},
                        filters = previewJournalFilters,
                        selectedFilter = "All",
                        onFilterSelected = {},
                        dayGroups = previewJournalList,
                        onTransactionClick = { navigator.push(AppRoutes.JOURNAL_DETAIL) },
                    )
                    HomeNavTab.More -> MoreHubScreenContent(
                        onReportsClick = { navigator.push(AppRoutes.REPORTS) },
                        onCategoriesClick = { navigator.push(AppRoutes.CATEGORIES) },
                        onCurrencyClick = { navigator.push(AppRoutes.CURRENCY) },
                        onExportClick = { navigator.push(AppRoutes.EXPORT) },
                        onClearClick = { navigator.push(AppRoutes.CLEAR) },
                    )
                    HomeNavTab.Add -> Unit
                }
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
