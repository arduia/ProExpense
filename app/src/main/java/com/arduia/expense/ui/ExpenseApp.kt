package com.arduia.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.arduia.expense.R
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.shouldRelockOnBackground
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.ui.ExpenseDraftPrefs
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.onboarding.CompleteOnboardingUseCase
import com.arduia.expense.feature.onboarding.GetOnboardingStatusUseCase
import com.arduia.expense.shell.home.HomeViewModel
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.QuickAccessPickerSheetContent
import com.arduia.expense.ui.home.QuickAccessPrefs
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.splash.SplashScreen
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject

private const val SPLASH_DURATION_MILLIS = 1800L

@Composable
fun ExpenseApp(
    features: FeatureUiRegistry = FeatureUiRegistry(),
    modifier: Modifier = Modifier,
    getOnboardingStatus: GetOnboardingStatusUseCase = koinInject(),
    completeOnboarding: CompleteOnboardingUseCase = koinInject(),
    pinAuthRepository: PinAuthRepository = koinInject(),
    onThemeModeChanged: (com.arduia.expense.data.ThemeMode) -> Unit = {},
    onLanguageChanged: () -> Unit = {},
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var onboardingComplete by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var pinConfigured by remember { mutableStateOf<Boolean?>(null) }
    // Deliberately `remember`, not `rememberSaveable` — on API < 29, onSaveInstanceState can run
    // before the ON_STOP observer below resets this to false, so a saved-instance-state bundle
    // could restore `unlocked = true` after process death and skip the PIN gate entirely. Losing
    // this across process death is the correct behavior anyway (US-AUTH-4: always re-prompt).
    var unlocked by remember { mutableStateOf(false) }
    // Opt-in (Settings > "Stay unlocked while switching apps", default off) — loaded alongside
    // pinConfigured below and kept live by MoreFlow's onStayUnlockedInBackgroundChanged callback.
    var stayUnlockedInBackground by remember { mutableStateOf(false) }
    var showQuickLog by rememberSaveable { mutableStateOf(false) }
    var showSharedCosts by rememberSaveable { mutableStateOf(false) }
    var showDebt by rememberSaveable { mutableStateOf(false) }
    // Set when a Split/Debt-kind Recents/Journal row is tapped, so the overlay opens straight to
    // that record's own detail screen instead of its default list/history step.
    var sharedCostInitialViewingId by rememberSaveable { mutableStateOf<String?>(null) }
    var debtInitialSelectedRecordId by rememberSaveable { mutableStateOf<String?>(null) }
    // Which tab a deep-linked Debt/Split open came from (Journal vs Home Recents) — drives the
    // one-tap back-to-origin label/action on Debt Detail / Split Summary. Null for non-deep-link
    // opens (More tab, quick access), which keep their existing list/history back behavior.
    var debtDeepLinkOrigin by rememberSaveable { mutableStateOf<HomeNavTab?>(null) }
    var sharedCostDeepLinkOrigin by rememberSaveable { mutableStateOf<HomeNavTab?>(null) }
    // Set by Home's Split quick-access tile so the Split flow opens straight on the New Split
    // amount input instead of its History list.
    var sharedCostStartAtNewSplit by rememberSaveable { mutableStateOf(false) }
    var showPinSetup by rememberSaveable { mutableStateOf(false) }
    var showReports by rememberSaveable { mutableStateOf(false) }
    var showCategoryManager by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var homeSelectedRecordId by rememberSaveable { mutableStateOf<String?>(null) }
    var homeSelectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    var quickLogLinkedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingDraftState by remember { mutableStateOf<ExpenseEntryState?>(null) }
    var editRecordId by rememberSaveable { mutableStateOf<String?>(null) }
    var showQuickAccessPicker by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var quickAccessVisible by remember { mutableStateOf(QuickAccessPrefs.load(context)) }
    val coroutineScope = rememberCoroutineScope()
    // Hoisted above tab switching — Journal's loaded pages and filters live here so reselecting
    // the tab resumes instantly instead of reloading (the shell ViewModel does the same for
    // spentByEvent, which used to be hoisted alongside this).
    val journalTabState = features.history.rememberJournalTabState()

    // Every cross-feature collection the shell renders comes from one KMP ViewModel (`:shell`),
    // so Compose and SwiftUI show the same computed state instead of each mapping records to rows.
    val homeViewModel = rememberHomeViewModel()
    val shell by homeViewModel.uiState.collectAsState()
    val records = shell.records
    val categories = shell.categories
    val events = shell.events
    val eventsLoading = shell.eventsLoading
    val debts = shell.debts
    val sharedCosts = shell.sharedCosts
    val spentByEvent = shell.spentByEvent
    val categoryNames = shell.linkNames.categoryNames
    val defaultCategoryChips = shell.defaultCategoryChips
    val customCategoryChips = shell.customCategoryChips
    val categoryTypeById = shell.categoryTypeById
    val homeCurrencyCode = shell.homeCurrencyCode
    val homeSymbol = shell.homeCurrencySymbol
    val defaultCategoryId = shell.defaultCategoryId
    val userName = shell.userName
    val homeState = shell.home

    LaunchedEffect(Unit) {
        val status = getOnboardingStatus()
        onboardingComplete = status.isComplete
        if (userName.isBlank()) homeViewModel.onUserNameChanged(status.displayName)
    }

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete == true) {
            ExpenseDraftPrefs.load(context)?.let { draft ->
                pendingDraftState = draft
                showQuickLog = true
            }
        }
    }

    // Re-reads budget / default category / home currency / profile name after onboarding lands —
    // the ViewModel already did this once on construction.
    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete == true) homeViewModel.loadSettings()
    }

    val expenseSavedMessage = stringResource(R.string.toast_expense_saved_home)
    val pinSetupSuccessMessage = stringResource(com.arduia.expense.feature.auth.R.string.pin_setup_success)
    var actionToastMessage by remember { mutableStateOf<String?>(null) }

    val onExpenseSaved: (LoggedExpenseHandoff) -> Unit = { _ ->
        showQuickLog = false
        quickLogLinkedEventId = null
        pendingDraftState = null
        // Shown here (post-dismiss) rather than inside QuickLogFlow itself — that composable
        // unmounts as soon as the save completes, before its own toast could ever render.
        actionToastMessage = expenseSavedMessage
    }

    val onTabSelected: (HomeNavTab) -> Unit = { tab ->
        if (tab == HomeNavTab.Home ||
            tab == HomeNavTab.Budget ||
            tab == HomeNavTab.Journal ||
            tab == HomeNavTab.More
        ) {
            // When the user manually navigates to Journal, clear the home-originated row
            // selection so the journal doesn't pre-jump to a record from a previous session.
            // Do NOT clear it when we programmatically switch to Journal from onRowClick — that
            // selection is what drives initialSelectedRowId in JournalFlow and is needed for the
            // back-to-home navigation in JournalDetailScreen.onBack.
            if (tab == HomeNavTab.Journal && selectedTab != HomeNavTab.Journal) {
                homeSelectedRecordId = null
            }
            // Same rationale as above, for the Active Event card's tap-through to Budget.
            if (tab == HomeNavTab.Budget && selectedTab != HomeNavTab.Budget) {
                homeSelectedEventId = null
            }
            selectedTab = tab
        }
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        showSplash = false
    }

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete == true) {
            when (val result = pinAuthRepository.isPinConfigured()) {
                is Result.Success -> pinConfigured = result.data
                is Result.Error -> pinConfigured = false
            }
            when (val result = pinAuthRepository.isStayUnlockedInBackgroundEnabled()) {
                is Result.Success -> stayUnlockedInBackground = result.data
                is Result.Error -> Unit
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP && shouldRelockOnBackground(stayUnlockedInBackground)) {
                    unlocked = false
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier.fillMaxSize()) {
        if (showSplash || onboardingComplete == null) {
            SplashScreen()
        } else {
            if (onboardingComplete == true) {
                if (pendingDraftState != null) {
                    // A restorable draft is never gated behind PIN (US-LOG-7) — resolve it first.
                    features.logging.QuickLogFlow(
                        onDismiss = {
                            showQuickLog = false
                            pendingDraftState = null
                        },
                        onSaved = onExpenseSaved,
                        currencyCode = homeCurrencyCode,
                        defaultCategoryId = defaultCategoryId,
                        initialDraftState = pendingDraftState,
                        homeCurrencySymbol = homeSymbol,
                        defaultCategories = defaultCategoryChips,
                        customCategories = customCategoryChips,
                        categoryTypes = categoryTypeById,
                        onAddCategory = { showCategoryManager = true },
                    )
                } else if (pinConfigured == true && !unlocked) {
                    features.auth.PinLockFlow(
                        onUnlocked = { unlocked = true },
                        modifier = Modifier,
                    )
                } else if (pinConfigured != null) {
                    when (selectedTab) {
                        HomeNavTab.Budget ->
                            features.eventBudget.EventsTab(
                                events = events,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                records = records,
                                spentByEvent = spentByEvent,
                                categoryNames = categoryNames,
                                initialSelectedEventId = homeSelectedEventId,
                                onAddTaggedExpense = { eventId ->
                                    quickLogLinkedEventId = eventId
                                    showQuickLog = true
                                },
                                onExpenseClick = { recordId ->
                                    homeSelectedRecordId = recordId
                                    selectedTab = HomeNavTab.Journal
                                },
                                homeCurrencySymbol = homeSymbol,
                                isLoading = eventsLoading,
                            )
                        HomeNavTab.Journal ->
                            features.history.JournalTab(
                                state = journalTabState,
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                initialSelectedRowId = homeSelectedRecordId,
                                onEditRecord = { editRecordId = it },
                                categories = categories,
                                events = events,
                                debts = debts,
                                sharedCosts = sharedCosts,
                                homeCurrencySymbol = homeSymbol,
                                onOpenLinkedEvent = { eventId ->
                                    homeSelectedEventId = eventId
                                    selectedTab = HomeNavTab.Budget
                                },
                                onOpenSplit = { splitId ->
                                    sharedCostInitialViewingId = splitId
                                    sharedCostDeepLinkOrigin = HomeNavTab.Journal
                                    showSharedCosts = true
                                },
                                onOpenDebt = { debtId ->
                                    debtInitialSelectedRecordId = debtId
                                    debtDeepLinkOrigin = HomeNavTab.Journal
                                    showDebt = true
                                },
                            )
                        HomeNavTab.More ->
                            MoreFlow(
                                features = features,
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                onDebtClick = { showDebt = true },
                                onSharedClick = { showSharedCosts = true },
                                onPinClick = { showPinSetup = true },
                                pinConfigured = pinConfigured,
                                initialDisplayName = userName.ifBlank { null },
                                onCurrencyChanged = { homeViewModel.onHomeCurrencyChanged(it.code) },
                                onBudgetChanged = { homeViewModel.onMonthlyBudgetChanged(it) },
                                onDefaultCategoryChanged = { homeViewModel.onDefaultCategoryChanged(it) },
                                onThemeModeChanged = onThemeModeChanged,
                                onLanguageChanged = onLanguageChanged,
                                onLockNowClick = { unlocked = false },
                                onStayUnlockedInBackgroundChanged = { stayUnlockedInBackground = it },
                            )
                        else ->
                            HomeShell(
                                state = homeState,
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                onReportsClick = { showReports = true },
                                onDebtClick = { showDebt = true },
                                onSplitClick = {
                                    sharedCostStartAtNewSplit = true
                                    showSharedCosts = true
                                },
                                onEventsClick = { onTabSelected(HomeNavTab.Budget) },
                                onLogFirstExpense = { showQuickLog = true },
                                onSeeAll = { onTabSelected(HomeNavTab.Journal) },
                                onCustomizeQuickAccess = { showQuickAccessPicker = true },
                                visibleTiles = quickAccessVisible,
                                onRowClick = { row ->
                                    when (row.rowKind) {
                                        ProRowKind.SPLIT ->
                                            row.linkedId?.let {
                                                sharedCostInitialViewingId = it
                                                sharedCostDeepLinkOrigin = HomeNavTab.Home
                                                showSharedCosts = true
                                            }
                                        ProRowKind.DEBT_LENT, ProRowKind.DEBT_OWED ->
                                            row.linkedId?.let {
                                                debtInitialSelectedRecordId = it
                                                debtDeepLinkOrigin = HomeNavTab.Home
                                                showDebt = true
                                            }
                                        ProRowKind.EXPENSE, ProRowKind.INCOME -> {
                                            homeSelectedRecordId = row.id
                                            selectedTab = HomeNavTab.Journal
                                        }
                                    }
                                },
                                onActiveEventClick = { eventId ->
                                    homeSelectedEventId = eventId
                                    selectedTab = HomeNavTab.Budget
                                },
                            )
                    }
                } else {
                    // pinConfigured loads asynchronously right after onboardingComplete flips
                    // true (LaunchedEffect below) — without this branch the Box above renders
                    // nothing for that gap frame, exposing the raw blue windowBackground.
                    SplashScreen()
                }
            } else {
                features.onboarding.FirstLaunchFlow(
                    onComplete = { handoff ->
                        homeViewModel.onUserNameChanged(handoff.profileName)
                        homeViewModel.onHomeCurrencyChanged(handoff.currencyCode)
                        coroutineScope.launch {
                            withContext(NonCancellable) {
                                completeOnboarding(handoff.profileName, handoff.currencyCode)
                                // Resolve pinConfigured before flipping onboardingComplete so both
                                // land in the same recomposition — otherwise the Home branch below
                                // briefly falls through to its Splash fallback while pinConfigured
                                // is still null, flashing the splash screen a second time.
                                pinConfigured =
                                    when (val result = pinAuthRepository.isPinConfigured()) {
                                        is Result.Success -> result.data
                                        is Result.Error -> false
                                    }
                            }
                            onboardingComplete = true
                        }
                    },
                )
            }

            if (showQuickLog && pendingDraftState == null) {
                features.logging.QuickLogFlow(
                    onDismiss = {
                        showQuickLog = false
                        quickLogLinkedEventId = null
                    },
                    onSaved = onExpenseSaved,
                    currencyCode = homeCurrencyCode,
                    defaultCategoryId = defaultCategoryId,
                    initialLinkedEventId = quickLogLinkedEventId,
                    homeCurrencySymbol = homeSymbol,
                    defaultCategories = defaultCategoryChips,
                    customCategories = customCategoryChips,
                    categoryTypes = categoryTypeById,
                    onAddCategory = { showCategoryManager = true },
                )
            }

            editRecordId?.let { recordId ->
                features.logging.EditExpenseFlow(
                    recordId = recordId,
                    onDismiss = { editRecordId = null },
                    onSaved = { editRecordId = null },
                    homeCurrencySymbol = homeSymbol,
                    defaultCategories = defaultCategoryChips,
                    customCategories = customCategoryChips,
                    categoryTypes = categoryTypeById,
                    onAddCategory = { showCategoryManager = true },
                )
            }

            if (showCategoryManager) {
                features.categories.CategoryListFlow(onBack = { showCategoryManager = false })
            }

            if (showSharedCosts) {
                features.sharedCost.SharedCostsOverlay(
                    onDismiss = {
                        showSharedCosts = false
                        sharedCostInitialViewingId = null
                        sharedCostDeepLinkOrigin = null
                        sharedCostStartAtNewSplit = false
                    },
                    homeCurrencySymbol = homeSymbol,
                    homeCurrencyCode = homeCurrencyCode,
                    initialViewingId = sharedCostInitialViewingId,
                    deepLinkBackLabel =
                        when (sharedCostDeepLinkOrigin) {
                            HomeNavTab.Journal ->
                                stringResource(com.arduia.expense.feature.sharedcost.R.string.shared_back_journal)
                            HomeNavTab.Home ->
                                stringResource(com.arduia.expense.feature.sharedcost.R.string.shared_back_home)
                            else -> null
                        },
                    onDeepLinkBack =
                        sharedCostDeepLinkOrigin?.let {
                            {
                                showSharedCosts = false
                                sharedCostInitialViewingId = null
                                sharedCostDeepLinkOrigin = null
                            }
                        },
                    startAtNewSplit = sharedCostStartAtNewSplit,
                    preloadedSharedCosts = sharedCosts,
                )
            }

            if (showDebt) {
                features.debt.DebtOverlay(
                    onDismiss = {
                        showDebt = false
                        debtInitialSelectedRecordId = null
                        debtDeepLinkOrigin = null
                    },
                    homeCurrencySymbol = homeSymbol,
                    initialSelectedRecordId = debtInitialSelectedRecordId,
                    deepLinkBackLabel =
                        when (debtDeepLinkOrigin) {
                            HomeNavTab.Journal ->
                                stringResource(com.arduia.expense.feature.debt.R.string.debt_back_journal)
                            HomeNavTab.Home ->
                                stringResource(com.arduia.expense.feature.debt.R.string.debt_back_home)
                            else -> null
                        },
                    onDeepLinkBack =
                        debtDeepLinkOrigin?.let {
                            {
                                showDebt = false
                                debtInitialSelectedRecordId = null
                                debtDeepLinkOrigin = null
                            }
                        },
                )
            }

            if (showPinSetup) {
                features.auth.PinSetupFlow(
                    onDismiss = {
                        showPinSetup = false
                        unlocked = true
                    },
                    onSaved = { actionToastMessage = pinSetupSuccessMessage },
                    modifier = Modifier,
                )
            }

            LaunchedEffect(showPinSetup) {
                if (!showPinSetup && onboardingComplete == true) {
                    when (val result = pinAuthRepository.isPinConfigured()) {
                        is Result.Success -> pinConfigured = result.data
                        is Result.Error -> Unit
                    }
                }
            }

            if (showReports) {
                features.reports.ReportsFlow(
                    onBack = { showReports = false },
                    empty = records.isEmpty(),
                    onLogFirstExpense = {
                        showReports = false
                        showQuickLog = true
                    },
                    homeCurrencySymbol = homeSymbol,
                )
            }

            ProBottomSheetHost(
                visible = showQuickAccessPicker,
                title = stringResource(R.string.quick_access_customize_title),
                onClose = { showQuickAccessPicker = false },
            ) {
                QuickAccessPickerSheetContent(
                    order = quickAccessVisible,
                    onToggle = { tile ->
                        val updated =
                            if (tile in quickAccessVisible) {
                                if (quickAccessVisible.size > 1) quickAccessVisible - tile else quickAccessVisible
                            } else {
                                quickAccessVisible + tile
                            }
                        quickAccessVisible = updated
                        QuickAccessPrefs.save(context, updated)
                    },
                    onMoveUp = { tile ->
                        val index = quickAccessVisible.indexOf(tile)
                        if (index > 0) {
                            val updated =
                                quickAccessVisible.toMutableList().apply {
                                    this[index] = this[index - 1].also { this[index - 1] = this[index] }
                                }
                            quickAccessVisible = updated
                            QuickAccessPrefs.save(context, updated)
                        }
                    },
                    onMoveDown = { tile ->
                        val index = quickAccessVisible.indexOf(tile)
                        if (index in 0 until quickAccessVisible.lastIndex) {
                            val updated =
                                quickAccessVisible.toMutableList().apply {
                                    this[index] = this[index + 1].also { this[index + 1] = this[index] }
                                }
                            quickAccessVisible = updated
                            QuickAccessPrefs.save(context, updated)
                        }
                    },
                )
            }

            ProToastHost(
                message = actionToastMessage,
                onDismiss = { actionToastMessage = null },
                bottomBarInset = ProExpenseTheme.dimensions.navBarHeight,
            )
        }
    }
}

/**
 * The shell's KMP ViewModel, scoped to this composition. Same pattern as
 * `rememberLoggingViewModel` in feature:logging — the platform host owns the lifecycle, so the
 * ViewModel's coroutine scope is cancelled when the shell leaves composition.
 */
@Composable
private fun rememberHomeViewModel(): HomeViewModel {
    val scope = currentKoinScope()
    val viewModel = remember { scope.get<HomeViewModel>() }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    return viewModel
}
