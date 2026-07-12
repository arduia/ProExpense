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
import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordKind
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SHOW_SPLIT_AND_DEBT_ROWS
import com.arduia.expense.domain.SPLIT_ROW_SUBTITLE_TYPE
import com.arduia.expense.domain.debtRowSubtitleType
import com.arduia.expense.domain.debtRowTitle
import com.arduia.expense.domain.isVisibleInHomeRecents
import com.arduia.expense.domain.kind
import com.arduia.expense.domain.linkedRowId
import com.arduia.expense.domain.splitRowTitle
import com.arduia.expense.domain.tagLabel
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.ui.ExpenseDraftPrefs
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.onboarding.CompleteOnboardingUseCase
import com.arduia.expense.feature.onboarding.GetOnboardingStatusUseCase
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.resolveCategoryLabel
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.QuickAccessPickerSheetContent
import com.arduia.expense.ui.home.QuickAccessPrefs
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.preview.HomeActiveEventState
import com.arduia.expense.ui.preview.HomeBudgetSummaryState
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.splash.SplashScreen
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val SPLASH_DURATION_MILLIS = 1800L
private const val RECENT_HOME_LIMIT = 8

@Composable
fun ExpenseApp(
    features: FeatureUiRegistry = FeatureUiRegistry(),
    modifier: Modifier = Modifier,
    getOnboardingStatus: GetOnboardingStatusUseCase = koinInject(),
    completeOnboarding: CompleteOnboardingUseCase = koinInject(),
    financeRecordRepository: FinanceRecordRepository = koinInject(),
    categoryRepository: CategoryRepository = koinInject(),
    eventRepository: EventRepository = koinInject(),
    debtRepository: DebtRepository = koinInject(),
    sharedCostRepository: SharedCostRepository = koinInject(),
    pinAuthRepository: PinAuthRepository = koinInject(),
    currencyRepository: CurrencyRepository = koinInject(),
    budgetRepository: BudgetRepository = koinInject(),
    defaultCategoryRepository: DefaultCategoryRepository = koinInject(),
    computeEventProgress: ComputeEventProgressUseCase = koinInject(),
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
    var userName by rememberSaveable { mutableStateOf("") }
    var userCurrency by rememberSaveable { mutableStateOf("") }
    var homeCurrencyCode by rememberSaveable { mutableStateOf("USD") }
    var monthlyBudget by remember { mutableStateOf<Money?>(null) }
    var defaultCategoryId by rememberSaveable { mutableStateOf("food") }
    var showQuickAccessPicker by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var quickAccessVisible by remember { mutableStateOf(QuickAccessPrefs.load(context)) }
    val coroutineScope = rememberCoroutineScope()
    // Hoisted above tab switching (same reason as spentByEvent below) — Journal's loaded pages
    // and filters live here so reselecting the tab resumes instantly instead of reloading.
    val journalTabState = features.history.rememberJournalTabState()

    // null (not emptyList()) until the first Flow emission arrives, so the empty-state
    // illustration doesn't flash on cold start before real data has had a chance to load.
    val recordsOrNull by financeRecordRepository.observeAll().collectAsState(initial = null)
    val recordsLoading = recordsOrNull == null
    val records = recordsOrNull.orEmpty()
    // Collected once here (not re-subscribed every time Add Expense opens) so the category
    // chips are already resolved by the time the user taps Add — collectAsState(emptyList())
    // in the logging flow itself re-queried on every visit and flashed empty chips each time.
    val categoriesOrNull by categoryRepository.observeAll().collectAsState(initial = null)
    val categories = categoriesOrNull.orEmpty()
    // Both expense and income categories show together — direction is decided by which
    // category the user picks, not by a separate toggle (US-LOG income).
    val defaultCategoryChips =
        remember(categories) {
            categories.filter { !it.isCustom }.sortedBy { it.sortOrder }.map { it.id.value to it.name }
        }
    val customCategoryChips =
        remember(categories) {
            categories.filter { it.isCustom }.sortedBy { it.sortOrder }.map { it.id.value to it.name }
        }
    val categoryTypeById =
        remember(categories) {
            categories.associate { it.id.value to it.type }
        }
    val eventsOrNull by eventRepository.observeAll().collectAsState(initial = null)
    val eventsLoading = eventsOrNull == null
    val events = eventsOrNull.orEmpty()
    val debts by debtRepository.observeAll().collectAsState(emptyList())
    val sharedCosts by sharedCostRepository.observeAll().collectAsState(emptyList())
    val eventNames = remember(events) { events.associate { it.id.value to it.name } }
    val debtNames = remember(debts) { debts.associate { it.id.value to it.personName } }
    val sharedCostNames = remember(sharedCosts) { sharedCosts.associate { it.id.value to it.title } }
    val categoryNames = remember(categories) { categories.associate { it.id.value to it.name } }
    // Hoisted here (not inside EventsTab) so it survives Budget <-> other tab switches — it
    // previously lived in a remember scoped to EventsTab itself, which was torn down and
    // recreated (resetting to an empty map) every time the user navigated away and back.
    // Summed from the already-observed records Flow (not a one-shot EventRepository.getSpent
    // call) so the Budget tab and Event Detail summary react immediately to any add/edit/delete
    // of a linked expense, not just to the event itself changing.
    val spentByEvent =
        remember(events, records) {
            events.associate { event ->
                val spentCents =
                    records
                        .filter { (it.link as? RecordLink.ToEvent)?.eventId == event.id }
                        .sumOf { it.homeCurrencyMoney.amount.valueInCents }
                event.id.value to Money(Amount(spentCents), event.budget.currency)
            }
        }

    val homeSymbol = currencySymbol(homeCurrencyCode)

    val activeEvent =
        remember(events) {
            events.filter { it.status == EventStatus.ACTIVE }.maxByOrNull { it.startEpochMillis }
        }
    val activeEventSpent =
        remember(activeEvent, spentByEvent) {
            activeEvent?.let { spentByEvent[it.id.value] }
        }
    val activeEventState =
        activeEvent?.let { event ->
            val progress = computeEventProgress(event, activeEventSpent)
            HomeActiveEventState(
                eventId = event.id.value,
                title = event.name,
                dateRange =
                    if (event.startEpochMillis == event.endEpochMillis) {
                        PlatformDateFormatter.shortDateLabel(event.startEpochMillis)
                    } else {
                        "${PlatformDateFormatter.shortDateLabel(event.startEpochMillis)} — " +
                            PlatformDateFormatter.shortDateLabel(event.endEpochMillis)
                    },
                spentLabel = AmountInput.formatMoney(progress.spentCents, homeSymbol),
                budgetLabel = "of " + AmountInput.formatMoney(progress.budgetCents, homeSymbol),
                progress = progress.progress,
                isOverBudget = progress.isOverBudget,
            )
        }

    val todaySection = stringResource(R.string.home_today_section)

    val dateLabel = remember { buildDateLabel() }
    val monthLabel = remember { buildMonthLabel() }

    LaunchedEffect(Unit) {
        val status = getOnboardingStatus()
        onboardingComplete = status.isComplete
        if (userName.isBlank()) userName = status.displayName
    }

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete == true) {
            ExpenseDraftPrefs.load(context)?.let { draft ->
                pendingDraftState = draft
                showQuickLog = true
            }
        }
    }

    LaunchedEffect(onboardingComplete, userCurrency) {
        when (val result = currencyRepository.getSettings()) {
            is Result.Success -> homeCurrencyCode = result.data.homeCurrency.code
            is Result.Error -> Unit
        }
    }

    LaunchedEffect(onboardingComplete) {
        when (val result = budgetRepository.getMonthlyBudget()) {
            is Result.Success -> monthlyBudget = result.data
            is Result.Error -> Unit
        }
        when (val result = defaultCategoryRepository.getDefaultCategoryId()) {
            is Result.Success -> result.data?.let { defaultCategoryId = it }
            is Result.Error -> Unit
        }
    }

    // Visible unconditionally (Debt & totals decision), but never counted toward spend/income
    // totals below — only a toggle-on debt (already a real, linked FinanceRecord in `records`)
    // does that.
    val visibleDebts = remember(debts) { debts.filter { !it.recordAsTransaction } }

    val homeState =
        if (records.isEmpty() && visibleDebts.isEmpty()) {
            previewHomeEmpty.copy(
                greetingName = userName,
                dateLabel = dateLabel,
                monthLabel = monthLabel,
                activeEvent = activeEventState,
                isLoading = recordsLoading,
            )
        } else {
            val monthStart =
                (Calendar.getInstance() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            val monthEnd = (monthStart.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            val recordsThisMonth =
                records.filter {
                    it.recordedAtEpochMillis >= monthStart.timeInMillis && it.recordedAtEpochMillis < monthEnd.timeInMillis
                }
            // "Spend this month" (US-HOME-1), not all-time — the header label promises a monthly
            // figure, and only expenses count as spend (income must not offset it).
            val totalCents =
                recordsThisMonth
                    .filter { it.type == RecordType.EXPENSE }
                    .sumOf { it.homeCurrencyMoney.amount.valueInCents }
            val totalLabel = AmountInput.formatMoney(totalCents, homeSymbol)
            val budgetSummary =
                monthlyBudget?.let { budget ->
                    val budgetCents = budget.amount.valueInCents
                    HomeBudgetSummaryState(
                        spentLabel = AmountInput.formatMoney(totalCents, homeSymbol),
                        budgetLabel = "of " + AmountInput.formatMoney(budgetCents, homeSymbol),
                        progress = if (budgetCents > 0) totalCents.toFloat() / budgetCents else 0f,
                        statusLabel = if (totalCents > budgetCents) "Over budget" else "On track",
                        isOverBudget = totalCents > budgetCents,
                    )
                }
            // Recent shows the last 5-10 entries (US-HOME-2), not the entire history — toggle-off
            // debts are merged in before truncating (see buildHomeDayGroups).
            val dayGroups =
                buildHomeDayGroups(
                    records = records,
                    visibleDebts = visibleDebts,
                    linkNames = HomeLinkNames(eventNames, debtNames, sharedCostNames, categoryNames),
                    homeCurrencySymbol = homeSymbol,
                    limit = RECENT_HOME_LIMIT,
                )
            previewHomeEmpty.copy(
                greetingName = userName,
                dateLabel = dateLabel,
                monthLabel = monthLabel,
                monthSpend = totalLabel,
                showEmptyHint = false,
                dayGroups = dayGroups,
                sparklinePoints = buildSparklinePoints(records),
                budgetSummary = budgetSummary,
                activeEvent = activeEventState,
            )
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
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
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
                                onCurrencyChanged = { homeCurrencyCode = it.code },
                                onBudgetChanged = { monthlyBudget = it },
                                onDefaultCategoryChanged = { defaultCategoryId = it },
                                onThemeModeChanged = onThemeModeChanged,
                                onLanguageChanged = onLanguageChanged,
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
                                onEventsClick = { selectedTab = HomeNavTab.Budget },
                                onLogFirstExpense = { showQuickLog = true },
                                onSeeAll = { selectedTab = HomeNavTab.Journal },
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
                        userName = handoff.profileName
                        userCurrency = handoff.currencyCode
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

private const val SPARKLINE_DAYS = 7

private fun buildSparklinePoints(records: List<FinanceRecord>): List<Float> {
    val today = Calendar.getInstance()
    return (SPARKLINE_DAYS - 1 downTo 0).map { offset ->
        val day = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
        val key = PlatformDateFormatter.dayKey(day.timeInMillis)
        records
            .filter { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) == key && it.type == RecordType.EXPENSE }
            // homeCurrencyMoney, not the record's own currency (US-CUR-4) — otherwise a foreign
            // currency amount is added into the sparkline as if it were home-currency cents.
            .sumOf { it.homeCurrencyMoney.amount.valueInCents }
            .toFloat()
    }
}

/** Shared timestamp so [FinanceRecord] and toggle-off [Debt] rows can be merged and truncated together. */
private data class HomeMergedEntry(
    val recordedAtEpochMillis: Long,
    val item: HomeTransactionItem,
)

private fun FinanceRecord.toHomeTransactionItem(
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
    sharedCostNames: Map<String, String>,
    categoryNames: Map<String, String>,
): HomeTransactionItem {
    val rowKind =
        when (kind()) {
            RecordKind.EXPENSE -> ProRowKind.EXPENSE
            RecordKind.INCOME -> ProRowKind.INCOME
            RecordKind.SPLIT -> ProRowKind.SPLIT
            RecordKind.DEBT_LENT -> ProRowKind.DEBT_LENT
            RecordKind.DEBT_OWED -> ProRowKind.DEBT_OWED
        }
    // The row's own badge/note already convey "this is a split/debt" — an "@ tag" chip repeating
    // the same title underneath would be redundant.
    val suppressTag = rowKind == ProRowKind.SPLIT || rowKind == ProRowKind.DEBT_LENT || rowKind == ProRowKind.DEBT_OWED
    val linkedId = linkedRowId()
    val timeLabel = PlatformDateFormatter.timeLabel(recordedAtEpochMillis)
    val (rowNote, rowMeta) =
        when (rowKind) {
            ProRowKind.SPLIT ->
                splitRowTitle(linkedId?.let { sharedCostNames[it] }) to "$SPLIT_ROW_SUBTITLE_TYPE · $timeLabel"
            ProRowKind.DEBT_LENT, ProRowKind.DEBT_OWED -> {
                val isLent = rowKind == ProRowKind.DEBT_LENT
                val personName = linkedId?.let { debtNames[it] }.orEmpty()
                debtRowTitle(personName, isLent) to "${debtRowSubtitleType(isLent)} · $timeLabel"
            }
            ProRowKind.EXPENSE, ProRowKind.INCOME ->
                note?.trim().orEmpty().ifEmpty { resolveCategoryLabel(categoryId.value, categoryNames) } to timeLabel
        }
    return HomeTransactionItem(
        id = id.value,
        categoryId = categoryId.value,
        note = rowNote,
        meta = rowMeta,
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        isIncome = type == RecordType.INCOME,
        tag = if (suppressTag) null else link.tagLabel(eventNames, debtNames, sharedCostNames),
        rowKind = rowKind,
        linkedId = linkedId,
    )
}

private fun Debt.toDebtHomeTransactionItem(): HomeTransactionItem {
    val isLent = direction == DebtDirection.OWED_TO_ME
    val rowKind = if (isLent) ProRowKind.DEBT_LENT else ProRowKind.DEBT_OWED
    return HomeTransactionItem(
        id = id.value,
        categoryId = "",
        note = debtRowTitle(personName, isLent),
        meta = "${debtRowSubtitleType(isLent)} · ${PlatformDateFormatter.timeLabel(recordedAtEpochMillis)}",
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        rowKind = rowKind,
        linkedId = id.value,
    )
}

/** Bundles the tag-label lookup maps so they cost one parameter, not four, in call sites below. */
private data class HomeLinkNames(
    val eventNames: Map<String, String>,
    val debtNames: Map<String, String>,
    val sharedCostNames: Map<String, String>,
    val categoryNames: Map<String, String>,
)

/**
 * Merges real records with toggle-off debts (visible everywhere, never counted toward totals —
 * see [Debt.recordAsTransaction]) before truncating to [limit], so a recent debt doesn't get
 * evicted by real expenses/income that are actually older than it, then groups by day. Day totals
 * are computed from the [records] subset only.
 */
private fun buildHomeDayGroups(
    records: List<FinanceRecord>,
    visibleDebts: List<Debt>,
    linkNames: HomeLinkNames,
    homeCurrencySymbol: String,
    limit: Int,
): List<HomeDayGroup> {
    val recordEntries =
        records
            .filter { it.kind().isVisibleInHomeRecents() }
            .map { record ->
                val item =
                    record.toHomeTransactionItem(
                        linkNames.eventNames,
                        linkNames.debtNames,
                        linkNames.sharedCostNames,
                        linkNames.categoryNames,
                    )
                HomeMergedEntry(record.recordedAtEpochMillis, item)
            }
    // Unlike the recordEntries filter above, toggle-off debts are never a real FinanceRecord — a
    // debt-kind entry there always means toggle-on (counted), so it keeps its own gate here.
    val debtEntries =
        if (SHOW_SPLIT_AND_DEBT_ROWS) {
            visibleDebts.map { debt -> HomeMergedEntry(debt.recordedAtEpochMillis, debt.toDebtHomeTransactionItem()) }
        } else {
            emptyList()
        }
    val recent = (recordEntries + debtEntries).sortedByDescending { it.recordedAtEpochMillis }.take(limit)

    return recent
        .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
        .toSortedMap(compareByDescending { it })
        .map { (key, dayEntries) ->
            val dayTotalCents =
                records
                    .filter {
                        PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) == key && it.type == RecordType.EXPENSE
                    }.sumOf { it.homeCurrencyMoney.amount.valueInCents }
            HomeDayGroup(
                dayTitle = PlatformDateFormatter.dayLabel(dayEntries.first().recordedAtEpochMillis),
                dayTotal = AmountInput.formatMoney(dayTotalCents, homeCurrencySymbol),
                transactions = dayEntries.map { it.item },
            )
        }
}

private fun buildDateLabel(): String {
    val calendar = Calendar.getInstance()
    return SimpleDateFormat("EEE · MMM d", Locale.US).format(calendar.time).uppercase()
}

private fun buildMonthLabel(): String {
    val calendar = Calendar.getInstance()
    return SimpleDateFormat("MMM", Locale.US).format(calendar.time).uppercase()
}
