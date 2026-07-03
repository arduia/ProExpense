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
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
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
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.QuickAccessPickerSheetContent
import com.arduia.expense.ui.home.QuickAccessPrefs
import com.arduia.expense.ui.home.QuickAccessTileType
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.preview.HomeActiveEventState
import com.arduia.expense.ui.preview.HomeBudgetSummaryState
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.splash.SplashScreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

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
    var showPinSetup by rememberSaveable { mutableStateOf(false) }
    var showReports by rememberSaveable { mutableStateOf(false) }
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

    // null (not emptyList()) until the first Flow emission arrives, so the empty-state
    // illustration doesn't flash on cold start before real data has had a chance to load.
    val recordsOrNull by financeRecordRepository.observeAll().collectAsState(initial = null)
    val recordsLoading = recordsOrNull == null
    val records = recordsOrNull.orEmpty()
    var categoryMap by remember { mutableStateOf<Map<String, Category>>(emptyMap()) }
    val eventsOrNull by eventRepository.observeAll().collectAsState(initial = null)
    val eventsLoading = eventsOrNull == null
    val events = eventsOrNull.orEmpty()
    val debts by debtRepository.observeAll().collectAsState(emptyList())
    val sharedCosts by sharedCostRepository.observeAll().collectAsState(emptyList())
    val eventNames = remember(events) { events.associate { it.id.value to it.name } }
    val debtNames = remember(debts) { debts.associate { it.id.value to it.personName } }
    val sharedCostNames = remember(sharedCosts) { sharedCosts.associate { it.id.value to it.title } }

    val homeSymbol = currencySymbol(homeCurrencyCode)

    val activeEvent = remember(events) {
        events.filter { it.status == EventStatus.ACTIVE }.maxByOrNull { it.startEpochMillis }
    }
    var activeEventSpent by remember { mutableStateOf<Money?>(null) }
    LaunchedEffect(activeEvent) {
        activeEventSpent = activeEvent?.let { event ->
            (eventRepository.getSpent(event.id) as? Result.Success)?.data
        }
    }
    val activeEventState = activeEvent?.let { event ->
        val progress = computeEventProgress(event, activeEventSpent)
        HomeActiveEventState(
            eventId = event.id.value,
            title = event.name,
            dateRange = if (event.startEpochMillis == event.endEpochMillis) {
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

    val noteFallback = stringResource(R.string.home_logged_note_fallback)
    val todaySection = stringResource(R.string.home_today_section)

    val dateLabel = remember { buildDateLabel() }
    val monthLabel = remember { buildMonthLabel() }

    LaunchedEffect(Unit) {
        when (val result = categoryRepository.getAll()) {
            is Result.Success -> {
                categoryMap = result.data.associateBy { it.id.value }
            }
            is Result.Error -> {
                // Log error if needed
            }
        }
    }

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

    val homeState = if (records.isEmpty()) {
        previewHomeEmpty.copy(
            greetingName = userName,
            dateLabel = dateLabel,
            monthLabel = monthLabel,
            activeEvent = activeEventState,
            isLoading = recordsLoading,
        )
    } else {
        val monthStart = (Calendar.getInstance() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monthEnd = (monthStart.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        val recordsThisMonth = records.filter {
            it.recordedAtEpochMillis >= monthStart.timeInMillis && it.recordedAtEpochMillis < monthEnd.timeInMillis
        }
        // "Spend this month" (US-HOME-1), not all-time — the header label promises a monthly figure.
        val totalCents = recordsThisMonth.sumOf { it.homeCurrencyMoney.amount.valueInCents }
        val totalLabel = AmountInput.formatMoney(totalCents, homeSymbol)
        val budgetSummary = monthlyBudget?.let { budget ->
            val budgetCents = budget.amount.valueInCents
            HomeBudgetSummaryState(
                spentLabel = AmountInput.formatMoney(totalCents, homeSymbol),
                budgetLabel = "of " + AmountInput.formatMoney(budgetCents, homeSymbol),
                progress = if (budgetCents > 0) totalCents.toFloat() / budgetCents else 0f,
                statusLabel = if (totalCents > budgetCents) "Over budget" else "On track",
                isOverBudget = totalCents > budgetCents,
            )
        }
        val sorted = records.sortedByDescending { it.recordedAtEpochMillis }
        // Recent shows the last 5-10 entries (US-HOME-2), not the entire history.
        val dayGroups = sorted
            .take(RECENT_HOME_LIMIT)
            .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
            .toSortedMap(compareByDescending { it })
            .map { (_, dayRecords) ->
                val dayTotalCents = dayRecords.sumOf { it.homeCurrencyMoney.amount.valueInCents }
                val dayTotalLabel = AmountInput.formatMoney(dayTotalCents, homeSymbol)
                HomeDayGroup(
                    dayTitle = PlatformDateFormatter.dayLabel(dayRecords.first().recordedAtEpochMillis),
                    dayTotal = dayTotalLabel,
                    transactions = dayRecords.map { record ->
                        HomeTransactionItem(
                            id = record.id.value,
                            categoryId = record.categoryId.value,
                            note = record.note?.trim().orEmpty().ifEmpty { noteFallback },
                            meta = PlatformDateFormatter.timeLabel(record.recordedAtEpochMillis),
                            amount = AmountInput.formatMoney(
                                record.money.amount.valueInCents,
                                currencySymbol(record.money.currency.code),
                            ),
                            tag = record.link.tagLabel(eventNames, debtNames, sharedCostNames),
                        )
                    },
                )
            }
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
        if (tab == HomeNavTab.Home || tab == HomeNavTab.Budget ||
            tab == HomeNavTab.Journal || tab == HomeNavTab.More
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
        val observer = LifecycleEventObserver { _, event ->
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
                    )
                } else if (pinConfigured == true && !unlocked) {
                    features.auth.PinLockFlow(
                        onUnlocked = { unlocked = true },
                        modifier = Modifier,
                    )
                } else if (pinConfigured != null) {
                    when (selectedTab) {
                        HomeNavTab.Budget -> features.eventBudget.EventsTab(
                            events = events,
                            onTabSelected = onTabSelected,
                            onAddClick = { showQuickLog = true },
                            initialSelectedEventId = homeSelectedEventId,
                            onAddTaggedExpense = { eventId ->
                                quickLogLinkedEventId = eventId
                                showQuickLog = true
                            },
                            homeCurrencySymbol = homeSymbol,
                            isLoading = eventsLoading,
                        )
                        HomeNavTab.Journal -> features.history.JournalTab(
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected,
                            onAddClick = { showQuickLog = true },
                            initialSelectedRowId = homeSelectedRecordId,
                            onEditRecord = { editRecordId = it },
                            homeCurrencySymbol = homeSymbol,
                        )
                        HomeNavTab.More -> MoreFlow(
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
                        )
                        else -> HomeShell(
                            state = homeState,
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected,
                            onAddClick = { showQuickLog = true },
                            onReportsClick = { showReports = true },
                            onDebtClick = { showDebt = true },
                            onSplitClick = { showSharedCosts = true },
                            onEventsClick = { selectedTab = HomeNavTab.Budget },
                            onLogFirstExpense = { showQuickLog = true },
                            onSeeAll = { selectedTab = HomeNavTab.Journal },
                            onCustomizeQuickAccess = { showQuickAccessPicker = true },
                            visibleTiles = quickAccessVisible,
                            onRowClick = { row ->
                                homeSelectedRecordId = row.id
                                selectedTab = HomeNavTab.Journal
                            },
                            onActiveEventClick = { eventId ->
                                homeSelectedEventId = eventId
                                selectedTab = HomeNavTab.Budget
                            },
                        )
                    }
                }
            } else {
                features.onboarding.FirstLaunchFlow(
                    onComplete = { handoff ->
                        userName = handoff.profileName
                        userCurrency = handoff.currencyCode
                        coroutineScope.launch {
                            withContext(NonCancellable) {
                                completeOnboarding(handoff.profileName, handoff.currencyCode)
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
                )
            }

            editRecordId?.let { recordId ->
                features.logging.EditExpenseFlow(
                    recordId = recordId,
                    onDismiss = { editRecordId = null },
                    onSaved = { editRecordId = null },
                    homeCurrencySymbol = homeSymbol,
                )
            }

            if (showSharedCosts) {
                features.sharedCost.SharedCostsOverlay(
                    onDismiss = { showSharedCosts = false },
                    homeCurrencySymbol = homeSymbol,
                )
            }

            if (showDebt) {
                features.debt.DebtOverlay(onDismiss = { showDebt = false }, homeCurrencySymbol = homeSymbol)
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
                    selected = quickAccessVisible,
                    onToggle = { tile ->
                        val updated = if (tile in quickAccessVisible) {
                            if (quickAccessVisible.size > 1) quickAccessVisible - tile else quickAccessVisible
                        } else {
                            quickAccessVisible + tile
                        }
                        quickAccessVisible = updated
                        QuickAccessPrefs.save(context, updated)
                    },
                )
            }

            ProToastHost(
                message = actionToastMessage,
                onDismiss = { actionToastMessage = null },
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
            .filter { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) == key }
            // homeCurrencyMoney, not the record's own currency (US-CUR-4) — otherwise a foreign
            // currency amount is added into the sparkline as if it were home-currency cents.
            .sumOf { it.homeCurrencyMoney.amount.valueInCents }
            .toFloat()
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
