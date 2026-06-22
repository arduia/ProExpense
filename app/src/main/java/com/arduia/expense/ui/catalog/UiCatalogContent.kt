package com.arduia.expense.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.arduia.expense.feature.auth.ui.PinEntryScreen
import com.arduia.expense.feature.auth.ui.PinRecoveryScreen
import com.arduia.expense.feature.auth.ui.PinSecurityQuestionScreen
import com.arduia.expense.feature.auth.ui.PinSetPinScreen
import com.arduia.expense.feature.auth.ui.PinSetupScreen
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.feature.auth.ui.preview.previewPinEntry
import com.arduia.expense.feature.auth.ui.preview.previewPinLock
import com.arduia.expense.feature.auth.ui.preview.previewPinSetConfirmMismatch
import com.arduia.expense.feature.auth.ui.preview.previewPinSetup
import com.arduia.expense.feature.auth.ui.preview.previewPinWrong
import com.arduia.expense.feature.categories.ui.CategoryActionsSheetContent
import com.arduia.expense.feature.categories.ui.CategoryListScreen
import com.arduia.expense.feature.categories.ui.CategoryNewSheetContent
import com.arduia.expense.feature.categories.ui.preview.CategoryNewFormState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.feature.categories.ui.preview.previewCategoryNewDuplicate
import com.arduia.expense.feature.currency.ui.MoreCurrencyScreen
import com.arduia.expense.feature.currency.ui.preview.previewMoreCurrencies
import com.arduia.expense.feature.debt.ui.DebtAddSheetContent
import com.arduia.expense.feature.debt.ui.DebtDetailScreen
import com.arduia.expense.feature.debt.ui.DebtListScreen
import com.arduia.expense.feature.debt.ui.preview.previewDebtAddLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtLentDetail
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe
import com.arduia.expense.feature.debt.ui.preview.previewDebtOweDetail
import com.arduia.expense.feature.debt.ui.preview.previewDebtSettled
import com.arduia.expense.feature.eventbudget.ui.EventBudgetListScreen
import com.arduia.expense.feature.eventbudget.ui.EventCreateSheetContent
import com.arduia.expense.feature.eventbudget.ui.EventDetailScreen
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventCreateErrors
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventCreateValid
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetail
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetailClosed
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetailWarn
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
import com.arduia.expense.feature.history.ui.JournalActionsSheetContent
import com.arduia.expense.feature.history.ui.JournalDetailScreen
import com.arduia.expense.feature.history.ui.JournalListScreen
import com.arduia.expense.feature.history.ui.JournalQuickNoteSheetContent
import com.arduia.expense.feature.history.ui.preview.previewJournalDetail
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.feature.history.ui.preview.previewJournalQuickNote
import com.arduia.expense.feature.history.ui.preview.previewJournalSearchEmpty
import com.arduia.expense.feature.importexport.ui.MoreClearScreen
import com.arduia.expense.feature.importexport.ui.MoreExportScreen
import com.arduia.expense.feature.importexport.ui.preview.previewMoreClearOptions
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.feature.logging.ui.AddExpenseAmountScreen
import com.arduia.expense.feature.logging.ui.AddExpenseDetailsScreen
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountZeroValidation
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetails
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsNoteLimit
import com.arduia.expense.feature.onboarding.ui.OnboardingScreenContent
import com.arduia.expense.feature.onboarding.ui.ProfileSetupScreenContent
import com.arduia.expense.feature.onboarding.ui.ProfileSetupState
import com.arduia.expense.feature.onboarding.ui.ProfileSetupStep
import com.arduia.expense.feature.reports.ui.ReportsScreen
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.feature.sharedcost.ui.SharedCostsHistoryScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsSummaryScreen
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedCustomLimits
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummary
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedZeroValidation
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.more.MoreHubScreen
import com.arduia.expense.ui.preview.previewHomeBudget
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewHomeEvent
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.splash.SplashScreen
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.feature.auth.R as AuthR
import com.arduia.expense.feature.categories.R as CategoriesR
import com.arduia.expense.feature.debt.R as DebtR
import com.arduia.expense.feature.eventbudget.R as EventR
import com.arduia.expense.feature.history.R as HistoryR

/** One navigable screen state in the UI verification catalog. */
data class UiCatalogEntry(
    val id: String,
    val label: String,
    val edge: Boolean = false,
    val content: @Composable () -> Unit,
)

