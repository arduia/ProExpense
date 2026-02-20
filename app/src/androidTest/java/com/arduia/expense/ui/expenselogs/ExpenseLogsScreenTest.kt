package com.arduia.expense.ui.expenselogs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ExpenseLogsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyNormalStateRendersTitleAndMenuAndFilter() {
        val state = ExpenseLogsState(
            mode = ExpenseMode.NORMAL,
            isFilterEnabled = true
        )

        val pagingDataFlow = MutableStateFlow(PagingData.empty<ExpenseLogUiModel>())

        composeTestRule.setContent {
            ProExpenseTheme {
                val pagingItems = pagingDataFlow.collectAsLazyPagingItems()
                ExpenseLogsScreen(
                    state = state,
                    pagingItems = pagingItems,
                    selectedIds = emptySet(),
                    onEvent = {},
                    onItemClick = {},
                    onItemDeleteClick = {},
                    onItemSelectionToggle = {}
                )
            }
        }

        // Verify Title
        composeTestRule.onNodeWithText("Expense Logs").assertIsDisplayed()
        
        // Verify Menu Icon is shown
        composeTestRule.onNodeWithContentDescription("Menu").assertIsDisplayed()

        // Verify Filter Action is shown
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
    }

    @Test
    fun verifySelectionModeRendersSelectionCountAndDeleteIcon() {
        val state = ExpenseLogsState(
            mode = ExpenseMode.SELECTION,
            selectedCount = 3
        )

        val pagingDataFlow = MutableStateFlow(PagingData.empty<ExpenseLogUiModel>())

        composeTestRule.setContent {
            ProExpenseTheme {
                val pagingItems = pagingDataFlow.collectAsLazyPagingItems()
                ExpenseLogsScreen(
                    state = state,
                    pagingItems = pagingItems,
                    selectedIds = emptySet(),
                    onEvent = {},
                    onItemClick = {},
                    onItemDeleteClick = {},
                    onItemSelectionToggle = {}
                )
            }
        }

        // Verify Selection Count Title
        composeTestRule.onNodeWithText("3 Selected").assertIsDisplayed()

        // Verify Close Selection Icon
        composeTestRule.onNodeWithContentDescription("Close Selection").assertIsDisplayed()

        // Verify Delete Icon
        composeTestRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
        
        // Ensure standard Menu/Filter are NOT shown
        composeTestRule.onNodeWithContentDescription("Menu").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Filter").assertDoesNotExist()
    }
}
