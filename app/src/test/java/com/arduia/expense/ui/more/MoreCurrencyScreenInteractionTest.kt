package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arduia.expense.feature.currency.ui.MoreCurrencyScreen
import com.arduia.expense.feature.currency.ui.preview.previewMoreCurrencies
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
 * US-CUR-2: tapping a row only highlights it as pending — the home currency isn't updated until
 * Save is tapped, and Save stays disabled while the pending selection still matches the current
 * one. US-CUR-3: search filters the list by code/name.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class MoreCurrencyScreenInteractionTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun render(onSave: (String) -> Unit = {}) {
        rule.setContent {
            ProExpenseTheme {
                MoreCurrencyScreen(
                    items = previewMoreCurrencies,
                    selectedCode = "USD",
                    onSave = onSave,
                    onBack = {},
                )
            }
        }
    }

    @Test
    fun save_disabledInitially_whenPendingMatchesCurrentCurrency() {
        render()

        rule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun tappingRow_onlyHighlightsPending_doesNotCallOnSave() {
        var saveCalls = 0
        render(onSave = { saveCalls++ })

        rule.onNodeWithText("EUR").performClick()

        assert(saveCalls == 0) { "Tapping a row must not call onSave directly, got $saveCalls calls" }
        rule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun tappingSave_afterSelectingDifferentCurrency_callsOnSaveWithNewCode() {
        var savedCode: String? = null
        render(onSave = { savedCode = it })

        rule.onNodeWithText("EUR").performClick()
        rule.onNodeWithText("Save").performClick()

        assert(savedCode == "EUR") { "Expected onSave(\"EUR\"), got $savedCode" }
    }

    @Test
    fun searchFiltersListByNameOrCode() {
        render()

        rule.onNodeWithText("Euro").assertIsDisplayed()
        rule.onNodeWithText("US Dollar").assertIsDisplayed()

        rule.onNodeWithText("Search currency...").performTextInput("euro")

        rule.onNodeWithText("Euro").assertIsDisplayed()
        rule.onNodeWithText("US Dollar").assertDoesNotExist()
    }
}
