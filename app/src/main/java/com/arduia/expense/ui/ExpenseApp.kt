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
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.ui.categories.CategoriesViewModel
import com.arduia.expense.ui.currency.CurrencyViewModel
import com.arduia.expense.ui.debt.DebtViewModel
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.events.EventsViewModel
import com.arduia.expense.ui.home.AppRoute
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.HomeViewModel
import com.arduia.expense.ui.journal.JournalViewModel
import com.arduia.expense.ui.logging.ExpenseEntryViewModel
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.reports.ReportsViewModel
import com.arduia.expense.ui.sharedcost.SharedCostViewModel
import com.arduia.expense.ui.splash.SplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

private const val SPLASH_DURATION_MILLIS = 1800L

@Composable
fun ExpenseApp(
    features: FeatureUiRegistry = FeatureUiRegistry(),
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val homeViewModel = koinInject<HomeViewModel> { parametersOf(scope) }
    val journalViewModel = koinInject<JournalViewModel> { parametersOf(scope) }
    val reportsViewModel = koinInject<ReportsViewModel> { parametersOf(scope) }
    val currencyViewModel = koinInject<CurrencyViewModel> { parametersOf(scope) }
    val eventsViewModel = koinInject<EventsViewModel> { parametersOf(scope) }
    val debtViewModel = koinInject<DebtViewModel> { parametersOf(scope) }
    val categoriesViewModel = koinInject<CategoriesViewModel> { parametersOf(scope) }
    val sharedCostViewModel = koinInject<SharedCostViewModel> { parametersOf(scope) }
    val entryViewModel = koinInject<ExpenseEntryViewModel>()

    val homeState by homeViewModel.state.collectAsState()
    val journalState by journalViewModel.state.collectAsState()
    val reportsState by reportsViewModel.state.collectAsState()
    val eventsState by eventsViewModel.state.collectAsState()
    val debtState by debtViewModel.state.collectAsState()
    val categoriesState by categoriesViewModel.state.collectAsState()
    val sharedCostState by sharedCostViewModel.state.collectAsState()
    val selectedCurrency by currencyViewModel.selectedCode.collectAsState()

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
                        events = eventsState.cards,
                        detailFor = { id -> eventsState.details[id] },
                        onCreateEvent = { name, budgetRaw ->
                            eventsViewModel.create(name, budgetRaw)
                        },
                    )
                    HomeNavTab.Journal -> features.history.JournalTab(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                        days = journalState.days,
                        detailFor = { id -> journalState.details[id] },
                        onEditRecord = openEditEntry,
                        onDeleteRecord = { id ->
                            journalViewModel.delete(id)
                            homeViewModel.refresh()
                            reportsViewModel.refresh()
                            eventsViewModel.refresh()
                        },
                    )
                    HomeNavTab.More -> MoreFlow(
                        features = features,
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = openNewEntry,
                        onDebtClick = { showDebt = true },
                        onSharedClick = { showSharedCosts = true },
                        onPinClick = { showPinSetup = true },
                        selectedCurrency = selectedCurrency,
                        onCurrencySelect = { code ->
                            currencyViewModel.select(code)
                            homeViewModel.refresh()
                            journalViewModel.refresh()
                            reportsViewModel.refresh()
                        },
                        reportsPeriods = reportsState.periods,
                        categoriesState = categoriesState.list,
                        onCategoryCreate = { iconId, label ->
                            categoriesViewModel.create(iconId, label)
                        },
                        onCategoryUpdate = { oldId, iconId, label ->
                            categoriesViewModel.update(oldId, iconId, label)
                        },
                        onCategoryDelete = { id -> categoriesViewModel.delete(id) },
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
                        journalViewModel.refresh()
                        reportsViewModel.refresh()
                        eventsViewModel.refresh()
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
                history = sharedCostState.history,
                onCreateSplit = { title, totalCents, names, customShareCents ->
                    sharedCostViewModel.create(title, totalCents, names, customShareCents)
                },
            )
        }

        if (showDebt) {
            features.debt.DebtOverlay(
                onDismiss = { showDebt = false },
                lentList = debtState.lent,
                oweList = debtState.owe,
                detailFor = { id -> debtState.details[id] },
                onCreateRecord = { side, person, amountRaw ->
                    debtViewModel.create(side, person, amountRaw)
                },
                onMarkSettled = { id -> debtViewModel.markSettled(id) },
                onDeleteRecord = { id -> debtViewModel.delete(id) },
            )
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
                periods = reportsState.periods,
            )
        }
    }
}
