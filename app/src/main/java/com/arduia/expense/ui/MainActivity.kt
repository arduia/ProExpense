package com.arduia.expense.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.ExpenseApplication
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.koin.compose.koinInject

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as ExpenseApplication).ensureStarted()
        enableEdgeToEdge()
        // Every screen shows financial data (amounts, PIN entry) — block screenshots/screen
        // recording and hide content in the recents/app-switcher thumbnail (US-AUTH privacy).
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            ThemedExpenseApp()
        }
    }
}

/**
 * Resolves the user's Light/Dark/System theme choice here, above [ExpenseApp] — the picker lives
 * deep inside More, but `darkTheme` has to be decided at the [ProExpenseTheme] root that wraps
 * the whole tree (US-MORE-3: applies immediately, no restart).
 */
@Composable
private fun ThemedExpenseApp() {
    val themeRepository: ThemeRepository = koinInject()
    // Dark is the product default (US-MORE-3) — seed the pre-load placeholder with it too, so a
    // fresh install's first frame doesn't flash light before the persisted value resolves below.
    var themeMode by remember { mutableStateOf(ThemeMode.DARK) }

    LaunchedEffect(Unit) {
        when (val result = themeRepository.getThemeMode()) {
            is Result.Success -> themeMode = result.data
            is Result.Error -> Unit
        }
    }

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    ProExpenseTheme(darkTheme = darkTheme) {
        ExpenseApp(onThemeModeChanged = { themeMode = it })
    }
}
