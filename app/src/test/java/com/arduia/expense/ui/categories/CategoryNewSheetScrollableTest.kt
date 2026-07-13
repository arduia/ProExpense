package com.arduia.expense.ui.categories

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.categories.ui.CategoryNewSheetContent
import com.arduia.expense.feature.categories.ui.preview.previewCategoryNewDuplicate
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: Add Category's content used to be one plain Column, wrapping every section
 * (including the type/name header and the colors/Add footer) to its full natural height with no
 * way to shrink — when the sheet's available room got tight (e.g. the keyboard eating a big chunk
 * of the screen for the full 20-icon grid), there was nothing to scroll, so the fixed sections
 * could get pushed off-screen entirely with no way to reach them. The icon grid is now the only
 * flexible section (weight(fill = false) + verticalScroll()); the header above it and the footer
 * below it stay fixed no matter how little room is left, only visible icon rows shrink.
 *
 * A directly height-constrained parent stands in for "not enough room" here rather than a real
 * keyboard/IME — Robolectric doesn't drive WindowInsets.ime, but from CategoryNewSheetContent's
 * own layout perspective, a bounded parent height is exactly the same shape of constraint a real
 * device applies once ProBottomSheet's own max-height budget (see ProBottomSheet.kt) shrinks for
 * an open keyboard.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class CategoryNewSheetScrollableTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun constrainedHeight_fixedHeaderAndFooterStayVisible_aroundTheFullIconGrid() {
        rule.setContent {
            ProExpenseTheme {
                // previewCategoryNewDuplicate carries the full 20-icon default list plus the
                // duplicate-name warning row — enough header+icon+footer content that it would
                // overflow this constrained height if nothing shrank.
                Box(Modifier.fillMaxWidth().height(350.dp)) {
                    CategoryNewSheetContent(
                        form = previewCategoryNewDuplicate,
                        onNameChange = {},
                        onIconSelected = {},
                        onAdd = {},
                    )
                }
            }
        }

        // Fixed header (top): the duplicate-name warning sits directly under the name field.
        rule.onNodeWithText("A category with this name already exists").assertIsDisplayed()
        // Fixed footer (bottom): color section label and the Add button.
        rule.onNodeWithText("Color").assertIsDisplayed()
        rule.onNodeWithText("Add category").assertIsDisplayed()
    }
}
