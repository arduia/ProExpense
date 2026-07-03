package com.arduia.expense.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import com.arduia.expense.BuildConfig
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.ExpenseApplication
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.koin.compose.koinInject
import java.util.Locale

class MainActivity : FragmentActivity() {

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
    val localeRepository: LocaleRepository = koinInject()
    // Dark is the product default (US-MORE-3) — seed the pre-load placeholder with it too, so a
    // fresh install's first frame doesn't flash light before the persisted value resolves below.
    var themeMode by remember { mutableStateOf(ThemeMode.DARK) }
    var languageTag by remember { mutableStateOf(AppLanguage.DEFAULT.tag) }

    LaunchedEffect(Unit) {
        when (val result = themeRepository.getThemeMode()) {
            is Result.Success -> themeMode = result.data
            is Result.Error -> Unit
        }
        when (val result = localeRepository.getLanguageTag()) {
            is Result.Success -> if (result.data.isNotBlank()) languageTag = result.data
            is Result.Error -> Unit
        }
    }

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    ProExpenseTheme(darkTheme = darkTheme) {
        LocalizedContent(languageTag = languageTag) {
            ExpenseApp(
                onThemeModeChanged = { themeMode = it },
                onLanguageTagChanged = { languageTag = it },
            )
        }
    }
}

/**
 * Overrides string-resource resolution to [languageTag] for the whole composition — mirrors
 * ThemedExpenseApp's no-restart pattern for Theme (US-MORE-3 analog for language). MainActivity
 * is a plain FragmentActivity, not AppCompatActivity, so AppCompatDelegate's per-app-language API
 * won't auto-recreate it; overriding the Configuration Compose reads from is the same fix without
 * that dependency.
 */
@Composable
private fun LocalizedContent(languageTag: String, content: @Composable () -> Unit) {
    val baseContext = LocalContext.current
    val localizedContext = remember(languageTag, baseContext) {
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
    ) {
        content()
    }
}
