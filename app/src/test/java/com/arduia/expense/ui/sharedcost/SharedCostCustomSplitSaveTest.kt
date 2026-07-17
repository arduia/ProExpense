package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
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
 * End-to-end guard: driving the real flow (new split -> total -> Custom -> edit a share via the
 * Edit-person sheet -> save) must hand the typed custom share off to onSaveSplit unchanged,
 * mirroring what a user actually does — not just what the isolated logic functions produce in a
 * unit test.
 *
 * Regression guard: the initial draft, `onNewSplit`, and `onKey`/`onBackspace` used to seed
 * `customShareRaws` (via `withParticipants()`) while `rawTotal` was still empty or mid-keystroke.
 * `syncCustomShares` only reseeds while the share list size differs from the participant count, so
 * that first premature seed (an equal share of $0) froze forever and never reflected the finished
 * total. Custom shares are now only seeded once, off the finished value, at `onConfirmAmount`.
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
class SharedCostCustomSplitSaveTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun customSplit_editedShareViaSheetIsPassedToOnSaveSplit() {
        var savedMode: SharedSplitMode? = null
        var savedShares: List<String>? = null

        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    onSaveSplit = { _, _, mode, _, customShareRaws, _ ->
                        savedMode = mode
                        savedShares = customShareRaws
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

        // Open the Edit-person sheet for the first participant and set their share via the
        // on-screen keypad — the first keystroke overwrites the pre-filled equal share ($60)
        // rather than appending onto it.
        rule.onNodeWithContentDescription("Edit split").performScrollTo().performClick()

        rule.onNodeWithText("Edit person").assertExists()
        // The name/amount editor and Done/Next buttons are pinned to the sheet's bottom (only
        // the roster above them scrolls), so no performScrollTo() is needed here.
        rule.onNodeWithContentDescription("Amount").performClick()
        rule.onNodeWithText("7").performClick()
        rule.onNodeWithText("5").performClick()
        rule.onNodeWithText("Done").performClick()

        rule.onNodeWithText("Review").performClick()
        rule.onNodeWithText("Save split", substring = true).performClick()

        assertEquals(SharedSplitMode.Custom, savedMode)
        assertEquals("75", savedShares?.getOrNull(0))
    }
}
