package com.arduia.expense.ui.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Standalone entry point for visually verifying every screen + edge-case state. Registered only in
 * the `dev` flavor (see `app/src/dev/AndroidManifest.xml`) so production builds are untouched.
 */
class UiCatalogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProExpenseTheme {
                UiCatalogScreen()
            }
        }
    }
}
