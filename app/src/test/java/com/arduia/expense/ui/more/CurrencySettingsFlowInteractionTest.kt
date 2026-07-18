package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.currency.ui.CurrencySettingsFlow
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
 * US-CUR-2 Scenario 1 — picking a currency and tapping Save must show a confirmation dialog
 * naming the pending currency before `onSelect` (the actual persistence call) ever fires;
 * cancelling must leave the current home currency untouched.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class CurrencySettingsFlowInteractionTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun render(onSelect: (String) -> Unit) {
        rule.setContent {
            ProExpenseTheme {
                CurrencySettingsFlow(
                    selectedCode = "USD",
                    onSelect = onSelect,
                    onBack = {},
                )
            }
        }
    }

    /** The dialog's own confirm button shares the "Save" label with the underlying screen's. */
    private fun tapDialogConfirm() = rule.onAllNodesWithText("Save")[1].performClick()

    @Test
    fun tappingSave_showsConfirmDialog_beforeOnSelectFires() {
        var selectCalls = 0
        render(onSelect = { selectCalls++ })

        rule.onNodeWithText("EUR").performClick()
        rule.onNodeWithText("Save").performClick()

        rule.onNodeWithText("Change home currency?").assertIsDisplayed()
        assert(selectCalls == 0) { "onSelect must not fire until the dialog is confirmed, got $selectCalls calls" }
    }

    @Test
    fun confirmingDialog_callsOnSelectWithPendingCurrency() {
        var selectedCode: String? = null
        render(onSelect = { selectedCode = it })

        rule.onNodeWithText("EUR").performClick()
        rule.onNodeWithText("Save").performClick()
        tapDialogConfirm()

        assert(selectedCode == "EUR") { "Expected onSelect(\"EUR\"), got $selectedCode" }
    }

    @Test
    fun cancelingDialog_leavesCurrentCurrencyUnchanged() {
        var selectCalls = 0
        render(onSelect = { selectCalls++ })

        rule.onNodeWithText("EUR").performClick()
        rule.onNodeWithText("Save").performClick()
        rule.onNodeWithText("Cancel").performClick()

        assert(selectCalls == 0) { "Cancelling must never call onSelect, got $selectCalls calls" }
        rule.onNodeWithText("Change home currency?").assertDoesNotExist()
    }
}
