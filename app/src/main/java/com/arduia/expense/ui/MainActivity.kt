package com.arduia.expense.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.ExpenseApplication
import com.arduia.expense.ui.theme.ProExpenseTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as ExpenseApplication).ensureStarted()
        enableEdgeToEdge()
        // Every screen shows financial data (amounts, PIN entry) — block screenshots/screen
        // recording and hide content in the recents/app-switcher thumbnail (US-AUTH privacy).
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            ProExpenseTheme {
                ExpenseApp()
            }
        }
    }
}
