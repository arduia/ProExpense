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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.debt.DebtFlow
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.events.EventsFlow
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.journal.JournalFlow
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.more.MoreFlow
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
import com.arduia.expense.ui.pin.PinSetupFlow
import com.arduia.expense.ui.reports.ReportsFlow
import com.arduia.expense.ui.sharedcost.SharedCostsFlow
import com.arduia.expense.ui.splash.SplashScreen
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.theme.ProExpenseTheme

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
                                state = previewHomeEmpty,
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
                            onComplete = { _, _ -> onboardingComplete = true },
                        )
                    }

                    if (showQuickLog) {
                        QuickLogFlow(
                            onDismiss = { showQuickLog = false },
                            onSaved = { showQuickLog = false },
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
                            empty = true,
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
