package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
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
 * Retrospective guard (2026-07): the chip row was nested inside the screen's padded content
 * Column, so the horizontalScroll viewport was capped at `screenWidth - 2*screenPadding` — chips
 * always stopped short of both screen edges no matter how far the user swiped. The row now sits
 * outside that padding with leading/trailing spacers reproducing the resting inset only.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class JournalChipRowEdgeToEdgeTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun lastFilterChip_scrollsFlushToTheScreensRightEdge() {
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
                    // The date-range chip pushes the row past the viewport width — without
                    // overflow there is nothing to scroll, and the defect wouldn't manifest.
                    dateRangeLabel = "May 1 – May 15",
                )
            }
        }

        val screenRight = rule.onRoot().getUnclippedBoundsInRoot().right
        // The last filter's label ("More") collides with the bottom nav tab of the same name —
        // index 0 is the filter chip since the chip row composes before HomeBottomNav.
        val lastChipLabel = previewJournalList.filters.last().label
        val lastChip = rule.onAllNodesWithText(lastChipLabel)[0]
        lastChip.performScrollTo()
        val lastChipRight = lastChip.getUnclippedBoundsInRoot().right

        // A row still capped by the screen's outer padding stops ~22dp short; a true edge-to-edge
        // viewport lands within a few dp of the physical screen edge (trailing spacer + rounding).
        val gapFromEdge = (screenRight - lastChipRight).value
        assert(gapFromEdge < 10f) {
            "Last chip should scroll near the screen's right edge, but stopped ${gapFromEdge}dp short " +
                "— the scrollable Row is likely still nested inside the screen's outer horizontal padding."
        }
    }
}
