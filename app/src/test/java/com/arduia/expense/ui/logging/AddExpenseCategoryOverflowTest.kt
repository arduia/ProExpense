package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.feature.logging.ui.AddExpenseAmountScreen
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountManyCategories
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: the category chip area used to grow with the custom-category count inside the
 * screen's own single scrolling Column, pushing NumericKeypad's Save button below the visible
 * screen on a long custom-category list. The chip area is now a weighted, independently
 * scrollable region so the keypad stays pinned regardless of category count.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class AddExpenseCategoryOverflowTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun manyCustomCategories_saveButtonStaysVisible() {
        rule.setContent {
            ProExpenseTheme {
                AddExpenseAmountScreen(
                    state = previewExpenseAmountManyCategories,
                    onClose = {},
                    onKey = {},
                    onBackspace = {},
                    onCategorySelected = {},
                    onSave = {},
                    onNext = {},
                    customCategories = (1..20).map { "custom-$it" to "Custom category $it" },
                )
            }
        }

        rule.onNodeWithText("Save").assertIsDisplayed()
    }
}
