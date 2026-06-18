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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class MoreHubScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setHub(
        state: MoreHubState = MoreHubState(),
        onNavigate: (MoreDestination) -> Unit = {},
    ) {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    MoreHubScreen(
                        state = state,
                        onNavigate = onNavigate,
                        onTabSelect = {},
                        onAdd = {},
                    )
                }
            }
        }
    }

    @Test
    fun hub_showsHeaderProfileAndGroups() {
        setHub(state = MoreHubState(currencyCode = "USD"))

        composeTestRule.onNodeWithText("SETTINGS & EXTRAS").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maya").assertIsDisplayed()
        composeTestRule.onNodeWithText("All data local · no account").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Reports").assertIsDisplayed()
        // Currency detail value reflects the hub state.
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
    }

    @Test
    fun hub_tappingFeatureRowNavigates() {
        var destination: MoreDestination? = null
        setHub(onNavigate = { destination = it })

        composeTestRule.onNodeWithContentDescription("Reports").performClick()

        assertEquals(MoreDestination.Reports, destination)
    }

    @Test
    fun hub_tappingClearDataNavigates() {
        var destination: MoreDestination? = null
        setHub(onNavigate = { destination = it })

        composeTestRule.onNodeWithContentDescription("Clear data").performScrollTo().performClick()

        assertEquals(MoreDestination.ClearData, destination)
    }
}
