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
import com.arduia.expense.ui.design.HomeNavTab
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

                Box(Modifier.fillMaxSize()) {
                    if (onboardingComplete) {
                        HomeShell(
                            state = previewHomeCasual,
                            selectedTab = HomeNavTab.Home,
                            onTabSelected = {},
                            onAddClick = { showQuickLog = true },
                            onSplitClick = { showSharedCosts = true },
                        )
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
                }
            }
        }
    }
}