/** A group of states, keyed to a design-spec screen. */
data class UiCatalogSection(
    val id: String,
    val title: String,
    val specRef: String,
    val entries: List<UiCatalogEntry>,
)

/** Flat lookup across every section. */
fun UiCatalogSection.entryOrNull(entryId: String): UiCatalogEntry? =
    entries.firstOrNull { it.id == entryId }

/**
 * Every screen and edge-case state from the design spec / PRD, wired to the same public content
 * composables + preview fixtures the Roborazzi suite uses — so this catalog stays in lock-step with
 * what ships. Pure data: the @Composable lambdas are rendered lazily when a state is opened.
 */
fun uiCatalogSections(): List<UiCatalogSection> = listOf(
    UiCatalogSection(
        id = "onboarding",
        title = "Onboarding & setup",
        specRef = "01 · 02 · 02P",
        entries = listOf(
            UiCatalogEntry("splash", "Splash") { SplashScreen() },
            UiCatalogEntry("onboarding_welcome", "Onboarding · welcome") {
                OnboardingScreenContent(onGetStarted = {}, onSkip = {}, initialPage = 0)
            },
            UiCatalogEntry("onboarding_quick_log", "Onboarding · quick log") {
                OnboardingScreenContent(onGetStarted = {}, onSkip = {}, initialPage = 1)
            },
            UiCatalogEntry("onboarding_journal", "Onboarding · journal") {
                OnboardingScreenContent(onGetStarted = {}, onSkip = {}, initialPage = 4)
            },
            UiCatalogEntry("profile_name", "Profile · name") {
                ProfileSetupScreenContent(
                    state = ProfileSetupState(name = "Maya"),
                    onNameChange = {},
                    onContinue = {},
                    onStartTracking = {},
                    onSkip = {},
                    onCurrencySelected = {},
                    onOpenCurrencySheet = {},
                    onCloseCurrencySheet = {},
                    onCurrencySearchChange = {},
                )
            },
            UiCatalogEntry("profile_currency", "Profile · currency") {
                ProfileSetupScreenContent(
                    state = ProfileSetupState(step = ProfileSetupStep.Currency),
                    onNameChange = {},
                    onContinue = {},
                    onStartTracking = {},
                    onSkip = {},
                    onCurrencySelected = {},
                    onOpenCurrencySheet = {},
                    onCloseCurrencySheet = {},
                    onCurrencySearchChange = {},
                )
            },
            UiCatalogEntry("profile_currency_sheet", "Profile · currency sheet", edge = true) {
                ProfileSetupScreenContent(
                    state = ProfileSetupState(
                        step = ProfileSetupStep.Currency,
                        showCurrencySheet = true,
                    ),
                    onNameChange = {},
                    onContinue = {},
                    onStartTracking = {},
                    onSkip = {},
                    onCurrencySelected = {},
                    onOpenCurrencySheet = {},
                    onCloseCurrencySheet = {},
                    onCurrencySearchChange = {},
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "home",
        title = "Home",
        specRef = "03",
        entries = listOf(
            UiCatalogEntry("home_casual", "Home · casual") {
                HomeShell(previewHomeCasual, HomeNavTab.Home, {}, {})
            },
            UiCatalogEntry("home_budget", "Home · budget") {
                HomeShell(previewHomeBudget, HomeNavTab.Home, {}, {})
            },
            UiCatalogEntry("home_event", "Home · event") {
                HomeShell(previewHomeEvent, HomeNavTab.Home, {}, {})
            },
            UiCatalogEntry("home_empty", "Home · empty", edge = true) {
                HomeShell(previewHomeEmpty, HomeNavTab.Home, {}, {})
            },
            UiCatalogEntry("home_pin_banner", "Home · PIN banner", edge = true) {
                HomeShell(
                    state = previewHomeCasual,
                    selectedTab = HomeNavTab.Home,
                    onTabSelected = {},
                    onAddClick = {},
                    showPinSetupBanner = true,
                )
            },
            UiCatalogEntry("home_content_only", "Home · content only") {
                HomeScreenContent(
                    state = previewHomeCasual,
                    onReportsClick = {},
                    onDebtClick = {},
                    onSplitClick = {},
                    onEventsClick = {},
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "add_expense",
        title = "Add expense",
        specRef = "04",
        entries = listOf(
            UiCatalogEntry("add_amount", "Amount") {
                AddExpenseAmountScreen(
                    state = previewExpenseAmountTyped,
                    onClose = {},
                    onKey = {},
                    onBackspace = {},
                    onCategorySelected = {},
                    onSave = {},
                    onNext = {},
                )
            },
            UiCatalogEntry("add_zero", "Amount · zero validation", edge = true) {
                AddExpenseAmountScreen(
                    state = previewExpenseAmountZeroValidation,
                    onClose = {},
                    onKey = {},
                    onBackspace = {},
                    onCategorySelected = {},
                    onSave = {},
                    onNext = {},
                )
            },
            UiCatalogEntry("add_details", "Details") {
                AddExpenseDetailsScreen(
                    state = previewExpenseDetails,
                    onBackToAmount = {},
                    onCategorySelected = {},
                    onNoteChange = {},
                    onDateClick = {},
                    onOpenTagSheet = {},
                    onCloseTagSheet = {},
                    onTagSelected = {},
                    onClearTag = {},
                    onSave = {},
                )
            },
            UiCatalogEntry("add_draft", "Discard draft prompt", edge = true) {
                QuickLogFlow(onDismiss = {}, showDraftPrompt = true, draftAmountLabel = "$12.50")
            },
            UiCatalogEntry("add_note", "Details · note limit", edge = true) {
                AddExpenseDetailsScreen(
                    state = previewExpenseDetailsNoteLimit,
                    onBackToAmount = {},
                    onCategorySelected = {},
                    onNoteChange = {},
                    onDateClick = {},
                    onOpenTagSheet = {},
                    onCloseTagSheet = {},
                    onTagSelected = {},
                    onClearTag = {},
                    onSave = {},
                )
            },
            UiCatalogEntry("add_tag", "Details · tag sheet", edge = true) {
                AddExpenseDetailsScreen(
                    state = previewExpenseDetails.copy(showTagSheet = true),
                    onBackToAmount = {},
                    onCategorySelected = {},
                    onNoteChange = {},
                    onDateClick = {},
                    onOpenTagSheet = {},
                    onCloseTagSheet = {},
                    onTagSelected = {},
                    onClearTag = {},
                    onSave = {},
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "journal",
        title = "Journal",
        specRef = "05 · 06",
        entries = listOf(
            UiCatalogEntry("journal_list", "List") {
                JournalListScreen(
                    state = previewJournalList,
                    onQueryChange = {},
                    onFilterSelected = {},
                    onRowClick = {},
                    onRowLongPress = {},
                    selectedTab = HomeNavTab.Journal,
                    onTabSelected = {},
                    onAddClick = {},
                )
            },
            UiCatalogEntry("journal_search_empty", "Search · empty", edge = true) {
                JournalListScreen(
                    state = previewJournalSearchEmpty,
                    onQueryChange = {},
                    onFilterSelected = {},
                    onRowClick = {},
                    onRowLongPress = {},
                    selectedTab = HomeNavTab.Journal,
                    onTabSelected = {},
                    onAddClick = {},
                )
            },
            UiCatalogEntry("journal_detail", "Detail") {
                JournalDetailScreen(
                    state = previewJournalDetail,
                    onBack = {},
                    onActions = {},
                    onLinkedTagClick = {},
                    onEdit = {},
                    onDelete = {},
                )
            },
            UiCatalogEntry("journal_quicknote", "Quick-note sheet", edge = true) {
                Box(Modifier.fillMaxSize()) {
                    JournalListScreen(
                        state = previewJournalList,
                        onQueryChange = {},
                        onFilterSelected = {},
                        onRowClick = {},
                        onRowLongPress = {},
                        selectedTab = HomeNavTab.Journal,
                        onTabSelected = {},
                        onAddClick = {},
                    )
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(HistoryR.string.journal_quick_note_title),
                        onClose = {},
                    ) {
                        JournalQuickNoteSheetContent(
                            state = previewJournalQuickNote,
                            onNoteChange = {},
                            onSave = {},
                        )
                    }
                }
            },
            UiCatalogEntry("journal_actions", "Actions sheet") {
                Box(Modifier.fillMaxSize()) {
                    JournalDetailScreen(
                        state = previewJournalDetail,
                        onBack = {},
                        onActions = {},
                        onLinkedTagClick = {},
                        onEdit = {},
                        onDelete = {},
                    )
                    ProBottomSheetHost(visible = true, title = null, onClose = {}) {
                        JournalActionsSheetContent(onEdit = {}, onDelete = {}, onCancel = {})
                    }
                }
            },
        ),
    ),
    UiCatalogSection(
        id = "event_budget",
        title = "Event budget",
        specRef = "07 · 08",
        entries = listOf(
            UiCatalogEntry("event_empty", "List · empty", edge = true) {
                EventBudgetListScreen(
                    events = emptyList(),
                    onCreateEvent = {},
                    onEventClick = {},
                    selectedTab = HomeNavTab.Budget,
                    onTabSelected = {},
                    onAddClick = {},
                )
            },
            UiCatalogEntry("event_list", "List") {
                EventBudgetListScreen(
                    events = previewEventList,
                    onCreateEvent = {},
                    onEventClick = {},
                    selectedTab = HomeNavTab.Budget,
                    onTabSelected = {},
                    onAddClick = {},
                )
            },
            UiCatalogEntry("event_create", "Create sheet") {
                Box(Modifier.fillMaxSize()) {
                    EventBudgetListScreen(
                        events = previewEventList,
                        onCreateEvent = {},
                        onEventClick = {},
                        selectedTab = HomeNavTab.Budget,
                        onTabSelected = {},
                        onAddClick = {},
                    )
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(EventR.string.event_create_title),
                        onClose = {},
                    ) {
                        EventCreateSheetContent(
                            form = previewEventCreateValid,
                            onNameChange = {},
                            onBudgetChange = {},
                            onPickStart = {},
                            onPickEnd = {},
                            onSave = {},
                        )
                    }
                }
            },
            UiCatalogEntry("event_errors", "Create · errors", edge = true) {
                Box(Modifier.fillMaxSize()) {
                    EventBudgetListScreen(
                        events = previewEventList,
                        onCreateEvent = {},
                        onEventClick = {},
                        selectedTab = HomeNavTab.Budget,
                        onTabSelected = {},
                        onAddClick = {},
                    )
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(EventR.string.event_create_title),
                        onClose = {},
                    ) {
                        EventCreateSheetContent(
                            form = previewEventCreateErrors,
                            onNameChange = {},
                            onBudgetChange = {},
                            onPickStart = {},
                            onPickEnd = {},
                            onSave = {},
                        )
                    }
                }
            },
            UiCatalogEntry("event_detail", "Detail") {
                EventDetailScreen(
                    state = previewEventDetail,
                    onBack = {},
                    onMore = {},
                    onAddTagged = {},
                    onExpenseClick = {},
                )
            },
            UiCatalogEntry("event_warn", "Detail · over budget", edge = true) {
                EventDetailScreen(
                    state = previewEventDetailWarn,
                    onBack = {},
                    onMore = {},
                    onAddTagged = {},
                    onExpenseClick = {},
                )
            },
            UiCatalogEntry("event_closed", "Detail · closed", edge = true) {
                EventDetailScreen(
                    state = previewEventDetailClosed,
                    onBack = {},
                    onMore = {},
                    onAddTagged = {},
                    onExpenseClick = {},
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "debt",
        title = "Debt tracker",
        specRef = "09",
        entries = listOf(
            UiCatalogEntry("debt_lent", "I lent") {
                DebtListScreen(previewDebtLent, {}, {}, {}, {})
            },
            UiCatalogEntry("debt_owe", "I owe") {
                DebtListScreen(previewDebtOwe, {}, {}, {}, {})
            },
            UiCatalogEntry("debt_add", "Add record sheet") {
                Box(Modifier.fillMaxSize()) {
                    DebtListScreen(previewDebtLent, {}, {}, {}, {})
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(DebtR.string.debt_new_record),
                        onClose = {},
                    ) {
                        DebtAddSheetContent(
                            form = previewDebtAddLent,
                            onSideSelected = {},
                            onPersonChange = {},
                            onAmountChange = {},
                            onPickDate = {},
                            onPickDue = {},
                            onSave = {},
                        )
                    }
                }
            },
            UiCatalogEntry("debt_lent_detail", "Detail · lent") {
                DebtDetailScreen(previewDebtLentDetail, {}, {}, {}, {})
            },
            UiCatalogEntry("debt_owe_detail", "Detail · owe") {
                DebtDetailScreen(previewDebtOweDetail, {}, {}, {}, {})
            },
            UiCatalogEntry("debt_conflict", "Opposite-side warning", edge = true) {
                Box(Modifier.fillMaxSize()) {
                    DebtListScreen(previewDebtLent, {}, {}, {}, {})
                    ProAlertDialog(
                        visible = true,
                        icon = ProIconGlyph.User,
                        iconTint = ProExpenseTheme.colors.warning,
                        iconBackground = ProExpenseTheme.colors.warningTint,
                        title = stringResource(DebtR.string.debt_conflict_title, "John"),
                        body = AnnotatedString(
                            stringResource(
                                DebtR.string.debt_conflict_body,
                                "John",
                                "\"" + stringResource(DebtR.string.debt_tab_owe) + "\"",
                                "\"" + stringResource(DebtR.string.debt_tab_lent) + "\"",
                            ),
                        ),
                        confirmLabel = stringResource(DebtR.string.debt_continue),
                        onConfirm = {},
                        dismissLabel = stringResource(DebtR.string.debt_cancel),
                        onDismiss = {},
                        confirmVariant = ProButtonVariant.Warning,
                    )
                }
            },
            UiCatalogEntry("debt_settled", "Delete settled confirm", edge = true) {
                Box(Modifier.fillMaxSize()) {
                    DebtListScreen(previewDebtSettled, {}, {}, {}, {})
                    ProAlertDialog(
                        visible = true,
                        icon = ProIconGlyph.Close,
                        iconTint = ProExpenseTheme.colors.danger,
                        iconBackground = ProExpenseTheme.colors.dangerTint,
                        title = stringResource(DebtR.string.debt_delete_title),
                        body = AnnotatedString(stringResource(DebtR.string.debt_delete_body, "Aiko")),
                        confirmLabel = stringResource(DebtR.string.debt_delete),
                        onConfirm = {},
                        dismissLabel = stringResource(DebtR.string.debt_cancel),
                        onDismiss = {},
                        confirmVariant = ProButtonVariant.Danger,
                    )
                }
            },
        ),
    ),
    UiCatalogSection(
        id = "shared_costs",
        title = "Shared costs",
        specRef = "10",
        entries = listOf(
            UiCatalogEntry("shared_input", "Input") {
                SharedCostsInputScreen(
                    state = previewSharedInputEqual,
                    onBack = {},
                    onKey = {},
                    onBackspace = {},
                    onNoteChange = {},
                    onDecrementPeople = {},
                    onIncrementPeople = {},
                    onModeSelected = {},
                    onShareChange = { _, _ -> },
                    onContinue = {},
                    showKeypad = false,
                )
            },
            UiCatalogEntry("shared_summary", "Summary") {
                SharedCostsSummaryScreen(
                    state = previewSharedSummary,
                    onBack = {},
                    onSwitchToCustom = {},
                    onSave = {},
                )
            },
            UiCatalogEntry("shared_history", "History") {
                SharedCostsHistoryScreen(
                    items = previewSharedHistoryItems,
                    onNewSplit = {},
                    onItemClick = {},
                )
            },
            UiCatalogEntry("shared_zero", "Input · zero validation", edge = true) {
                SharedCostsInputScreen(
                    state = previewSharedZeroValidation,
                    onBack = {},
                    onKey = {},
                    onBackspace = {},
                    onNoteChange = {},
                    onDecrementPeople = {},
                    onIncrementPeople = {},
                    onModeSelected = {},
                    onShareChange = { _, _ -> },
                    onContinue = {},
                    showKeypad = false,
                )
            },
            UiCatalogEntry("shared_limits", "Input · custom limits", edge = true) {
                SharedCostsInputScreen(
                    state = previewSharedCustomLimits,
                    onBack = {},
                    onKey = {},
                    onBackspace = {},
                    onNoteChange = {},
                    onDecrementPeople = {},
                    onIncrementPeople = {},
                    onModeSelected = {},
                    onShareChange = { _, _ -> },
                    onContinue = {},
                    showKeypad = false,
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "reports",
        title = "Reports",
        specRef = "12",
        entries = listOf(
            UiCatalogEntry("reports", "Reports") { ReportsScreen(previewReports, {}, {}, {}) },
            UiCatalogEntry("reports_unc", "Uncategorized", edge = true) {
                ReportsScreen(previewReportsUncategorized, {}, {}, {})
            },
            UiCatalogEntry("reports_empty", "Empty", edge = true) {
                ReportsScreen(previewReportsEmpty, {}, {}, {})
            },
        ),
    ),
    UiCatalogSection(
        id = "more",
        title = "More & settings",
        specRef = "13",
        entries = listOf(
            UiCatalogEntry("more_hub", "Hub") {
                MoreHubScreen(
                    state = previewMoreHub,
                    onFeatureClick = {},
                    onSettingClick = {},
                    onSettingToggle = { _, _ -> },
                    selectedTab = HomeNavTab.More,
                    onTabSelected = {},
                    onAddClick = {},
                )
            },
            UiCatalogEntry("more_currency", "Currency") {
                MoreCurrencyScreen(
                    items = previewMoreCurrencies,
                    selectedCode = "USD",
                    onSelect = {},
                    onBack = {},
                )
            },
            UiCatalogEntry("more_export", "Data export") {
                MoreExportScreen(files = previewMoreExportFiles, onExport = {}, onBack = {})
            },
            UiCatalogEntry("more_clear", "Clear data", edge = true) {
                MoreClearScreen(
                    options = previewMoreClearOptions,
                    checkedIds = setOf("expenses"),
                    onToggle = {},
                    onClear = {},
                    onBack = {},
                )
            },
        ),
    ),
    UiCatalogSection(
        id = "categories",
        title = "Categories",
        specRef = "11",
        entries = listOf(
            UiCatalogEntry("categories", "List") {
                CategoryListScreen(previewCategoryList, {}, {})
            },
            UiCatalogEntry("cat_dup", "New · duplicate", edge = true) {
                Box(Modifier.fillMaxSize()) {
                    CategoryListScreen(previewCategoryList, {}, {})
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(CategoriesR.string.categories_new_title),
                        onClose = {},
                    ) {
                        CategoryNewSheetContent(
                            form = previewCategoryNewDuplicate,
                            onNameChange = {},
                            onIconSelected = {},
                            onAdd = {},
                        )
                    }
                }
            },
            UiCatalogEntry("cat_actions", "Actions sheet") {
                Box(Modifier.fillMaxSize()) {
                    CategoryListScreen(previewCategoryList, {}, {})
                    ProBottomSheetHost(visible = true, title = null, onClose = {}) {
                        CategoryActionsSheetContent(
                            row = CategoryRowUi("coffee", "Coffee runs"),
                            onEdit = {},
                            onDelete = {},
                            onCancel = {},
                        )
                    }
                }
            },
            UiCatalogEntry("cat_edit", "Edit sheet") {
                Box(Modifier.fillMaxSize()) {
                    CategoryListScreen(previewCategoryList, {}, {})
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(CategoriesR.string.categories_edit_title),
                        onClose = {},
                    ) {
                        CategoryNewSheetContent(
                            form = CategoryNewFormState(name = "Coffee runs", selectedIconId = "coffee"),
                            onNameChange = {},
                            onIconSelected = {},
                            onAdd = {},
                            confirmLabel = stringResource(CategoriesR.string.categories_save),
                        )
                    }
                }
            },
        ),
    ),
    UiCatalogSection(
        id = "pin",
        title = "PIN & recovery",
        specRef = "14 · 15",
        entries = listOf(
            UiCatalogEntry("pin_entry", "Entry") { PinEntryScreen(previewPinEntry, {}, {}, {}, {}) },
            UiCatalogEntry("pin_wrong", "Entry · wrong PIN", edge = true) {
                PinEntryScreen(previewPinWrong, {}, {}, {}, {})
            },
            UiCatalogEntry("pin_lock", "Entry · locked out", edge = true) {
                PinEntryScreen(previewPinLock, {}, {}, {}, {})
            },
            UiCatalogEntry("pin_setup", "Setup") {
                PinSetupScreen(
                    state = previewPinSetup,
                    onTogglePin = {},
                    onToggleBiometric = {},
                    onRevealNew = {},
                    onRevealConfirm = {},
                    onRecoveryClick = {},
                    onSave = {},
                    onBack = {},
                )
            },
            UiCatalogEntry("pin_mismatch", "Set PIN · mismatch", edge = true) {
                PinSetPinScreen(
                    state = previewPinSetConfirmMismatch,
                    onDigit = {},
                    onBackspace = {},
                    onBack = {},
                )
            },
            UiCatalogEntry("pin_security", "Security question") {
                PinSecurityQuestionScreen(
                    questions = pinSecurityQuestions,
                    selectedId = "pet",
                    answer = "Biscuit",
                    onSelect = {},
                    onAnswerChange = {},
                    onEnable = {},
                    onBack = {},
                )
            },
            UiCatalogEntry("pin_recovery", "Recovery", edge = true) {
                PinRecoveryScreen(
                    questionText = stringResource(AuthR.string.security_question_pet),
                    answer = "Biscuit",
                    attemptsLabel = stringResource(AuthR.string.pin_recover_attempts, 2, 5),
                    onAnswerChange = {},
                    onVerify = {},
                    onBack = {},
                )
            },
        ),
    ),
)
