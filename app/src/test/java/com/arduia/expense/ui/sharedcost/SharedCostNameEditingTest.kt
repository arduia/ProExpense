package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.width
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.SharedCostsEditPersonSheetContent
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsSummaryScreen
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.absoluteValue

/**
 * US-SHC-1 guard: "optionally naming people (default 'Person 1…')" — participant names must be
 * editable, now via the dedicated Edit-person sheet (`EditPersonSheetV4` in the design handoff)
 * rather than inline fields on the input screen.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostEditPersonSheetContentTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val people =
        listOf(
            SharedCostParticipantUi("Aiko", "$30.00"),
            SharedCostParticipantUi("Ben", "$30.00"),
        )

    @Suppress("LongParameterList")
    private fun setSheet(
        mode: SharedSplitMode = SharedSplitMode.Equal,
        activeIndex: Int = 0,
        activeAmountRaw: String = "30",
        onPickPerson: (Int) -> Unit = {},
        onNameChange: (String) -> Unit = {},
        onAmountKey: (key: String, freshEntry: Boolean) -> Unit = { _, _ -> },
        onAmountBackspace: (freshEntry: Boolean) -> Unit = {},
        onDone: () -> Unit = {},
        onNext: () -> Unit = {},
    ) {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsEditPersonSheetContent(
                    people = people,
                    activeIndex = activeIndex,
                    mode = mode,
                    activeAmountRaw = activeAmountRaw,
                    equalShareLabel = "$30.00",
                    onPickPerson = onPickPerson,
                    onNameChange = onNameChange,
                    onAmountKey = onAmountKey,
                    onAmountBackspace = onAmountBackspace,
                    onDone = onDone,
                    onNext = onNext,
                )
            }
        }
    }

    @Test
    fun nameField_editingActivePerson_invokesOnNameChange() {
        var edited: String? = null
        setSheet(activeIndex = 0, onNameChange = { edited = it })

        // "Aiko" also appears in the read-only roster row above the editable field — narrow to
        // the node that actually accepts text input.
        rule.onNode(hasText("Aiko") and hasSetTextAction()).performTextReplacement("Maya")

        assertEquals("Maya", edited)
    }

    @Test
    fun rosterTap_invokesOnPickPersonWithTappedIndex() {
        var picked: Int? = null
        setSheet(activeIndex = 0, onPickPerson = { picked = it })

        rule.onNodeWithText("Ben").performClick()

        assertEquals(1, picked)
    }

    @Test
    fun equalMode_amountIsLocked_tappingNeverRevealsKeypad() {
        setSheet(mode = SharedSplitMode.Equal)

        rule.onNodeWithContentDescription("Amount").performClick()

        rule.onNodeWithText("7").assertDoesNotExist()
    }

    @Test
    fun customMode_tappingAmount_revealsKeypadAndKeyPressInvokesCallback() {
        var pressedKey: String? = null
        setSheet(mode = SharedSplitMode.Custom, activeAmountRaw = "40", onAmountKey = { key, _ -> pressedKey = key })

        rule.onNodeWithText("7").assertDoesNotExist()
        rule.onNodeWithContentDescription("Amount").performClick()
        rule.onNodeWithText("7").performClick()

        assertEquals("7", pressedKey)
    }

    /**
     * Regression guard: the amount field arrives pre-filled with the computed equal share (or a
     * previously-set custom value) — the first keystroke after activating it must report
     * `freshEntry = true` so the caller overwrites rather than appends onto it (the same
     * "560.00" append bug the old inline field's select-all-on-focus behavior guarded against).
     */
    @Test
    fun customMode_firstKeyAfterActivation_reportsFreshEntry_subsequentKeysDoNot() {
        val freshFlags = mutableListOf<Boolean>()
        setSheet(
            mode = SharedSplitMode.Custom,
            activeAmountRaw = "40",
            onAmountKey = { _, fresh -> freshFlags.add(fresh) },
        )

        rule.onNodeWithContentDescription("Amount").performClick()
        rule.onNodeWithText("7").performClick()
        rule.onNodeWithText("5").performClick()

        assertEquals(listOf(true, false), freshFlags)
    }

    @Test
    fun done_invokesOnDone_withoutTouchingOnNext() {
        var doneCalled = false
        var nextCalled = false
        setSheet(onDone = { doneCalled = true }, onNext = { nextCalled = true })

        rule.onNodeWithText("Done").performClick()

        assertTrue(doneCalled)
        assertFalse(nextCalled)
    }

    @Test
    fun next_invokesOnNext() {
        var nextCalled = false
        setSheet(onNext = { nextCalled = true })

        rule.onNodeWithText("Next").performClick()

        assertTrue(nextCalled)
    }

    /** There's no next person to advance to on the last participant — the label must say so. */
    @Test
    fun lastPerson_advanceButtonReadsReview_notNext() {
        var nextCalled = false
        setSheet(activeIndex = people.lastIndex, onNext = { nextCalled = true })

        rule.onNodeWithText("Next").assertDoesNotExist()
        rule.onNodeWithText("Review").performClick()

        assertTrue(nextCalled)
    }

    /**
     * Regression guard: the advance button once used weight(1.4f) against Done's weight(1f),
     * rendering visibly narrower/wider siblings in the same row.
     */
    @Test
    fun doneAndNextButtons_areEqualWidth() {
        setSheet()

        val doneWidth = rule.onNodeWithText("Done").getUnclippedBoundsInRoot().width
        val nextWidth = rule.onNodeWithText("Next").getUnclippedBoundsInRoot().width

        val diff = (doneWidth.value - nextWidth.value).absoluteValue
        assert(diff <= 1f) {
            "Done ($doneWidth) and Next ($nextWidth) buttons must be equal width."
        }
    }
}

