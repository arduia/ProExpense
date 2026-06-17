package com.arduia.expense.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class ProfileCurrencyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileCurrency_showsSpecCopyAndSelection() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    ProfileCurrencyScreen(
                        selectedCode = "USD",
                        onCurrencySelect = {},
                        onSkip = {},
                        onStartTracking = {},
                    )
                }
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("PROFILE · 2 OF 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pick your home currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start tracking").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Step 2 of 2").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("USD US Dollar").assertIsSelected()
    }

    @Test
    fun profileCurrency_selectingRowInvokesCallback() {
        var selected: String? = null
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    ProfileCurrencyScreen(
                        selectedCode = "USD",
                        onCurrencySelect = { selected = it.code },
                        onSkip = {},
                        onStartTracking = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("EUR Euro").performClick()

        assertEquals("EUR", selected)
    }
}
