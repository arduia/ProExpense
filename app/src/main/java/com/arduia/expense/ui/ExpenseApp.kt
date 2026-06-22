package com.arduia.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arduia.expense.ExpenseApplication
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.AppRoute
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.HomeViewModel
import com.arduia.expense.ui.logging.ExpenseEntryViewModel
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.splash.SplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DURATION_MILLIS = 1800L

@Composable
fun ExpenseApp(
    features: FeatureUiRegistry = FeatureUiRegistry(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val container = remember(context) {
        (context.applicationContext as ExpenseApplication).container
    }
    val scope = rememberCoroutineScope()
    val homeViewModel = remember {
        HomeViewModel(
            financeRepository = container.financeRecordRepository,
            budgetRepository = container.budgetRepository,
            profileRepository = container.profileRepository,
            securityStateReader = container.securityStateReader,
            scope = scope,
        )
    }
    val entryViewModel = remember {
        ExpenseEntryViewModel(
            financeRepository = container.financeRecordRepository,
            profileRepository = container.profileRepository,
        )
    }

    val homeState by homeViewModel.state.collectAsState()

    var splashElapsed by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var showQuickLog by remember { mutableStateOf(false) }
    var entryStart by remember { mutableStateOf<LoggedExpenseHandoff?>(null) }
    var pinBannerDismissed by rememberSaveable { mutableStateOf(false) }
    var showSharedCosts by rememberSaveable { mutableStateOf(false) }
    var showDebt by rememberSaveable { mutableStateOf(false) }
    var showPinSetup by rememberSaveable { mutableStateOf(false) }
    var showReports by rememberSaveable { mutableStateOf(false) }

    val openNewEntry: () -> Unit = {
        scope.launch {
            entryStart = entryViewModel.newEntryStart()
            showQuickLog = true
        }
    }
    val openEditEntry: (String) -> Unit = { id ->
        scope.launch {
            entryViewModel.loadForEdit(id)?.let { handoff ->
                entryStart = handoff
                showQuickLog = true
            }
        }
    }
    val onTabSelected: (HomeNavTab) -> Unit = { tab ->
        if (tab == HomeNavTab.Home || tab == HomeNavTab.Budget ||
            tab == HomeNavTab.Journal || tab == HomeNavTab.More
        ) {
            selectedTab = tab
        }
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        splashElapsed = true
    }

    val showSplash = !splashElapsed || homeState.route == AppRoute.Loading

    Box(modifier.fillMaxSize()) {
        when {
            showSplash -> SplashScreen()

            homeState.route == AppRoute.Onboarding -> {
                features.onboarding.FirstLaunchFlow(
                    onComplete = { handoff ->
                        scope.launch {
                            homeViewModel.completeOnboarding(handoff.profileName, handoff.currencyCode)
                        }
                    },
                )
            }

            else -> {
                when (selectedTab) {
                    HomeNavTab.Budget -> features.eventBudget.EventsTab(
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                    )
                    HomeNavTab.Journal -> features.history.JournalTab(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                    )
                    HomeNavTab.More -> MoreFlow(
                        features = features,
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                        onDebtClick = { showDebt = true },
                        onSharedClick = { showSharedCosts = true },
                        onPinClick = { showPinSetup = true },
                    )
                    else -> HomeShell(
                        state = homeState.home,
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                        showPinSetupBanner = homeState.showPinBanner && !pinBannerDismissed,
                        onPinBannerTap = { showPinSetup = true },
                        onPinBannerDismiss = { pinBannerDismissed = true },
                        onReportsClick = { showReports = true },
                        onDebtClick = { showDebt = true },
                        onSplitClick = { showSharedCosts = true },
                        onEventsClick = { selectedTab = HomeNavTab.Budget },
                        onLogFirstExpense = openNewEntry,
                        onTransactionClick = openEditEntry,
                    )
                }
            }
        }

        if (showQuickLog) {
            features.logging.QuickLogFlow(
                onDismiss = {
                    showQuickLog = false
                    entryStart = null
                },
                onSaved = { handoff ->
                    scope.launch {
                        entryViewModel.submit(handoff)
                        homeViewModel.refresh()
                    }
                    showQuickLog = false
                    entryStart = null
                },
                start = entryStart,
            )
        }

        if (showSharedCosts) {
            features.sharedCost.SharedCostsOverlay(
                onDismiss = { showSharedCosts = false },
            )
        }

        if (showDebt) {
            features.debt.DebtOverlay(onDismiss = { showDebt = false })
        }

        if (showPinSetup) {
            features.auth.PinSetupFlow(
                onDismiss = { showPinSetup = false },
            )
        }

        if (showReports) {
            features.reports.ReportsFlow(
                onBack = { showReports = false },
                empty = homeState.home.isEmpty,
                onLogFirstExpense = {
                    showReports = false
                    openNewEntry()
                },
            )
        }
    }
}
