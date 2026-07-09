package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.ui.JournalDateRangeSheet
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Verifies the open audit question from the Journal/Reports date-time UI review: does M3's
 * `DateRangePicker` support a single-day range (tapping the same day for both start and end)?
 * No emulator/device was available in this environment, so this drives the actual production
 * composable through Robolectric instead of a manual on-device check.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class JournalDateRangeSameDayTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapping_theSameDayTwice_selectsASingleDayRangeAndEnablesApply() {
        var confirmedStart: Long? = null
        var confirmedEnd: Long? = null

        rule.setContent {
            ProExpenseTheme {
                JournalDateRangeSheet(
                    visible = true,
                    initialStartEpochMillis = null,
                    initialEndEpochMillis = null,
                    onConfirm = { start, end ->
                        confirmedStart = start
                        confirmedEnd = end
                    },
                    onClear = {},
                    onDismiss = {},
                )
            }
        }

        val applyLabel = rule.activity.getString(R.string.journal_date_range_apply)
        rule.onNodeWithText(applyLabel).assertIsNotEnabled()

        rule.onNodeWithText("Wednesday, July 15, 2026").performClick()
        rule.onNodeWithText("Wednesday, July 15, 2026").performClick()

        rule.onNodeWithText(applyLabel).assertIsEnabled()
        rule.onNodeWithText(applyLabel).performClick()

        requireNotNull(confirmedStart)
        requireNotNull(confirmedEnd)
        assert(confirmedStart == confirmedEnd) {
            "Expected a single-day range (start == end), got start=$confirmedStart end=$confirmedEnd"
        }
    }
}
