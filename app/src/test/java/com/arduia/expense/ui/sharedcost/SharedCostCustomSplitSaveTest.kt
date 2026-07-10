package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * End-to-end guard: driving the real flow (new split -> total -> Custom -> edit a share -> save)
 * must hand the typed custom share off to onSaveSplit unchanged, mirroring what a user actually
 * does — not just what the isolated logic functions produce in a unit test.
 *
 * Regression guard: the initial draft, `onNewSplit`, and `onKey`/`onBackspace` used to seed
 * `customShareRaws` (via `withParticipants()`) while `rawTotal` was still empty or mid-keystroke.
 * `syncCustomShares` only reseeds while the share list size differs from the participant count, so
 * that first premature seed (an equal share of $0) froze forever and never reflected the finished
 * total. Custom shares are now only seeded once, off the finished value, at `onConfirmAmount`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class SharedCostCustomSplitSaveTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun customSplit_editedShareIsPassedToOnSaveSplit() {
        var savedMode: SharedSplitMode? = null
        var savedShares: List<String>? = null

        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    onSaveSplit = { _, _, mode, _, customShareRaws ->
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
        rule.onNodeWithText("Save split").performClick()

        rule.onNodeWithText("Custom split").performClick()

        // The un-edited default share must reflect the finished $120 total ($60 each) — not a
        // stale value frozen before the total was finished. Whole-dollar custom shares render
        // without decimals ("$60", not "$60.00").
        val shareField = rule.onAllNodesWithText("60", substring = true)[0]
        shareField.performClick()
        shareField.performTextInput("75")

        rule.onNodeWithText("Save split").performClick()
        rule.onNodeWithText("Save split", substring = true).performClick()

        assertEquals(SharedSplitMode.Custom, savedMode)
        assertEquals("75", savedShares?.getOrNull(0))
    }
}