/**
 * Integration guard: the Edit-person sheet is reached from Screen 1's edit-icon buttons, and
 * "Next" walks through every person before landing on Split summary — "Done" only closes the
 * sheet.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostEditPersonSheetFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    /** New split, total "120" confirmed — lands on the input screen's Details section (default 2 people). */
    private fun startNewSplitDetails() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(onDismiss = {})
            }
        }
        rule.onNodeWithText("New split").performClick()
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("0").performClick()
        rule.onNodeWithText("Save split").performClick()
    }

    @Test
    fun tappingEditSplitIcon_opensEditPersonSheet() {
        startNewSplitDetails()

        rule.onNodeWithContentDescription("Edit split").performClick()

        rule.onNodeWithText("Edit person").assertExists()
    }

    @Test
    fun nextThroughAllPeople_landsOnSplitSummary() {
        startNewSplitDetails()

        rule.onNodeWithContentDescription("Edit split").performClick()
        rule.onNodeWithText("Next").performClick()
        // Default new-split draft has 2 people — this second person is the last one, so the
        // advance button reads "Review" instead of "Next".
        rule.onNodeWithText("Review").performClick()

        rule.onNodeWithText("Split summary").assertExists()
    }

    @Test
    fun done_closesSheetWithoutNavigatingToSummary() {
        startNewSplitDetails()

        rule.onNodeWithContentDescription("Edit split").performClick()
        rule.onNodeWithText("Done").performClick()

        rule.onNodeWithText("Split a bill").assertExists()
    }

    /**
     * US-SHC-2 guard: person info (names) must carry over when switching between Equal and
     * Custom — only amounts are mode-specific (Equal is always computed, never the stored
     * custom share). A rename made in one mode must still be visible after switching modes.
     */
    @Test
    fun renamingInEqualMode_persistsAfterSwitchingToCustom() {
        startNewSplitDetails()

        rule.onNodeWithContentDescription("Edit Person 2").performScrollTo().performClick()
        rule.onNode(hasText("Person 2") and hasSetTextAction()).performTextReplacement("Zara")
        rule.onNodeWithText("Done").performClick()

        rule.onNodeWithText("Custom split").performClick()

        rule.onNodeWithText("Zara").assertExists()
    }

    @Test
    fun renamingInCustomMode_persistsAfterSwitchingBackToEqual() {
        startNewSplitDetails()
        rule.onNodeWithText("Custom split").performClick()

        rule.onNodeWithContentDescription("Edit Person 2").performScrollTo().performClick()
        rule.onNode(hasText("Person 2") and hasSetTextAction()).performTextReplacement("Zara")
        rule.onNodeWithText("Done").performClick()

        rule.onNodeWithText("Even split").performClick()

        rule.onNodeWithText("Zara").assertExists()
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
