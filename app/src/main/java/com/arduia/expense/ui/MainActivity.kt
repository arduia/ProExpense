package com.arduia.expense.ui

import android.content.Context
import android.content.res.Configuration
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
import com.arduia.expense.BuildConfig
import com.arduia.expense.ExpenseApplication
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.storage.repository.peekLanguageTag
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.koin.compose.koinInject
import java.util.Locale

class MainActivity : FragmentActivity() {
    // MainActivity is a plain FragmentActivity, not AppCompatActivity, so AppCompatDelegate's
    // per-app-language API won't auto-recreate it with the stored locale — wrapping the base
    // Context here (the same technique the OS itself uses) is the reliable, API-24+-safe fix.
    // Reads SharedPreferences directly (not through Koin/the suspend repository) since this runs
    // before ExpenseApplication.ensureStarted() / DI is available.
    override fun attachBaseContext(newBase: Context) {
        val languageTag = peekLanguageTag(newBase) ?: AppLanguage.DEFAULT.tag
        super.attachBaseContext(newBase.withLocale(languageTag))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as ExpenseApplication).ensureStarted()
        enableEdgeToEdge()
        // Every screen shows financial data (amounts, PIN entry) — block screenshots/screen
        // recording and hide content in the recents/app-switcher thumbnail (US-AUTH privacy).
        // Disabled on the dev flavor so screenshots/screen recording work during development.
        if (!BuildConfig.FLAVOR.equals("dev", ignoreCase = true)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        setContent {
            ThemedExpenseApp(onLanguageChanged = { recreate() })
        }
    }
}

private fun Context.withLocale(languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

/**
 * Resolves the user's Light/Dark/System theme choice here, above [ExpenseApp] — the picker lives
 * deep inside More, but `darkTheme` has to be decided at the [ProExpenseTheme] root that wraps
 * the whole tree (US-MORE-3: applies immediately, no restart).
 */
@Composable
private fun ThemedExpenseApp(onLanguageChanged: () -> Unit) {
    val themeRepository: ThemeRepository = koinInject()
    // System is the product default (US-MORE-3) — seed the pre-load placeholder with it too, so
    // a fresh install's first frame already follows the OS before the persisted value resolves.
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    LaunchedEffect(Unit) {
        when (val result = themeRepository.getThemeMode()) {
            is Result.Success -> themeMode = result.data
            is Result.Error -> Unit
        }
    }

    val darkTheme =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

    ProExpenseTheme(darkTheme = darkTheme) {
        ExpenseApp(
            onThemeModeChanged = { themeMode = it },
            // The language is already persisted by the caller before this fires (see MoreFlow /
            // FirstLaunchFlow) — attachBaseContext() re-reads it fresh on recreate().
            onLanguageChanged = onLanguageChanged,
        )
    }
}
