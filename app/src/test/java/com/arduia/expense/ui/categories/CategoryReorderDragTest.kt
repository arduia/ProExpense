package com.arduia.expense.ui.categories

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import com.arduia.expense.feature.categories.ui.CategoryListScreen
import com.arduia.expense.feature.categories.ui.dragHandleTestTag
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Regression guard: the category drag handle's `pointerInput` was keyed on `rows.size`, which
 * never changes when items are reordered (only their order does). Because `key(categoryId)`
 * keeps a row's composable node alive across reorders, that pointerInput coroutine was never
 * relaunched after the first drag, so its `onDragStart` closure kept resetting `draggingIndex`
 * to the row's *original* index on every later drag. Re-grabbing an already-moved row therefore
 * started dragging whichever row now sat in that stale slot (typically the row below) instead of
 * the one under the finger — reported as "the item below moves instead of the selected one."
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class CategoryReorderDragTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun regrabbingAMovedRow_dragsItFromItsCurrentPosition_notItsOriginalOne() {
        val reorderCalls = mutableListOf<Pair<Int, Int>>()
        val firstRowId = previewCategoryList.categories.first().categoryId

        composeTestRule.setContent {
            ProExpenseTheme {
                var rows by remember { mutableStateOf(previewCategoryList.categories) }
                CategoryListScreen(
                    state = previewCategoryList.copy(categories = rows),
                    onBack = {},
                    onCreate = {},
                    onReorder = { from, to ->
                        reorderCalls += from to to
                        rows = rows.toMutableList().apply { add(to, removeAt(from)) }
                    },
                )
            }
        }

        dragHandleDownOneRow(firstRowId)
        composeTestRule.waitForIdle()
        dragHandleDownOneRow(firstRowId)
        composeTestRule.waitForIdle()

        assertEquals("first drag should move the row from index 0 to 1", 0 to 1, reorderCalls[0])
        assertEquals(
            "second drag must start from the row's current index (1), not its stale original index (0)",
            1 to 2,
            reorderCalls[1],
        )
    }

    private fun dragHandleDownOneRow(categoryId: String) {
        composeTestRule.onNodeWithTag(dragHandleTestTag(categoryId)).performTouchInput {
            down(center)
            advanceEventTime(700)
            moveBy(Offset(0f, 500f))
            up()
        }
    }
}
