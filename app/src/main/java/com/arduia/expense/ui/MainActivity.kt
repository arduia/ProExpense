package com.arduia.expense.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
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
                    var showHome by remember { mutableStateOf(false) }
                    var profileLabel by remember { mutableStateOf("") }

                    if (showHome) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Home — welcome, $profileLabel!",
                                style = ProExpenseTheme.typography.body,
                                color = ProExpenseTheme.colors.onSurface,
                            )
                        }
                    } else {
                        FirstLaunchFlow(
                            onComplete = { profile ->
                                profileLabel = profile.name
                                showHome = true
                            },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
