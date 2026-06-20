package com.arduia.expense.ui.handoff

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.testAppGraph
import com.arduia.expense.ui.ExpenseApp
import com.arduia.expense.ui.auth.PinEntryMode
import com.arduia.expense.ui.auth.PinEntryScreenContent
import com.arduia.expense.ui.auth.PinSetupScreenContent
import com.arduia.expense.ui.auth.PinSetupStep
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.budget.previewEvents
import com.arduia.expense.ui.categories.CategoryListScreenContent
import com.arduia.expense.ui.currency.CurrencySettingScreenContent
import com.arduia.expense.ui.currency.ProfileCurrencyScreenContent
import com.arduia.expense.ui.data.ClearDataScreenContent
import com.arduia.expense.ui.data.DataExportScreenContent
import com.arduia.expense.ui.debt.DebtAddScreenContent
import com.arduia.expense.ui.debt.DebtDetailScreenContent
import com.arduia.expense.ui.debt.DebtTrackerScreenContent
import com.arduia.expense.ui.events.EventCreateScreenContent
import com.arduia.expense.ui.events.EventDetailScreenContent
import com.arduia.expense.ui.events.EventListScreenContent
import com.arduia.expense.ui.home.HomeScreenContent
import com.arduia.expense.ui.journal.JournalDetailScreenContent
import com.arduia.expense.ui.journal.JournalScreenContent
import com.arduia.expense.ui.logging.AddAmountScreen
import com.arduia.expense.ui.logging.AddDetailsScreen
import com.arduia.expense.ui.more.MoreHubScreenContent
import com.arduia.expense.ui.onboarding.OnboardingScreen
import com.arduia.expense.ui.onboarding.ProfileNameScreen
import com.arduia.expense.ui.onboarding.SplashScreen
import com.arduia.expense.ui.preview.previewCategoryHandoffState
import com.arduia.expense.ui.preview.previewMoreHubState
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.preview.previewEventDetailTransactions
import com.arduia.expense.ui.preview.previewEventListActive
import com.arduia.expense.ui.preview.previewEventListEmpty
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.preview.previewJournalList
import com.arduia.expense.ui.preview.previewReportsCategories
import com.arduia.expense.ui.preview.previewSharedHistory
import com.arduia.expense.ui.reports.ReportsScreenContent
import com.arduia.expense.ui.shared.SharedCostsScreenContent
import com.arduia.expense.ui.shared.SharedHistoryScreenContent
import com.arduia.expense.ui.shared.SharedSummaryScreenContent
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w427dp-h952dp")
@Category(ScreenshotTests::class)
class HandoffScreensScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @androidx.compose.runtime.Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test fun splash_default() = capture { SplashScreen(onFinished = {}) }

    @Test fun onboarding_welcome() = capture { OnboardingScreen(onGetStarted = {}, onSkip = {}, initialPage = 0) }

    @Test fun profile_name_default() = capture { ProfileNameScreen(onContinue = {}, onSkip = {}) }

    @Test fun profile_currency_default() = capture {
        ProfileCurrencyScreenContent(
            selectedCode = "USD",
            showPicker = false,
            onOpenPicker = {},
            onClosePicker = {},
            onCurrencySelected = {},
            onContinue = {},
            onSkip = {},
        )
    }

    @Test fun pin_setup() = capture {
        PinSetupScreenContent(
            step = PinSetupStep.Create,
            filledDots = 3,
            mismatchError = false,
            securityQuestions = listOf("pet" to "What was your first pet's name?"),
            selectedQuestionId = "pet",
            onSecurityQuestionSelected = {},
            securityAnswer = "",
            onSecurityAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onContinueSecurity = {},
        )
    }

    @Test fun pin_entry() = capture {
        PinEntryScreenContent(
            mode = PinEntryMode.Entry,
            filledDots = 4,
            lockoutSeconds = 0,
            recoveryAnswer = "",
            onRecoveryAnswerChange = {},
            onDigit = {},
            onBackspace = {},
            onForgotPin = {},
            onUnlockRecovery = {},
        )
    }

    @Test fun home_empty() = capture {
        HomeScreenContent(
            state = previewHomeEmpty,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun home_casual() = capture {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun expense_app_home() = capture { ExpenseApp(appGraph = testAppGraph()) }

    @Test fun add_amount_zero() = capture {
        AddAmountScreen(
            amount = "",
            selectedCategoryId = "food",
            onAmountChange = {},
            onCategorySelected = {},
            onClose = {},
            onSave = {},
            onNext = {},
        )
    }

    @Test fun add_details_tagged() = capture {
        AddDetailsScreen(
            amountLabel = "$12.50",
            selectedCategoryId = "food",
            note = "Lunch with M.",
            dateLabel = "Today, May 25",
            timeLabel = "12:30 PM",
            eventTag = "Bali Trip",
            onNoteChange = {},
            onCategorySelected = {},
            onBack = {},
            onEditAmount = {},
            onClearTag = {},
            onSave = {},
        )
    }

    @Test fun journal_list() = capture {
        JournalScreenContent(
            searchQuery = "",
            onSearchChange = {},
            filters = previewJournalFilters,
            selectedFilter = "All",
            onFilterSelected = {},
            dayGroups = previewJournalList,
            onTransactionClick = {},
        )
    }

    @Test fun journal_detail() = capture {
        JournalDetailScreenContent(
            categoryId = "entertainment",
            note = "Movie · Dune",
            meta = "Entertainment · May 25 · 08:10 PM",
            amount = "$18.00",
            eventTag = "Bali Trip",
            showActionsSheet = false,
            onBack = {},
            onMore = {},
            onDismissActions = {},
            onEdit = {},
            onDelete = {},
        )
    }

    @Test fun more_hub() = capture {
        MoreHubScreenContent(
            state = previewMoreHubState.copy(showBottomNav = false),
            onReportsClick = {},
            onDebtClick = {},
            onSharedCostsClick = {},
            onCategoriesClick = {},
            onCurrencyClick = {},
            onBudgetClick = {},
            onSecurityClick = {},
            onLanguageClick = {},
            onExportClick = {},
            onImportClick = {},
            onClearClick = {},
            onAddClick = {},
            onTabSelected = {},
        )
    }

    @Test fun reports_monthly() = capture {
        ReportsScreenContent(
            monthLabel = "May 2025",
            totalSpend = "$80.90",
            categories = previewReportsCategories,
            onBack = {},
        )
    }

    @Test fun categories_list() = capture {
        CategoryListScreenContent(
            state = previewCategoryHandoffState,
            onBack = {},
            onAddClick = {},
            onCreateCategoryClick = {},
        )
    }

    @Test fun currency_setting() = capture {
        CurrencySettingScreenContent(
            selectedCode = "USD",
            showPicker = false,
            onOpenPicker = {},
            onClosePicker = {},
            onCurrencySelected = {},
            onBack = {},
        )
    }

    @Test fun data_export() = capture {
        DataExportScreenContent(
            exportPassword = "",
            onExportPasswordChange = {},
            onBack = {},
            onExport = {},
        )
    }

    @Test fun clear_data() = capture {
        ClearDataScreenContent(
            showConfirmStep = false,
            onRequestClear = {},
            onCancelConfirm = {},
            onBack = {},
            onConfirmClear = {},
        )
    }

    @Test fun event_list_active() = capture {
        EventListScreenContent(events = previewEventListActive, onNewEvent = {}, onEventClick = {})
    }

    @Test fun event_list_empty() = capture {
        EventListScreenContent(events = previewEventListEmpty, onNewEvent = {}, onEventClick = {})
    }

    @Test fun budget_events_list() = capture {
        BudgetScreenContent(events = previewEvents, onNewEvent = {}, onEventClick = {})
    }

    @Test fun event_create() = capture {
        EventCreateScreenContent(
            name = "Bali Trip",
            onNameChange = {},
            dateRange = "May 12 — May 26",
            amountText = "2000",
            onAmountChange = {},
            showErrors = false,
            onBack = {},
            onSave = {},
        )
    }

    @Test fun event_detail() = capture {
        EventDetailScreenContent(
            title = "Bali Trip",
            dateRange = "May 12 — May 26",
            spentLabel = "$1,240",
            budgetLabel = "of $2,000",
            progress = 0.62f,
            isClosed = false,
            transactions = previewEventDetailTransactions,
            onBack = {},
        )
    }

    @Test fun debt_tracker_lent() = capture {
        DebtTrackerScreenContent(
            selectedTab = 0,
            lentRecords = previewDebtLent,
            oweRecords = previewDebtOwe,
            onTabSelected = {},
            onAddClick = {},
            onRecordClick = {},
            onBack = {},
        )
    }

    @Test fun debt_add() = capture {
        DebtAddScreenContent(
            personName = "John",
            onPersonNameChange = {},
            amountText = "50.00",
            onAmountChange = {},
            dueDate = "Due in 30 days",
            saveEnabled = true,
            onClose = {},
            onSave = {},
        )
    }

    @Test fun debt_detail_lent() = capture {
        DebtDetailScreenContent(
            personName = "John",
            isLent = true,
            amount = "$50.00",
            dueLabel = "Due in 30 days",
            isSettled = false,
            showDeleteConfirm = false,
            onBack = {},
            onMarkSettled = {},
            onRequestDelete = {},
            onCancelDelete = {},
            onConfirmDelete = {},
        )
    }

    @Test fun shared_input() = capture {
        SharedCostsScreenContent(
            amountText = "120.00",
            onAmountChange = {},
            peopleCount = 4,
            showZeroValidation = false,
            onIncrementPeople = {},
            onDecrementPeople = {},
            onCalculate = {},
            onBack = {},
        )
    }

    @Test fun shared_summary() = capture {
        SharedSummaryScreenContent(
            people = listOf("Alex" to "$30.00", "Jordan" to "$30.00"),
            onBack = {},
            onSave = {},
        )
    }

    @Test fun shared_history() = capture {
        SharedHistoryScreenContent(items = previewSharedHistory, onBack = {})
    }
}
