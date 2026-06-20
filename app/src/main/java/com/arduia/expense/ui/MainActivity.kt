package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.theme.ProExpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                var onboardingComplete by rememberSaveable { mutableStateOf(false) }

                if (onboardingComplete) {
                    HomeShell(
                        state = previewHomeCasual,
                        selectedTab = HomeNavTab.Home,
                        onTabSelected = {},
                        onAddClick = {},
                    )
                } else {
                    FirstLaunchFlow(
                        onComplete = { _, _ -> onboardingComplete = true },
                    )
                }
            }
        }
    }
}
