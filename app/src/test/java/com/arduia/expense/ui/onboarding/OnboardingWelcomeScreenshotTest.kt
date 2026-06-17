package com.arduia.expense.ui.onboarding

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
class OnboardingWelcomeScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingWelcome_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground {
                    OnboardingWelcomeScreen(
                        onSkip = {},
                        onBack = {},
                        onNext = {},
                        onGetStarted = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
