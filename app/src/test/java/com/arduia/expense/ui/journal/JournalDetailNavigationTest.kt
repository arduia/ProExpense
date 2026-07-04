package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.history.ui.JournalFlow
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
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
class JournalDetailNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapping_journal_row_opens_expense_detail() {
        rule.setContent {
            ProExpenseTheme {
                JournalFlow(
                    selectedTab = HomeNavTab.Journal,
                    onTabSelected = {},
                    onAddClick = {},
                )
            }
        }

        rule.onNodeWithText("Lunch with M.").performClick()

        // Detail screen shows the NOTE section header + Edit/Delete actions.
        rule.onNodeWithText("NOTE").assertIsDisplayed()
        // Regression guard: the line under the amount is a date+time label, not category+time
        // (previously reused the list row's "Food · 12:30 PM" meta string verbatim).
        rule.onNodeWithText("Today · 12:30 PM").assertIsDisplayed()
    }
}
