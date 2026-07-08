package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.feature.history.ui.JournalListScreen
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * A reload of already-visible journal rows (change-signal refresh, filter change) must not blank
 * the list — swapping to the loading placeholder while rows are on screen is the "blink" glitch.
 * The bare loading placeholder is only for the initial load, when there is nothing to show yet.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class JournalReloadKeepsContentTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingWithRowsAlreadyOnScreen_keepsShowingTheRows() {
        rule.setContent {
            ProExpenseTheme {
                JournalListScreen(
                    state = previewJournalList.copy(isLoading = true),
                    onQueryChange = {},
                    onFilterSelected = {},
                    onRowClick = {},
                    onRowLongPress = {},
                    selectedTab = HomeNavTab.Journal,
                    onTabSelected = {},
                    onAddClick = {},
                )
            }
        }

        rule.onNodeWithText("Lunch with M.").assertIsDisplayed()
    }
}
