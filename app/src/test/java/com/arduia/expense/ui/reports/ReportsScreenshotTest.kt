package com.arduia.expense.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.reports.ui.ReportsFlow
import com.arduia.expense.feature.reports.ui.ReportsScreen
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsPeriodEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.feature.reports.ui.preview.previewReportsWeekly
import com.arduia.expense.feature.reports.ui.preview.previewReportsWithOtherRollup
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
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
@Category(ScreenshotTests::class)
class ReportsScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun reports() =
        capture {
            ReportsScreen(previewReports, {}, {}, {})
        }

    @Test
    fun edge_reports_other_rollup() =
        capture {
            ReportsScreen(previewReportsWithOtherRollup, {}, {}, {})
        }

    @Test
    fun edge_reports_unc() =
        capture {
            ReportsScreen(previewReportsUncategorized, {}, {}, {})
        }

    @Test
    fun edge_reports_empty() =
        capture {
            ReportsScreen(previewReportsEmpty, {}, {}, {})
        }

    @Test
    fun edge_reports_period_empty() =
        capture {
            ReportsScreen(
                state = previewReportsPeriodEmpty,
                onBack = {},
                onPrevPeriod = {},
                onNextPeriod = {},
                globalEmpty = false,
            )
        }

    @Test
    fun reports_flow_monthly() =
        capture {
            ReportsFlow(onBack = {})
        }

    @Test
    fun reports_flow_weekly() =
        capture {
            ReportsFlow(
                onBack = {},
                periods = listOf(previewReportsWeekly),
                granularityIndex = 1,
            )
        }

    @Test
    fun edge_reports_flow_loading() =
        capture {
            ReportsFlow(onBack = {}, periods = emptyList(), isLoading = true)
        }

    @Test
    fun edge_reports_flow_all_periods_empty() =
        capture {
            // e.g. the weekly window's ~12-week history has no expenses at all — "swipe or use the
            // arrows to view another period" would be a dead end, so this gets a distinct message.
            ReportsFlow(
                onBack = {},
                periods = List(3) { previewReportsPeriodEmpty },
                granularityIndex = 1,
            )
        }
}
