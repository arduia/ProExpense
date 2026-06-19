package com.arduia.expense.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.budget.previewEvents
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.journal.JournalScreenContent
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.more.MoreHubScreenContent
import com.arduia.expense.ui.navigation.AppNavState
import com.arduia.expense.ui.navigation.AppNavigator
import com.arduia.expense.ui.navigation.AppRouteHost
import com.arduia.expense.ui.navigation.AppRoutes
import com.arduia.expense.ui.navigation.appNavTransition
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.preview.previewJournalList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.backwardScreenExit
import com.arduia.expense.ui.theme.forwardScreenEnter
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.util.filterJournalGroups

@Composable
fun ExpenseApp(
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var backStack by rememberSaveable { mutableStateOf(listOf<String>()) }
    var quickLogOpen by rememberSaveable { mutableStateOf(false) }
    var journalSearch by rememberSaveable { mutableStateOf("") }
    var journalFilter by rememberSaveable { mutableStateOf("All") }
    var saveToastMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val filteredJournal = remember(journalSearch, journalFilter) {
        filterJournalGroups(previewJournalList, journalSearch, journalFilter)
    }
    val savedToast = stringResource(R.string.toast_expense_saved)

    val navigator = remember(backStack, selectedTab) {
        AppNavigator(
            backStack = backStack,
            onBackStackChange = { backStack = it },
            onTabChange = { selectedTab = it },
        )
    }

    val currentRoute = backStack.lastOrNull()
    val showBottomNav = currentRoute == null && !quickLogOpen
    val navState = AppNavState(
        route = currentRoute,
        tab = selectedTab,
        stackSize = backStack.size,
    )

    BackHandler(enabled = quickLogOpen) {
        quickLogOpen = false
    }

    BackHandler(enabled = navigator.canPop && !quickLogOpen) {
        navigator.pop()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
            AnimatedContent(
                targetState = navState,
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
                transitionSpec = { appNavTransition(motion, reduceMotion) },
                label = "expenseAppNav",
            ) { state ->
                if (state.route != null) {
                    AppRouteHost(
                        route = state.route,
                        navigator = navigator,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    when (state.tab) {
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
                            searchQuery = journalSearch,
                            onSearchChange = { journalSearch = it },
                            filters = previewJournalFilters,
                            selectedFilter = journalFilter,
                            onFilterSelected = { journalFilter = it },
                            dayGroups = filteredJournal,
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

        AnimatedVisibility(
            visible = quickLogOpen,
            modifier = Modifier.fillMaxSize(),
            enter = motion.forwardScreenEnter(reduceMotion),
            exit = motion.backwardScreenExit(reduceMotion),
        ) {
            QuickLogFlow(
                modifier = Modifier.fillMaxSize(),
                onDismiss = { quickLogOpen = false },
                onSaved = {
                    quickLogOpen = false
                    saveToastMessage = savedToast
                },
            )
        }

        ProToastHost(
            message = saveToastMessage,
            onDismiss = { saveToastMessage = null },
        )
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
