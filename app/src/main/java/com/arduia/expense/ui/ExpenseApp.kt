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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.di.AppGraph
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.journal.JournalScreenContent
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.more.MoreHubScreenContent
import com.arduia.expense.ui.preview.MoreHubUiState
import com.arduia.expense.ui.navigation.AppNavState
import com.arduia.expense.ui.navigation.AppNavigator
import com.arduia.expense.ui.navigation.AppRouteHost
import com.arduia.expense.ui.navigation.AppRoutes
import com.arduia.expense.ui.navigation.appNavTransition
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.backwardScreenExit
import com.arduia.expense.ui.theme.forwardScreenEnter
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.util.toEventBudgetCardState
import com.arduia.expense.ui.util.toHomeScreenState
import com.arduia.expense.ui.util.toJournalDayGroup
import com.arduia.expense.ui.util.toJournalTransactionItem

@Composable
fun ExpenseApp(
    appGraph: AppGraph,
    modifier: Modifier = Modifier,
    onPinSetupRequested: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var backStack by rememberSaveable { mutableStateOf(listOf<String>()) }
    var quickLogOpen by rememberSaveable { mutableStateOf(false) }
    var editingRecordId by rememberSaveable { mutableStateOf<String?>(null) }
    var saveToastMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var saveErrorToast by rememberSaveable { mutableStateOf<String?>(null) }

    val homeState by appGraph.homeViewModel.uiState.collectAsState()
    val journalState by appGraph.journalViewModel.uiState.collectAsState()
    val eventBudgetState by appGraph.eventBudgetViewModel.uiState.collectAsState()

    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val savedToast = stringResource(R.string.toast_expense_saved)

    val addExpenseViewModel = remember(quickLogOpen, editingRecordId) {
        if (quickLogOpen) {
            appGraph.prewarmAddExpenseViewModel(
                editingRecordId = editingRecordId,
                onSaved = {
                    appGraph.refreshAfterDataChange()
                    quickLogOpen = false
                    editingRecordId = null
                    saveToastMessage = savedToast
                },
                onSaveFailed = { message ->
                    saveErrorToast = message
                },
            )
        } else {
            appGraph.clearAddExpenseViewModel()
            null
        }
    }

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

    val journalFilters = journalState.categoryFilters.ifEmpty { previewJournalFilters }
    val journalDayGroups = if (journalState.searchResults != null) {
        emptyList()
    } else {
        journalState.groupedEntries.map { it.toJournalDayGroup() }
    }
    val journalSearchResults = journalState.searchResults?.map { it.toJournalTransactionItem() }

    BackHandler(enabled = quickLogOpen) {
        quickLogOpen = false
        editingRecordId = null
    }

    BackHandler(enabled = navigator.canPop && !quickLogOpen) {
        navigator.pop()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ProExpenseTheme.colors.paper,
        ) { innerPadding ->
            AnimatedContent(
                targetState = navState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                transitionSpec = { appNavTransition(motion, reduceMotion) },
                label = "expenseAppNav",
            ) { state ->
                if (state.route != null) {
                    AppRouteHost(
                        route = state.route,
                        navigator = navigator,
                        appGraph = appGraph,
                        modifier = Modifier.fillMaxSize(),
                        onEditRecord = { recordId ->
                            editingRecordId = recordId
                            quickLogOpen = true
                        },
                    )
                } else {
                    when (state.tab) {
                        HomeNavTab.Home -> HomeScreenContent(
                            state = homeState.toHomeScreenState(),
                            showPinSetupBanner = homeState.showPinSetupBanner,
                            onPinBannerTap = onPinSetupRequested,
                            onPinBannerDismiss = appGraph.homeViewModel::onPinBannerDismissed,
                            onReportsClick = { navigator.push(AppRoutes.REPORTS) },
                            onDebtClick = { navigator.push(AppRoutes.DEBT_TRACKER) },
                            onSplitClick = { navigator.push(AppRoutes.SHARED_INPUT) },
                            onEventsClick = { navigator.switchTab(HomeNavTab.Budget) },
                            onActiveEventClick = { eventId ->
                                navigator.push(AppRoutes.eventDetail(eventId))
                            },
                        )
                        HomeNavTab.Budget -> BudgetScreenContent(
                            events = eventBudgetState.events.map { it.toEventBudgetCardState() },
                            onNewEvent = { navigator.push(AppRoutes.EVENT_CREATE) },
                            onEventClick = { eventId ->
                                navigator.push(AppRoutes.eventDetail(eventId))
                            },
                        )
                        HomeNavTab.Journal -> JournalScreenContent(
                            searchQuery = journalState.searchQuery,
                            onSearchChange = appGraph.journalViewModel::onSearchQueryChanged,
                            filters = journalFilters,
                            selectedFilter = journalState.selectedCategoryFilter,
                            onFilterSelected = appGraph.journalViewModel::onCategoryFilterSelected,
                            dayGroups = journalDayGroups.orEmpty(),
                            searchResults = journalSearchResults,
                            onTransactionClick = { id ->
                                navigator.push(AppRoutes.journalDetail(id))
                            },
                        )
                        HomeNavTab.More -> MoreHubScreenContent(
                            state = MoreHubUiState(
                                profileName = homeState.greetingName.ifBlank { "Guest" },
                                profileInitial = homeState.greetingName.firstOrNull()?.uppercaseChar()?.toString()
                                    ?: "G",
                                currencyCode = appGraph.homeCurrency(),
                            ),
                            onReportsClick = { navigator.push(AppRoutes.REPORTS) },
                            onDebtClick = { navigator.push(AppRoutes.DEBT_TRACKER) },
                            onSharedCostsClick = { navigator.push(AppRoutes.SHARED_INPUT) },
                            onCategoriesClick = { navigator.push(AppRoutes.CATEGORIES) },
                            onCurrencyClick = { navigator.push(AppRoutes.CURRENCY) },
                            onBudgetClick = { selectedTab = HomeNavTab.Budget },
                            onSecurityClick = { navigator.push(AppRoutes.SECURITY) },
                            onLanguageClick = {},
                            onExportClick = { navigator.push(AppRoutes.EXPORT) },
                            onImportClick = { navigator.push(AppRoutes.IMPORT) },
                            onClearClick = { navigator.push(AppRoutes.CLEAR) },
                            onAddClick = {
                                appGraph.prewarmAddExpenseViewModel(
                                    onSaved = {
                                        appGraph.refreshAfterDataChange()
                                        quickLogOpen = false
                                        saveToastMessage = savedToast
                                    },
                                    onSaveFailed = { message -> saveErrorToast = message },
                                )
                                quickLogOpen = true
                            },
                            onTabSelected = { tab ->
                                if (tab != HomeNavTab.Add) {
                                    selectedTab = tab
                                }
                            },
                        )
                        HomeNavTab.Add -> Unit
                    }
                }
            }
        }

        if (showBottomNav) {
            HomeBottomNav(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    if (tab != HomeNavTab.Add) {
                        selectedTab = tab
                    }
                },
                onAddClick = {
                    appGraph.prewarmAddExpenseViewModel(
                        onSaved = {
                            appGraph.refreshAfterDataChange()
                            quickLogOpen = false
                            saveToastMessage = savedToast
                        },
                        onSaveFailed = { message -> saveErrorToast = message },
                    )
                    quickLogOpen = true
                },
            )
        }

        AnimatedVisibility(
            visible = quickLogOpen,
            modifier = Modifier.fillMaxSize(),
            enter = motion.forwardScreenEnter(reduceMotion),
            exit = motion.backwardScreenExit(reduceMotion),
        ) {
            if (addExpenseViewModel != null) {
                QuickLogFlow(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = addExpenseViewModel,
                    onDismiss = {
                        quickLogOpen = false
                        editingRecordId = null
                    },
                    initialStep = if (editingRecordId != null) {
                        com.arduia.expense.ui.logging.QuickLogStep.Details
                    } else {
                        com.arduia.expense.ui.logging.QuickLogStep.Amount
                    },
                )
            }
        }

        ProToastHost(
            message = saveToastMessage ?: saveErrorToast,
            onDismiss = {
                saveToastMessage = null
                saveErrorToast = null
            },
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
    val context = LocalContext.current
    ProExpenseTheme {
        ExpenseApp(
            appGraph = AppGraph(
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                context,
            ),
        )
    }
}
