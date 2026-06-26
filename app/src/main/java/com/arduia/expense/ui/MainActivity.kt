package com.arduia.expense.ui

import android.os.Bundle
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
        setContent {
            ProExpenseTheme {
                ExpenseApp()
            }
        }
    }
}
