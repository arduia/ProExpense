package com.arduia.expense.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.onboarding.ui.ProfileSetupScreenContent
import com.arduia.expense.feature.onboarding.ui.ProfileSetupState
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.captureRoboImageWithTolerance
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ScreenshotTests::class)
class ProfileSetupScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @androidx.compose.runtime.Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImageWithTolerance()
    }

    @Test
    fun profile_merged() {
        capture {
            ProfileSetupScreenContent(
                state = ProfileSetupState(name = "Maya"),
                onNameChange = {},
                onStartTracking = {},
                onCurrencySelected = {},
                onOpenCurrencySheet = {},
                onCloseCurrencySheet = {},
                onCurrencySearchChange = {},
            )
        }
    }

    @Test
    fun profile_currency_sheet() {
        capture {
            ProfileSetupScreenContent(
                state =
                    ProfileSetupState(
                        name = "Maya",
                        showCurrencySheet = true,
                    ),
                onNameChange = {},
                onStartTracking = {},
                onCurrencySelected = {},
                onOpenCurrencySheet = {},
                onCloseCurrencySheet = {},
                onCurrencySearchChange = {},
            )
        }
    }
}
