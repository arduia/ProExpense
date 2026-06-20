package com.arduia.expense.ui.handoff

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.HandoffReferenceTests
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.auth.PinEntryMode
import com.arduia.expense.ui.auth.PinEntryScreenContent
import com.arduia.expense.ui.auth.PinSetupScreenContent
import com.arduia.expense.ui.auth.PinSetupStep
import com.arduia.expense.ui.categories.CategoryListScreenContent
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
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.preview.previewEventDetailTransactions
import com.arduia.expense.ui.preview.previewEventListActive
import com.arduia.expense.ui.preview.previewEventListEmpty
import com.arduia.expense.ui.preview.previewHomeBudget
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewHomeEvent
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.preview.previewJournalList
import com.arduia.expense.ui.preview.previewMoreHubState
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

/**
 * Visual regression against [design_handoff_pro_expense/reference-images] at the handoff
 * artboard (414×868 dp). Goldens are synced by [scripts/sync-handoff-reference-goldens.sh].
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
@Category(ScreenshotTests::class, HandoffReferenceTests::class)
class HandoffReferenceScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun captureHandoff(content: @androidx.compose.runtime.Composable () -> Unit) {
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

    @Test fun flow_04_splash() = captureHandoff { SplashScreen(onFinished = {}) }

    @Test fun flow_04_onb_1() = captureHandoff {
        OnboardingScreen(onGetStarted = {}, onSkip = {}, initialPage = 0)
    }

    @Test fun flow_04_onb_5() = captureHandoff {
        OnboardingScreen(onGetStarted = {}, onSkip = {}, initialPage = 4)
    }

    @Test fun flow_04_prof_name() = captureHandoff {
        ProfileNameScreen(onContinue = {}, onSkip = {})
    }

    @Test fun flow_04_prof_currency() = captureHandoff {
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

    @Test fun flow_05_pin_setup() = captureHandoff {
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

    @Test fun flow_05_pin_entry() = captureHandoff {
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

    @Test fun flow_01_home_empty() = captureHandoff {
        HomeScreenContent(
            state = previewHomeEmpty,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun flow_01_home_casual() = captureHandoff {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun flow_01_home_budget() = captureHandoff {
        HomeScreenContent(
            state = previewHomeBudget,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun flow_01_home_event() = captureHandoff {
        HomeScreenContent(
            state = previewHomeEvent,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }

    @Test fun flow_01_add_zero() = captureHandoff {
        AddAmountScreen(
            amount = "",
            selectedCategoryId = "food",
            onAmountChange = {},
            onCategorySelected = {},
            onClose = {},
            onSave = {},
            onNext = {},
            showCustomCategories = true,
        )
    }

    @Test fun flow_01_add_amount() = captureHandoff {
        AddAmountScreen(
            amount = "12.50",
            selectedCategoryId = "food",
            onAmountChange = {},
            onCategorySelected = {},
            onClose = {},
            onSave = {},
            onNext = {},
            showCustomCategories = true,
        )
    }

    @Test fun flow_01_add_details() = captureHandoff {
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

    @Test fun flow_02_journal_list() = captureHandoff {
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

    @Test fun flow_02_journal_detail() = captureHandoff {
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

    @Test fun flow_03_more_hub() = captureHandoff {
        MoreHubScreenContent(
            state = previewMoreHubState,
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

    @Test fun flow_03_reports() = captureHandoff {
        ReportsScreenContent(
            monthLabel = "May 2025",
            totalSpend = "$80.90",
            categories = previewReportsCategories,
            onBack = {},
        )
    }

    @Test fun flow_03_categories() = captureHandoff {
        CategoryListScreenContent(
            state = previewCategoryHandoffState,
            onBack = {},
            onAddClick = {},
            onCreateCategoryClick = {},
        )
    }

    @Test fun flow_03_more_export() = captureHandoff {
        DataExportScreenContent(
            exportPassword = "",
            onExportPasswordChange = {},
            onBack = {},
            onExport = {},
        )
    }

    @Test fun flow_03_more_clear() = captureHandoff {
        ClearDataScreenContent(
            showConfirmStep = false,
            onRequestClear = {},
            onCancelConfirm = {},
            onBack = {},
            onConfirmClear = {},
        )
    }

    @Test fun flow_06_event_list() = captureHandoff {
        EventListScreenContent(events = previewEventListActive, onNewEvent = {}, onEventClick = {})
    }

    @Test fun flow_06_event_empty() = captureHandoff {
        EventListScreenContent(events = previewEventListEmpty, onNewEvent = {}, onEventClick = {})
    }

    @Test fun flow_06_event_create() = captureHandoff {
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

    @Test fun flow_06_event_detail() = captureHandoff {
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

    @Test fun flow_07_debt_lent() = captureHandoff {
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

    @Test fun flow_07_debt_add() = captureHandoff {
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

    @Test fun flow_07_debt_lent_detail() = captureHandoff {
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

    @Test fun flow_08_shared_input() = captureHandoff {
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

    @Test fun flow_08_shared_summary() = captureHandoff {
        SharedSummaryScreenContent(
            people = listOf("Alex" to "$30.00", "Jordan" to "$30.00"),
            onBack = {},
            onSave = {},
        )
    }

    @Test fun flow_08_shared_history() = captureHandoff {
        SharedHistoryScreenContent(items = previewSharedHistory, onBack = {})
    }
}
