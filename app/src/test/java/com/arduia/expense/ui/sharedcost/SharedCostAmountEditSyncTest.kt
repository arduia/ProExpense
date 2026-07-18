package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: re-opening the confirmed total via its edit icon and typing a *different* value
 * used to leave Custom-mode per-person shares pointing at the old total — `onConfirmAmount`
 * called `withParticipants()`, whose `syncCustomShares` is a no-op once the share list already
 * matches the people count. Shares must now redistribute evenly whenever the total actually
 * changes, while an unmodified re-confirm must still preserve an intentionally-set split.
 *
 * Explicit device qualifiers matter here: the split-editor sheet is `fullHeight = true`, so its
 * forced height is a fraction of the real window height (see ProBottomSheet.kt) — Robolectric's
 * unqualified default window is only ~470dp tall (shorter than any real device this app targets),
 * which forces the numeric keypad into a much shorter sheet than production ever sees and breaks
 * the digit-click sequence below in ways unrelated to the behavior this test actually guards.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp")
@Category(ComposeUiTests::class)
class SharedCostAmountEditSyncTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun startCustomSplitWithEditedShare(
        savedTotal: (String) -> Unit,
        savedShares: (List<String>) -> Unit,
    ) {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    onSaveSplit = { _, rawTotal, _, _, customShareRaws, _ ->
                        savedTotal(rawTotal)
                        savedShares(customShareRaws)
                    },
                )
            }
        }

        rule.onNodeWithText("New split").performClick()
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("0").performClick()
        rule.onNodeWithText("Next").performClick()

        rule.onNodeWithText("Custom split").performScrollTo().performClick()

        rule.onNodeWithContentDescription("Edit split").performScrollTo().performClick()
        rule.onNodeWithContentDescription("Amount").performClick()
        rule.onNodeWithText("7").performClick()
        rule.onNodeWithText("5").performClick()
        rule.onNodeWithText("Done").performClick()
    }

    @Test
    fun changingTheTotal_redistributesCustomSharesEvenly() {
        var savedTotal: String? = null
        var savedShares: List<String>? = null
        startCustomSplitWithEditedShare({ savedTotal = it }, { savedShares = it })

        rule.onNodeWithContentDescription("Edit total amount").performScrollTo().performClick()
        repeat(3) { rule.onNodeWithContentDescription("Backspace").performClick() }
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("0").performClick()
        rule.onNodeWithText("0").performClick()
        rule.onNodeWithText("Next").performClick()

        rule.onNodeWithText("Review").performClick()
        rule.onNodeWithText("Save split", substring = true).performClick()

        assertEquals("200", savedTotal)
        assertEquals(listOf("100", "100"), savedShares)
    }

    @Test
    fun reconfirmingTheSameTotal_preservesCustomShares() {
        var savedTotal: String? = null
        var savedShares: List<String>? = null
        startCustomSplitWithEditedShare({ savedTotal = it }, { savedShares = it })

        rule.onNodeWithContentDescription("Edit total amount").performScrollTo().performClick()
        rule.onNodeWithText("Next").performClick()

        rule.onNodeWithText("Review").performClick()
        rule.onNodeWithText("Save split", substring = true).performClick()

        assertEquals("120", savedTotal)
        assertEquals(listOf("75", "60"), savedShares)
    }
}
