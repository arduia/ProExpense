package com.arduia.expense.ui.entry

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.entry.molecule.ExpenseEntryState
import com.arduia.expense.ui.entry.molecule.ExpenseEntryEvent
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class ExpenseEntryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val foodCategory = ExpenseCategory(ExpenseCategory.FOOD, R.string.food, R.drawable.ic_food)
    private val transportCategory = ExpenseCategory(ExpenseCategory.TRANSPORTATION, R.string.transportation, R.drawable.ic_transportation)

    @Test
    fun verifyInsertModeShowsSaveButtonAndRepeatSwitch() {
        val state = ExpenseEntryState(
            mode = ExpenseEntryMode.INSERT,
            categories = listOf(foodCategory, transportCategory),
            selectedCategory = foodCategory
        )

        composeTestRule.setContent {
            ProExpenseTheme {
                ExpenseEntryScreen(state = state, onEvent = {})
            }
        }

        // Title
        composeTestRule.onNodeWithText("Expense Entry").assertIsDisplayed()
        // Save button
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        // Repeat switch
        composeTestRule.onNodeWithText("Repeat Entry").assertIsDisplayed()
        // Toolbar actions
        composeTestRule.onNodeWithContentDescription("Select Date").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Select Time").assertIsDisplayed()
    }

    @Test
    fun verifyUpdateModeShowsUpdateButtonAndHidesRepeatSwitch() {
        val state = ExpenseEntryState(
            mode = ExpenseEntryMode.UPDATE,
            name = "Coffee",
            amount = "3.50",
            categories = listOf(foodCategory),
            selectedCategory = foodCategory
        )

        composeTestRule.setContent {
            ProExpenseTheme {
                ExpenseEntryScreen(state = state, onEvent = {})
            }
        }

        // Title
        composeTestRule.onNodeWithText("Update Data").assertIsDisplayed()
        // Update button
        composeTestRule.onNodeWithText("Update").assertIsDisplayed()
        // Repeat switch should NOT be shown
        composeTestRule.onNodeWithText("Repeat Entry").assertDoesNotExist()
    }

    @Test
    fun verifyAmountErrorIsDisplayed() {
        val state = ExpenseEntryState(
            amountError = "Empty Cost",
            categories = listOf(foodCategory),
            selectedCategory = foodCategory
        )

        composeTestRule.setContent {
            ProExpenseTheme {
                ExpenseEntryScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Empty Cost").assertIsDisplayed()
    }

    @Test
    fun verifyClickingCategoryEmitsEvent() {
        var emittedEvent: ExpenseEntryEvent? = null
        val state = ExpenseEntryState(
            categories = listOf(foodCategory, transportCategory),
            selectedCategory = foodCategory
        )

        composeTestRule.setContent {
            ProExpenseTheme {
                ExpenseEntryScreen(state = state, onEvent = { emittedEvent = it })
            }
        }

        // The transport category name is a string resource, let's look up its value or use a test tag.
        // We can just find the text node for "Transportation" (assuming English locale for tests).
        // Since we don't have the context here to resolve stringResource without a composable, 
        // we can assert on the node with the known structure, or just use the Activity context if available.
        // Let's use `onNodeWithText` assuming the test runs in English.
        // Actually, let's use a simpler approach: add a testTag to the CategoryChip or find by text.
        // `androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.transportation)`
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val transportText = context.getString(R.string.transportation)
        
        composeTestRule.onNodeWithText(transportText).performClick()

        assert(emittedEvent is ExpenseEntryEvent.CategorySelected)
        assert((emittedEvent as ExpenseEntryEvent.CategorySelected).category.id == ExpenseCategory.TRANSPORTATION)
    }
}
