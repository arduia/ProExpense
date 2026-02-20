package com.arduia.expense.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.settings.molecule.SettingsEvent
import com.arduia.expense.ui.settings.molecule.SettingsState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyUIElementsAreRendered() {
        val state = SettingsState(currencyValue = "$")

        composeTestRule.setContent {
            ProExpenseTheme {
                SettingsScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Language").assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("$").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme Mode").assertIsDisplayed()
    }

    @Test
    fun verifyThemeDialogShowsWhenStateHasShowThemeDialogForMode() {
        val state = SettingsState(showThemeDialogForMode = 1) // 1 represents light mode usually

        composeTestRule.setContent {
            ProExpenseTheme {
                SettingsScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Choose Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Light Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("System Default").assertIsDisplayed()
    }

    @Test
    fun verifyClickingThemeRowEmitsChooseThemeClickedEvent() {
        var clicked = false
        val state = SettingsState()

        composeTestRule.setContent {
            ProExpenseTheme {
                SettingsScreen(state = state, onEvent = { event ->
                    if (event is SettingsEvent.ChooseThemeClicked) {
                        clicked = true
                    }
                })
            }
        }

        composeTestRule.onNodeWithText("Theme Mode").performClick()
        assertTrue(clicked)
    }
}
