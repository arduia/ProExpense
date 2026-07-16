package com.arduia.expense.ui.design

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.captureRoboImageWithTolerance
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
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
class PickerScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @androidx.compose.runtime.Composable () -> Unit) {
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
        composeTestRule.onRoot().captureRoboImageWithTolerance()
    }

    @Test
    fun date_picker_empty_clearable() {
        capture {
            DatePickerScreen(
                visible = true,
                title = "Due date",
                initialEpochMillis = null,
                allowClear = true,
                onApply = {},
                onClear = {},
                onDismiss = {},
            )
        }
    }

    @Test
    fun date_picker_selected() {
        capture {
            // Fixed instant keeps the baseline deterministic across runs.
            DatePickerScreen(
                visible = true,
                title = "Due date",
                initialEpochMillis = 1_716_600_000_000L,
                allowClear = true,
                onApply = {},
                onClear = {},
                onDismiss = {},
            )
        }
    }

    @Test
    fun date_range_picker_empty() {
        capture {
            DateRangePickerScreen(
                visible = true,
                title = "Event dates",
                initialStartEpochMillis = null,
                initialEndEpochMillis = null,
                onApply = { _, _ -> },
                onDismiss = {},
            )
        }
    }

    @Test
    fun date_range_picker_set() {
        capture {
            // Fixed instants keep the baseline deterministic across runs.
            DateRangePickerScreen(
                visible = true,
                title = "Event dates",
                initialStartEpochMillis = 1_716_600_000_000L,
                initialEndEpochMillis = 1_717_200_000_000L,
                onApply = { _, _ -> },
                onDismiss = {},
            )
        }
    }
}
