package com.arduia.expense.ui.statistics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.statistics.molecule.StatisticEvent
import com.arduia.expense.ui.statistics.molecule.StatisticState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class StatisticScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyEmptyStateIsRendered() {
        val state = StatisticState(isEmptyExpenseData = true)

        composeTestRule.setContent {
            ProExpenseTheme {
                StatisticScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Statistics").assertIsDisplayed()
        composeTestRule.onNodeWithText("No Data").assertIsDisplayed()
    }

    @Test
    fun verifyCategoryStatisticsAreRendered() {
        val mockData = listOf(
            CategoryStatisticUiModel(R.string.food, 0.75f, "75%")
        )
        val state = StatisticState(categoryStatisticList = mockData, isEmptyExpenseData = false, dateRangeInfo = "01 Aug - 31 Aug")

        composeTestRule.setContent {
            ProExpenseTheme {
                StatisticScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Statistics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("75%").assertIsDisplayed()
        composeTestRule.onNodeWithText("01 Aug - 31 Aug").assertIsDisplayed()
    }
}
