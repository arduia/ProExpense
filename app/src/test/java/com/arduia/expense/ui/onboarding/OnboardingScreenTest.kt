package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeSlide_showsSpecCopy() {
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

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your personal finance notebook.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get started").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next ›").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Page 1 of 5, current").assertIsDisplayed()
    }

    @Test
    fun journalSlide_hidesSkipAndNext() {
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

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Journal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Review your spending like a diary, day by day.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get started").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").assertDoesNotExist()
    }
}
