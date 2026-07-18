package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.height
import com.arduia.expense.feature.history.ui.JournalListScreen
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.absoluteValue

/**
 * Retrospective guard (2026-07): the date-range chip once rendered at 59dp next to 28dp
 * FilterChips because a nested `proIconClickable` applied its 48dp minimum interactive size
 * inside the chip. Screenshot tests recorded the defect as the baseline and stayed green, so
 * sibling-size consistency must be asserted on real layout bounds, not pixels.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class JournalChipRowConsistencyTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dateRangeChip_matchesFilterChipHeight() {
        val dateRangeLabel = "May 1 – May 15"
        rule.setContent {
            ProExpenseTheme {
                JournalListScreen(
                    state = previewJournalList,
                    onQueryChange = {},
                    onFilterSelected = {},
                    onRowClick = {},
                    onRowLongPress = {},
                    selectedTab = HomeNavTab.Journal,
                    onTabSelected = {},
                    onAddClick = {},
                    dateRangeLabel = dateRangeLabel,
                )
            }
        }

        val dateChipHeight = rule.onNodeWithText(dateRangeLabel).getUnclippedBoundsInRoot().height
        val filterChipHeight = rule.onNodeWithText("All").getUnclippedBoundsInRoot().height

        val diff = (dateChipHeight.value - filterChipHeight.value).absoluteValue
        assert(diff <= 2f) {
            "Date-range chip height ($dateChipHeight) must match sibling FilterChip height " +
                "($filterChipHeight) — a nested minimumInteractiveComponentSize modifier " +
                "(e.g. proIconClickable) inflates the chip."
        }
        // Equality alone would pass if BOTH chips were inflated to the 48dp floor — also cap
        // the absolute height at the chip spec (~30dp label + 12dp padding).
        assert(filterChipHeight.value <= 40f) {
            "Chip row height ($filterChipHeight) exceeds the compact chip spec — check for " +
                "minimum-size inflation in the chip implementations."
        }
    }
}
