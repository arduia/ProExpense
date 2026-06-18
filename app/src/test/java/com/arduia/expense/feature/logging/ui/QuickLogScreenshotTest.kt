package com.arduia.expense.feature.logging.ui

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
class QuickLogScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addAmount_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                AddAmountScreen(
                    amountInput = "12.50",
                    currencyCode = "USD",
                    currencySymbol = "$",
                    selectedCategoryId = "food",
                    showZeroError = false,
                    canProceed = true,
                    onKey = {},
                    onCategorySelected = {},
                    onCancel = {},
                    onNext = {},
                    onQuickSave = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
