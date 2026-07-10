package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class ExpenseCategoryDirectionTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun category_picker_shows_expense_and_income_categories_together() {
        rule.setContent {
            ProExpenseTheme {
                QuickLogFlow(
                    onDismiss = {},
                    defaultCategories = listOf("food" to "Food", "salary" to "Salary"),
                    categoryTypes = mapOf("food" to RecordType.EXPENSE, "salary" to RecordType.INCOME),
                )
            }
        }

        // No expense/income tab — every category is visible at once, and direction is
        // decided purely by which one the user taps (US-LOG income).
        rule.onNodeWithText("Food").assertIsDisplayed()
        rule.onNodeWithText("Salary").assertIsDisplayed()
    }

    @Test
    fun selecting_an_income_category_relabels_the_screen_to_new_income() {
        rule.setContent {
            ProExpenseTheme {
                QuickLogFlow(
                    onDismiss = {},
                    defaultCategories = listOf("food" to "Food", "salary" to "Salary"),
                    categoryTypes = mapOf("food" to RecordType.EXPENSE, "salary" to RecordType.INCOME),
                )
            }
        }

        rule.onNodeWithText("New expense").assertIsDisplayed()

        rule.onNodeWithText("Salary").performClick()

        rule.onNodeWithText("New income").assertIsDisplayed()
    }
}
