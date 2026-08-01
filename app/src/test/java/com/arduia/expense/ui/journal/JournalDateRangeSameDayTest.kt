package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.ui.JournalDateRangeSheet
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
@Category(ComposeUiTests::class)
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

        // Derived from the clock, not hard-coded: the picker opens on the current month, so a
        // pinned date silently stops being rendered once the calendar rolls past it.
        val day = midMonthDayLabel()
        rule.onNodeWithText(day).performClick()
        rule.onNodeWithText(day).performClick()

        rule.onNodeWithText(applyLabel).assertIsEnabled()
        rule.onNodeWithText(applyLabel).performClick()

        requireNotNull(confirmedStart)
        requireNotNull(confirmedEnd)
        assert(confirmedStart == confirmedEnd) {
            "Expected a single-day range (start == end), got start=$confirmedStart end=$confirmedEnd"
        }
    }

    /**
     * A day cell that always exists in the currently displayed month, labelled the way M3's
     * `DateRangePicker` labels an ordinary day ("Monday, May 25, 2026"). Today itself is avoided —
     * its cell carries a different, "today"-prefixed description.
     */
    private fun midMonthDayLabel(): String {
        val calendar = Calendar.getInstance()
        val day = if (calendar.get(Calendar.DAY_OF_MONTH) == MID_MONTH_DAY) MID_MONTH_DAY + 1 else MID_MONTH_DAY
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(calendar.time)
    }

    private companion object {
        const val MID_MONTH_DAY = 15
    }
}
