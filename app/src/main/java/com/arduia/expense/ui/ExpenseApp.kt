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
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.budget.previewEvents
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.journal.JournalScreenContent
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.more.MoreHubScreenContent
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
    var quickLogOpen by rememberSaveable { mutableStateOf(false) }
    val dimens = ProExpenseTheme.dimensions

    if (quickLogOpen) {
        QuickLogFlow(
            modifier = modifier.fillMaxSize(),
            onDismiss = { quickLogOpen = false },
            onSaved = { quickLogOpen = false },
        )
        return
    }

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
                onAddClick = { quickLogOpen = true },
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
                HomeNavTab.Home -> HomeScreenContent(
                    state = previewHomeCasual,
                    onReportsClick = {},
                    onDebtClick = {},
                    onSplitClick = {},
                    onEventsClick = {},
                )
                HomeNavTab.Budget -> BudgetScreenContent(
                    events = previewEvents,
                    onNewEvent = {},
                )
                HomeNavTab.Journal -> JournalScreenContent(
                    searchQuery = "",
                    onSearchChange = {},
                    filters = previewJournalFilters,
                    selectedFilter = "All",
                    onFilterSelected = {},
                    dayGroups = previewJournalList,
                    onTransactionClick = {},
                )
                HomeNavTab.More -> MoreHubScreenContent(
                    onReportsClick = {},
                    onCategoriesClick = {},
                    onCurrencyClick = {},
                    onExportClick = {},
                    onClearClick = {},
                )
                HomeNavTab.Add -> HomeScreenContent(
                    state = previewHomeCasual,
                    onReportsClick = {},
                    onDebtClick = {},
                    onSplitClick = {},
                    onEventsClick = {},
                )
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

@Preview(
    name = "ExpenseApp — Journal tab",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ExpenseAppJournalPreview() {
    ProExpenseTheme {
        ExpenseApp()
    }
}
