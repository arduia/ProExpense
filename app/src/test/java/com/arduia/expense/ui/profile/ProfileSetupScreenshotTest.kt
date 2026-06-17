package com.arduia.expense.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class ProfileSetupScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(content = content)
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun profileName_matchesDesignSpec() {
        capture {
            ProfileNameScreen(
                name = "Maya",
                onNameChange = {},
                onSkip = {},
                onContinue = {},
            )
        }
    }

    @Test
    fun profileCurrency_matchesDesignSpec() {
        capture {
            ProfileCurrencyScreen(
                selectedCode = "USD",
                onCurrencySelect = {},
                onSkip = {},
                onStartTracking = {},
            )
        }
    }

    @Test
    fun profileCurrencySheet_matchesDesignSpec() {
        capture {
            ProfileCurrencySheetScreen(
                query = "",
                onQueryChange = {},
                onClose = {},
                onCurrencySelect = {},
            )
        }
    }
}
