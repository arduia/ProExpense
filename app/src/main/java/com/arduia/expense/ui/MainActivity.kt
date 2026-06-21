package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import com.arduia.expense.feature.auth.ui.PinSetupFlow
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.ui.debt.DebtFlow
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.events.EventsFlow
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
import com.arduia.expense.ui.reports.ReportsFlow
import com.arduia.expense.ui.splash.SplashScreen
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.util.Locale

private const val SPLASH_DURATION_MILLIS = 1800L

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                var onboardingComplete by rememberSaveable { mutableStateOf(false) }
                var showQuickLog by rememberSaveable { mutableStateOf(false) }
                var showSharedCosts by rememberSaveable { mutableStateOf(false) }
                var showDebt by rememberSaveable { mutableStateOf(false) }
                var showPinSetup by rememberSaveable { mutableStateOf(false) }
                var showReports by rememberSaveable { mutableStateOf(false) }
                var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }
                var userName by rememberSaveable { mutableStateOf("") }
                var loggedItems by remember { mutableStateOf<List<HomeTransactionItem>>(emptyList()) }
                var loggedTotal by remember { mutableStateOf(0.0) }

                val noteFallback = stringResource(R.string.home_logged_note_fallback)
                val todaySection = stringResource(R.string.home_today_section)

                val homeState = if (loggedItems.isEmpty()) {
                    previewHomeEmpty.copy(
                        greetingName = userName.ifBlank { previewHomeEmpty.greetingName },
                    )
                } else {
                    val totalLabel = "$" + AmountInput.formatDisplay(
                        String.format(Locale.US, "%.2f", loggedTotal),
                    )
                    previewHomeEmpty.copy(
                        greetingName = userName.ifBlank { previewHomeEmpty.greetingName },
                        monthSpend = totalLabel,
                        showEmptyHint = false,
                        dayGroups = listOf(
                            HomeDayGroup(
                                dayTitle = todaySection,
                                dayTotal = totalLabel,
                                transactions = loggedItems,
                            ),
                        ),
                    )
                }

                val onExpenseSaved: (ExpenseEntryState) -> Unit = { entry ->
                    loggedTotal += AmountInput.numericValue(entry.rawAmount) ?: 0.0
                    loggedItems = listOf(
                        HomeTransactionItem(
                            categoryId = entry.selectedCategoryId,
                            note = entry.note.trim().ifEmpty { noteFallback },
                            meta = entry.timeLabel,
                            amount = "$" + AmountInput.formatDisplay(entry.rawAmount),
                            tag = entry.linkedTagLabel,
                        ),
                    ) + loggedItems
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
                    kotlinx.coroutines.delay(SPLASH_DURATION_MILLIS)
                    showSplash = false
                }

                Box(Modifier.fillMaxSize()) {
                    if (showSplash) {
                        SplashScreen()
                    } else {
                    if (onboardingComplete) {
                        if (selectedTab == HomeNavTab.Budget) {
                            EventsFlow(
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                            )
                        } else if (selectedTab == HomeNavTab.Journal) {
                            JournalFlow(
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                            )
                        } else if (selectedTab == HomeNavTab.More) {
                            MoreFlow(
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                onDebtClick = { showDebt = true },
                                onSharedClick = { showSharedCosts = true },
                                onPinClick = { showPinSetup = true },
                            )
                        } else {
                            HomeShell(
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
                        FirstLaunchFlow(
                            onComplete = { name, _ ->
                                userName = name
                                onboardingComplete = true
                            },
                        )
                    }

                    if (showQuickLog) {
                        QuickLogFlow(
                            onDismiss = { showQuickLog = false },
                            onSaved = onExpenseSaved,
                        )
                    }

                    if (showSharedCosts) {
                        SharedCostsFlow(
                            onDismiss = { showSharedCosts = false },
                        )
                    }

                    if (showDebt) {
                        DebtFlow(
                            onDismiss = { showDebt = false },
                        )
                    }

                    if (showPinSetup) {
                        PinSetupFlow(
                            onDismiss = { showPinSetup = false },
                        )
                    }

                    if (showReports) {
                        ReportsFlow(
                            onBack = { showReports = false },
                            empty = loggedItems.isEmpty(),
                            onLogFirstExpense = {
                                showReports = false
                                showQuickLog = true
                            },
                        )
                    }
                    }
                }
            }
        }
    }
}
