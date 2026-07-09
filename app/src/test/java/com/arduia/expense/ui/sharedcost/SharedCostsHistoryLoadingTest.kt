package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.feature.sharedcost.ui.SharedCostsHistoryScreen
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Splits load asynchronously after first composition — `isLoading` must suppress the
 * "No splits yet" empty state until the real Flow emission has arrived, otherwise a user with
 * existing splits sees a false "no splits yet" flash on open (see [SharedCostFeatureEntry]'s
 * `collectAsState(initial = null)`).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostsHistoryLoadingTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_suppressesEmptyState() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsHistoryScreen(
                    items = emptyList(),
                    isLoading = true,
                    onNewSplit = {},
                    onItemClick = {},
                    onBack = {},
                )
            }
        }

        rule.onNodeWithText("No splits yet").assertDoesNotExist()
    }

    @Test
    fun notLoading_emptyList_showsEmptyState() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsHistoryScreen(
                    items = emptyList(),
                    isLoading = false,
                    onNewSplit = {},
                    onItemClick = {},
                    onBack = {},
                )
            }
        }

        rule.onNodeWithText("No splits yet").assertExists()
    }
}
