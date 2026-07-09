package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsSummaryScreen
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

    private fun inputState(mode: SharedSplitMode) =
        SharedCostUiState(
            rawTotal = "120",
            peopleCount = 2,
            mode = mode,
            amountConfirmed = true,
            participants =
                listOf(
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

    /**
     * Tapping into a pre-filled name field is meant to overwrite it — the whole value must be
     * selected on focus so typing replaces it outright, instead of appending after the cursor
     * (e.g. "Person 2" + "Zoe" -> "Person 2Zoe").
     */
    @Test
    fun focusingExistingName_selectsAllSoTypingOverwritesIt() {
        var edited: Pair<Int, String>? = null
        setInputScreen(SharedSplitMode.Equal) { index, name -> edited = index to name }

        val field = rule.onNodeWithText("Person 2")
        field.performClick()
        field.performTextInput("Zoe")

        assertEquals(1 to "Zoe", edited)
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

/**
 * i18n guard: `defaultParticipantName`/`syncNames`/`resolveNames` previously hardcoded the
 * English "Person N" literal directly, even though this module ships `values-th`/`values-my`
 * translations for everything else. They now accept a `%1$d`-style template
 * (`R.string.shared_default_person_name` at real call sites) so the default name localizes too.
 */
class SharedCostDefaultNameTemplateTest {
    @Test
    fun defaultParticipantName_usesProvidedTemplate() {
        assertEquals("P1", SharedCostSplitLogic.defaultParticipantName(1, "P%1\$d"))
    }

    @Test
    fun syncNames_generatesMissingNamesUsingProvidedTemplate() {
        val result = SharedCostSplitLogic.syncNames(emptyList(), count = 2, nameTemplate = "P%1\$d")

        assertEquals(listOf("P1", "P2"), result)
    }

    @Test
    fun resolveNames_blankNameFallsBackUsingProvidedTemplate() {
        val result = SharedCostSplitLogic.resolveNames(listOf("Aiko", ""), count = 2, nameTemplate = "P%1\$d")

        assertEquals(listOf("Aiko", "P2"), result)
    }
}

/** [SharedCostSplitLogic]'s formatters must reflect the user's actual home currency, not a hardcoded "$". */
class SharedCostCurrencyFormatTest {
    @Test
    fun formatCents_usesProvidedCurrencySymbol() {
        assertEquals("€12.34", SharedCostSplitLogic.formatCents(1234, "€"))
    }

    @Test
    fun formatRawTotal_usesProvidedCurrencySymbol() {
        assertEquals("¥120", SharedCostSplitLogic.formatRawTotal("120", "¥"))
    }

    @Test
    fun formatShareRaw_usesProvidedCurrencySymbol() {
        assertEquals("£60", SharedCostSplitLogic.formatShareRaw("60", "£"))
    }

    @Test
    fun formatCents_defaultsToDollarSignWhenSymbolOmitted() {
        assertEquals("$12.34", SharedCostSplitLogic.formatCents(1234))
    }
}

/**
 * Blocker-audit guard: the input/summary screens previously hardcoded "$" regardless of the
 * caller's currency, while saved history correctly showed the real symbol — a non-USD user saw
 * the wrong symbol throughout the entire creation flow.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostCurrencySymbolWiringTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun inputScreen_showsProvidedCurrencySymbol_notHardcodedDollar() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsInputScreen(
                    state = SharedCostUiState(rawTotal = "120", amountConfirmed = false),
                    onBack = {},
                    onKey = {},
                    onBackspace = {},
                    onNoteChange = {},
                    onDecrementPeople = {},
                    onIncrementPeople = {},
                    onModeSelected = {},
                    onShareChange = { _, _ -> },
                    onContinue = {},
                    showKeypad = false,
                    homeCurrencySymbol = "€",
                )
            }
        }

        rule.onNodeWithText("€120", substring = true).assertExists()
    }

    @Test
    fun summaryScreen_showsProvidedCurrencySymbol_notHardcodedDollar() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsSummaryScreen(
                    state =
                        SharedCostUiState(
                            rawTotal = "120",
                            peopleCount = 2,
                            participants =
                                listOf(
                                    SharedCostParticipantUi("Person 1", "€60.00"),
                                    SharedCostParticipantUi("Person 2", "€60.00"),
                                ),
                        ),
                    onBack = {},
                    onSwitchToCustom = {},
                    onSave = {},
                    homeCurrencySymbol = "€",
                )
            }
        }

        rule.onAllNodesWithText("€60", substring = true)[0].assertExists()
    }
}

/**
 * High-audit guard: US-SHC-2's NFR promises custom shares "survive process death the same as
 * any other draft input" — the draft previously used a bare `remember`, wiped on process death.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostDraftPersistenceTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun draft_survivesSimulatedProcessDeath() {
        val restorationTester = StateRestorationTester(rule)
        restorationTester.setContent {
            ProExpenseTheme {
                SharedCostsFlow(onDismiss = {})
            }
        }

        rule.onNodeWithText("New split").performClick()
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("0").performClick()

        restorationTester.emulateSavedInstanceStateRestore()

        rule.onNodeWithText("120", substring = true).assertExists()
    }
}
