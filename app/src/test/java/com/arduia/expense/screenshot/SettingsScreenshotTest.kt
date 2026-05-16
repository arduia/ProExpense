package com.arduia.expense.screenshot

import android.app.Application
import com.arduia.expense.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class, qualifiers = "w360dp-h720dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SettingsScreenshotTest {

    @Test
    fun settingsLayout() {
        inflateLayout(R.layout.fragment_settings)
            .measureAndCapture("src/test/snapshots/settings/settings_layout.png")
    }

    @Test
    fun chooseLanguageLayout() {
        inflateLayout(R.layout.fragment_choose_language)
            .measureAndCapture("src/test/snapshots/settings/choose_language_layout.png")
    }

    @Test
    fun chooseCurrencyLayout() {
        inflateLayout(R.layout.fragment_choose_currency)
            .measureAndCapture("src/test/snapshots/settings/choose_currency_layout.png")
    }

    @Test
    fun chooseThemeDialogLayout() {
        inflateLayout(R.layout.choose_theme_dialog)
            .measureAndCapture("src/test/snapshots/settings/choose_theme_dialog.png", heightDp = 300)
    }
}
