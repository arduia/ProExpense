package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.debt.DebtFlow
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.events.EventsFlow
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.logging.QuickLogFlow
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
import com.arduia.expense.ui.sharedcost.SharedCostsFlow
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.theme.ProExpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                var onboardingComplete by rememberSaveable { mutableStateOf(false) }
                var showQuickLog by rememberSaveable { mutableStateOf(false) }
                var showSharedCosts by rememberSaveable { mutableStateOf(false) }
                var showDebt by rememberSaveable { mutableStateOf(false) }
                var selectedTab by rememberSaveable { mutableStateOf(HomeNavTab.Home) }

                val onTabSelected: (HomeNavTab) -> Unit = { tab ->
                    if (tab == HomeNavTab.Home || tab == HomeNavTab.Budget) {
                        selectedTab = tab
                    }
                }

                Box(Modifier.fillMaxSize()) {
                    if (onboardingComplete) {
                        if (selectedTab == HomeNavTab.Budget) {
                            EventsFlow(
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                            )
                        } else {
                            HomeShell(
                                state = previewHomeCasual,
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected,
                                onAddClick = { showQuickLog = true },
                                onDebtClick = { showDebt = true },
                                onSplitClick = { showSharedCosts = true },
                                onEventsClick = { selectedTab = HomeNavTab.Budget },
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
                }
            }
        }
    }
}
