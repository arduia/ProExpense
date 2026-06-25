package com.arduia.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.timeLabel
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.splash.SplashScreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

private const val SPLASH_DURATION_MILLIS = 1800L

@Composable
fun ExpenseApp(
    features: FeatureUiRegistry = FeatureUiRegistry(),
    modifier: Modifier = Modifier,
    profileRepository: ProfileRepository = koinInject(),
    financeRecordRepository: FinanceRecordRepository = koinInject(),
    categoryRepository: CategoryRepository = koinInject(),
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var onboardingComplete by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var showQuickLog by rememberSaveable { mutableStateOf(false) }
    var showSharedCosts by rememberSaveable { mutableStateOf(false) }
    var showDebt by rememberSaveable { mutableStateOf(false) }
    var showPinSetup by rememberSaveable { mutableStateOf(false) }
    var showReports by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
    var userName by rememberSaveable { mutableStateOf("") }

    val records by financeRecordRepository.observeAll().collectAsState(emptyList())
    var categoryMap by remember { mutableStateOf<Map<String, Category>>(emptyMap()) }

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
        when (val result = profileRepository.isOnboardingComplete()) {
            is Result.Success -> {
                onboardingComplete = result.data
            }
            is Result.Error -> {
                onboardingComplete = false
            }
        }
    }

    val homeState = if (records.isEmpty()) {
        previewHomeEmpty.copy(
            greetingName = userName.ifBlank { previewHomeEmpty.greetingName },
            dateLabel = dateLabel,
            monthLabel = monthLabel,
        )
    } else {
        val totalCents = records.sumOf { it.money.amount.valueInCents }
        val totalLabel = "$" + AmountInput.formatDisplay(
            String.format(Locale.US, "%.2f", totalCents / 100.0),
        )
        val items = records.map { record ->
            HomeTransactionItem(
                categoryId = record.categoryId.value,
                note = record.note?.trim().orEmpty().ifEmpty { noteFallback },
                meta = timeLabel(record.recordedAtEpochMillis),
                amount = "$" + AmountInput.formatDisplay(
                    String.format(Locale.US, "%.2f", record.money.amount.valueInCents / 100.0),
                ),
                tag = null, // TODO: derive from record.link
            )
        }
        previewHomeEmpty.copy(
            greetingName = userName.ifBlank { previewHomeEmpty.greetingName },
            dateLabel = dateLabel,
            monthLabel = monthLabel,
            monthSpend = totalLabel,
            showEmptyHint = false,
            dayGroups = listOf(
                HomeDayGroup(
                    dayTitle = todaySection,
                    dayTotal = totalLabel,
                    transactions = items,
                ),
            ),
        )
    }

    val onExpenseSaved: (LoggedExpenseHandoff) -> Unit = { _ ->
        showQuickLog = false
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
        showSplash = false
    }

    Box(modifier.fillMaxSize()) {
        if (showSplash || onboardingComplete == null) {
            SplashScreen()
        } else {
            if (onboardingComplete == true) {
                when (selectedTab) {
                    HomeNavTab.Budget -> features.eventBudget.EventsTab(
                        onTabSelected = onTabSelected,
                        onAddClick = { showQuickLog = true },
                    )
                    HomeNavTab.Journal -> features.history.JournalTab(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = { showQuickLog = true },
                    )
                    HomeNavTab.More -> MoreFlow(
                        features = features,
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected,
                        onAddClick = { showQuickLog = true },
                        onDebtClick = { showDebt = true },
                        onSharedClick = { showSharedCosts = true },
                        onPinClick = { showPinSetup = true },
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
                    )
                }
            } else {
                features.onboarding.FirstLaunchFlow(
                    onComplete = { handoff ->
                        userName = handoff.profileName
                        onboardingComplete = true
                    },
                )
            }

            LaunchedEffect(onboardingComplete) {
                if (onboardingComplete == true && userName.isNotBlank()) {
                    profileRepository.setDisplayName(userName)
                    profileRepository.setOnboardingComplete()
                }
            }

            if (showQuickLog) {
                features.logging.QuickLogFlow(
                    onDismiss = { showQuickLog = false },
                    onSaved = onExpenseSaved,
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
                    empty = records.isEmpty(),
                    onLogFirstExpense = {
                        showReports = false
                        showQuickLog = true
                    },
                )
            }
        }
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
