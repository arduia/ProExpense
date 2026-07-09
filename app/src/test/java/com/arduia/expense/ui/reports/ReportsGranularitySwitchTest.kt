package com.arduia.expense.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.reports.ui.ReportsFlow
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
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
class ReportsGranularitySwitchTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapping_the_granularity_toggle_reportsTheCurrentlyDisplayedPeriodAsTheSwitchAnchor() {
        var reportedIndex: Int? = null
        var reportedAnchor: Pair<Long, Long>? = null
        val periods =
            listOf(
                previewReports.copy(
                    periodLabel = "May 2026",
                    periodStartEpochMillis = 1_000L,
                    periodEndEpochMillis = 2_000L,
                ),
            )

        rule.setContent {
            ProExpenseTheme {
                ReportsFlow(
                    onBack = {},
                    periods = periods,
                    onGranularityChange = { index, anchor ->
                        reportedIndex = index
                        reportedAnchor = anchor
                    },
                )
            }
        }

        rule.onNodeWithText("Week").performClick()

        assertEquals(1, reportedIndex)
        assertEquals(1_000L to 2_000L, reportedAnchor)
    }
}
