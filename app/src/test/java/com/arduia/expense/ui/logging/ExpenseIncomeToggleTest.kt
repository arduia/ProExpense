package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class ExpenseIncomeToggleTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapping_income_segment_swaps_category_picker_to_income_categories() {
        rule.setContent {
            ProExpenseTheme {
                QuickLogFlow(
                    onDismiss = {},
                    defaultCategories = listOf("food" to "Food"),
                    defaultIncomeCategories = listOf("salary" to "Salary"),
                )
            }
        }

        rule.onNodeWithText("Food").assertIsDisplayed()

        rule.onNodeWithText("Income").performClick()

        rule.onNodeWithText("Salary").assertIsDisplayed()
    }
}
