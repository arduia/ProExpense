package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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
class FirstLaunchScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splash_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    SplashScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun onboardingWelcome_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        modifier = Modifier.fillMaxSize(),
                        currentPage = 0,
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

    @Test
    fun onboardingQuickLog_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        modifier = Modifier.fillMaxSize(),
                        currentPage = 1,
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

    @Test
    fun onboardingSharedCosts_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        modifier = Modifier.fillMaxSize(),
                        currentPage = 2,
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

    @Test
    fun onboardingEventBudget_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        modifier = Modifier.fillMaxSize(),
                        currentPage = 3,
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

    @Test
    fun onboardingJournal_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    OnboardingScreen(
                        modifier = Modifier.fillMaxSize(),
                        currentPage = 4,
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
