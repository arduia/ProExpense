package com.arduia.expense.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w427dp-h952dp")
@Category(ScreenshotTests::class)
class OnboardingScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun captureOnboarding(page: Int) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    OnboardingScreen(
                        onGetStarted = {},
                        onSkip = {},
                        initialPage = page,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun onboarding_welcome() = captureOnboarding(0)

    @Test
    fun onboarding_quick_log() = captureOnboarding(1)

    @Test
    fun onboarding_shared_costs() = captureOnboarding(2)

    @Test
    fun onboarding_event_budget() = captureOnboarding(3)

    @Test
    fun onboarding_get_started() = captureOnboarding(4)

    @Test
    fun splash_default() {
        composeTestRule.setContent {
            ProExpenseTheme {
                SplashScreenContent()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun profile_name_default() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProfileNameScreen(
                    onContinue = {},
                    onSkip = {},
                    initialName = "Maya",
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
