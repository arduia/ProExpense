package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.ui.components.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
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
 * US-SHC-1 guard: "optionally naming people (default 'Person 1…')" — participant names must be
 * editable on the input screen (previously rendered as static Text with no edit path at all).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostNameEditingTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun inputState(mode: SharedSplitMode) = SharedCostUiState(
        rawTotal = "120",
        peopleCount = 2,
        mode = mode,
        amountConfirmed = true,
        participants = listOf(
            SharedCostParticipantUi("Person 1", "$60.00"),
            SharedCostParticipantUi("Person 2", "$60.00"),
        ),
    )

    private fun setInputScreen(
        mode: SharedSplitMode,
        onNameChange: (Int, String) -> Unit,
    ) {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsInputScreen(
                    state = inputState(mode),
                    onBack = {},
                    onKey = {},
                    onBackspace = {},
                    onNoteChange = {},
                    onDecrementPeople = {},
                    onIncrementPeople = {},
                    onModeSelected = {},
                    onShareChange = { _, _ -> },
                    onContinue = {},
                    onNameChange = onNameChange,
                    showKeypad = false,
                )
            }
        }
    }

    @Test
    fun equalMode_participantNameIsEditable() {
        var edited: Pair<Int, String>? = null
        setInputScreen(SharedSplitMode.Equal) { index, name -> edited = index to name }

        rule.onNodeWithText("Person 2").performTextReplacement("Maya")

        assertEquals(1 to "Maya", edited)
    }

    @Test
    fun customMode_participantNameIsEditable() {
        var edited: Pair<Int, String>? = null
        setInputScreen(SharedSplitMode.Custom) { index, name -> edited = index to name }

        rule.onNodeWithText("Person 1").performTextReplacement("Aiko")

        assertEquals(0 to "Aiko", edited)
    }
}

/** Save-time name normalization rules for [SharedCostSplitLogic.resolveNames]. */
class SharedCostNameResolutionTest {

    @Test
    fun resolveNames_blankNameFallsBackToDefault() {
        val resolved = SharedCostSplitLogic.resolveNames(listOf("Aiko", ""), count = 2)

        assertEquals(listOf("Aiko", "Person 2"), resolved)
    }

    @Test
    fun resolveNames_whitespaceOnlyNameFallsBackToDefault() {
        val resolved = SharedCostSplitLogic.resolveNames(listOf("   ", "Ben"), count = 2)

        assertEquals(listOf("Person 1", "Ben"), resolved)
    }

    @Test
    fun resolveNames_trimsAndKeepsCustomNames_andSyncsToCount() {
        val resolved = SharedCostSplitLogic.resolveNames(listOf("  Aiko  "), count = 3)

        assertEquals(listOf("Aiko", "Person 2", "Person 3"), resolved)
    }
}
