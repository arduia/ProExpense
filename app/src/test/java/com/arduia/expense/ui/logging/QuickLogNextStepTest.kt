package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class QuickLogNextStepTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapping_next_advances_to_details_step() {
        rule.setContent {
            ProExpenseTheme {
                QuickLogFlow(onDismiss = {})
            }
        }

        // Enter a non-zero amount, then advance to the Details step.
        rule.onNodeWithText("5").performClick()
        rule.onNodeWithText("Next >").performClick()

        // Details step shows its "Details" app-bar title (regression: it used to render blank
        // because the tag sheet host filled the column and pushed the content off-screen).
        rule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test
    fun quick_commit_save_with_foreign_currency_and_no_rate_redirects_to_details() {
        rule.setContent {
            ProExpenseTheme {
                QuickLogFlow(
                    onDismiss = {},
                    startState =
                        ExpenseEntryState(
                            rawAmount = "10",
                            currencyCode = "EUR",
                            homeCurrencyCode = "USD",
                            exchangeRateRaw = "",
                        ),
                )
            }
        }

        // Quick-commit Save can't skip straight to a saved record without a rate — the field
        // only lives on Details, so Save must behave like Next instead of silently failing.
        rule.onNodeWithText("Save").performClick()

        rule.onNodeWithText("Details").assertIsDisplayed()
    }
}
