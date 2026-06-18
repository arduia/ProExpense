package com.arduia.expense.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class MoreFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setFlow() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    MoreFlowScreen()
                }
            }
        }
    }

    @Test
    fun flow_openCurrencyThenBackReturnsToHub() {
        setFlow()

        composeTestRule.onNodeWithContentDescription("Currency").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Your home currency", substring = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Back to More").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("SETTINGS & EXTRAS").assertIsDisplayed()
    }

    @Test
    fun flow_currencySelectionPersistsToHub() {
        setFlow()

        composeTestRule.onNodeWithContentDescription("Currency").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("EUR Euro").performScrollTo().performClick()
        composeTestRule.onNodeWithContentDescription("Back to More").performClick()
        composeTestRule.waitForIdle()

        // Hub currency row now reflects the new selection.
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
    }

    @Test
    fun flow_openExportShowsExportScreen() {
        setFlow()

        composeTestRule.onNodeWithContentDescription("Export data").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Export your data").assertIsDisplayed()
        composeTestRule.onNodeWithText("Export as ZIP").assertIsDisplayed()
    }
}
