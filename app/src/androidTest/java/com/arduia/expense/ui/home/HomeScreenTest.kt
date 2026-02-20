package com.arduia.expense.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.expenselogs.ExpenseUiModel
import com.arduia.expense.ui.home.compose.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_renders_income_outcome_and_graph() {
        val incomeOutcomeData = IncomeOutcomeUiModel(
            incomeValue = "60,000",
            outComeValue = "50,000",
            currencySymbol = "MMK",
            dateRange = "DEC 1 - DEC 7"
        )
        
        val graphData = WeeklyGraphUiModel("DEC 1 - DEC 7", mapOf(1 to 10))

        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = incomeOutcomeData,
                    graphData = graphData,
                    recentData = emptyList(),
                    onMenuClick = {},
                    onMoreLogsClick = {},
                    onRecentItemClick = {},
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("60,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("50,000").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("MMK").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("Expenses In This Week", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("recent", ignoreCase = true).assertDoesNotExist()
    }

    @Test
    fun homeScreen_renders_recent_list_when_not_empty() {
        val recentData = listOf(
            ExpenseUiModel(
                id = 1,
                name = "Lunch",
                date = "Dec 4",
                category = R.drawable.ic_food,
                amount = "10,000",
                currencySymbol = "MMK",
                finance = "OUT"
            )
        )

        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = null,
                    graphData = null,
                    recentData = recentData,
                    onMenuClick = {},
                    onMoreLogsClick = {},
                    onRecentItemClick = {},
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("recent", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunch").assertIsDisplayed()
        composeTestRule.onNodeWithText("10,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("more", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun homeScreen_menu_icon_clicks_triggers_callback() {
        var clicked = false
        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = null, graphData = null, recentData = emptyList(),
                    onMenuClick = { clicked = true }, onMoreLogsClick = {}, onRecentItemClick = {}, onAddClick = {}
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        assert(clicked)
    }

    @Test
    fun homeScreen_add_expense_fab_clicks_triggers_callback() {
        var clicked = false
        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = null, graphData = null, recentData = emptyList(),
                    onMenuClick = {}, onMoreLogsClick = {}, onRecentItemClick = {}, onAddClick = { clicked = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Add Expense").performClick()
        assert(clicked)
    }

    @Test
    fun homeScreen_more_logs_clicks_triggers_callback() {
        var clicked = false
        val recentData = listOf(ExpenseUiModel(1, "Lunch", "Dec 4", R.drawable.ic_food, "10,000", "MMK", "OUT"))
        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = null, graphData = null, recentData = recentData,
                    onMenuClick = {}, onMoreLogsClick = { clicked = true }, onRecentItemClick = {}, onAddClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("more", ignoreCase = true).performClick()
        assert(clicked)
    }

    @Test
    fun homeScreen_recent_item_clicks_triggers_callback() {
        var clickedItem: ExpenseUiModel? = null
        val expectedItem = ExpenseUiModel(1, "Lunch", "Dec 4", R.drawable.ic_food, "10,000", "MMK", "OUT")
        composeTestRule.setContent {
            ProExpenseTheme {
                HomeScreen(
                    incomeOutcomeData = null, graphData = null, recentData = listOf(expectedItem),
                    onMenuClick = {}, onMoreLogsClick = {}, onRecentItemClick = { clickedItem = it }, onAddClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Lunch").performClick()
        assert(clickedItem == expectedItem)
    }
}
