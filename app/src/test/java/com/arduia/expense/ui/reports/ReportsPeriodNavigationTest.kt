package com.arduia.expense.ui.reports

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.reports.ui.ReportsFlow
import com.arduia.expense.feature.reports.ui.preview.previewReports
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
class ReportsPeriodNavigationTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val periods = listOf(
        previewReports.copy(periodLabel = "June 2026"),
        previewReports.copy(periodLabel = "May 2026"),
    )

    @Test
    fun tapping_previous_period_moves_to_an_older_period() {
        rule.setContent {
            ProExpenseTheme {
                ReportsFlow(onBack = {}, periods = periods)
            }
        }

        rule.onNodeWithText("June 2026").assertIsDisplayed()
        rule.onNodeWithContentDescription("Previous period").performClick()
        rule.onNodeWithText("May 2026").assertIsDisplayed()
    }

    @Test
    fun tapping_next_period_moves_back_toward_the_present() {
        rule.setContent {
            ProExpenseTheme {
                ReportsFlow(onBack = {}, periods = periods, initialPage = 1)
            }
        }

        rule.onNodeWithText("May 2026").assertIsDisplayed()
        rule.onNodeWithContentDescription("Next period").performClick()
        rule.onNodeWithText("June 2026").assertIsDisplayed()
    }

    @Test
    fun previous_period_is_disabled_at_the_oldest_period() {
        rule.setContent {
            ProExpenseTheme {
                ReportsFlow(onBack = {}, periods = periods, initialPage = 1)
            }
        }

        rule.onNodeWithText("May 2026").assertIsDisplayed()
        rule.onNodeWithContentDescription("Previous period").performClick()
        // No third, older period exists — the label must stay put, not wrap back to the newest.
        rule.onNodeWithText("May 2026").assertIsDisplayed()
    }
}
