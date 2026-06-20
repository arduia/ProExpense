package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.theme.ProExpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                HomeShell(
                    state = previewHomeCasual,
                    selectedTab = HomeNavTab.Home,
                    onTabSelected = {},
                    onAddClick = {},
                )
            }
        }
    }
}
