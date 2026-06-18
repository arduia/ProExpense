package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.onboarding.OnboardingScreen
import com.arduia.expense.ui.theme.ProExpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ProExpenseTheme.colors.surface,
                ) { innerPadding ->
                    OnboardingScreen(
                        onFinish = {},
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
