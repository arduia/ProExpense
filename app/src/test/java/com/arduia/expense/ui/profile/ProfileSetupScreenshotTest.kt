package com.arduia.expense.ui.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Test
    fun profileName_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProfileNameScreen(
                    name = "Maya",
                    onNameChange = {},
                    onContinue = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun profileCurrency_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProfileCurrencyScreen(
                    selectedCode = "USD",
                    onCurrencySelected = {},
                    onStartTracking = {},
                    onShowAllCurrencies = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun profileCurrencySheet_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProfileCurrencySheetPreviewHost()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
