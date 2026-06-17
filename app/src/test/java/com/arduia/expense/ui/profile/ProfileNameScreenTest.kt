package com.arduia.expense.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class ProfileNameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileName_showsSpecCopy() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    ProfileNameScreen(
                        name = "Maya",
                        onNameChange = {},
                        onSkip = {},
                        onContinue = {},
                    )
                }
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("PROFILE · 1 OF 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set up your profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("PROFILE NAME").assertIsDisplayed()
        composeTestRule.onNodeWithText("Used on your home screen and CSV exports.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Step 1 of 2").assertIsDisplayed()
    }

    @Test
    fun profileName_continueInvokesCallback() {
        var continued = false
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    ProfileNameScreen(
                        name = "Maya",
                        onNameChange = {},
                        onSkip = {},
                        onContinue = { continued = true },
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Continue").performClick()

        assertTrue(continued)
    }
}
