package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.sharedcost.ui.SharedCostsHistoryScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsSummaryScreen
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedCustomLimits
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummary
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedZeroValidation
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
class SharedCostsScreenshotTest {

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
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun shared_input() {
        capture {
            SharedCostsInputScreen(
                state = previewSharedInputEqual,
                onBack = {},
                onKey = {},
                onBackspace = {},
                onNoteChange = {},
                onDecrementPeople = {},
                onIncrementPeople = {},
                onModeSelected = {},
                onShareChange = { _, _ -> },
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    @Test
    fun shared_summary() {
        capture {
            SharedCostsSummaryScreen(
                state = previewSharedSummary,
                onBack = {},
                onSwitchToCustom = {},
                onSave = {},
            )
        }
    }

    @Test
    fun shared_history() {
        capture {
            SharedCostsHistoryScreen(
                items = previewSharedHistoryItems,
                onNewSplit = {},
                onItemClick = {},
            )
        }
    }

    @Test
    fun edge_shared_zero() {
        capture {
            SharedCostsInputScreen(
                state = previewSharedZeroValidation,
                onBack = {},
                onKey = {},
                onBackspace = {},
                onNoteChange = {},
                onDecrementPeople = {},
                onIncrementPeople = {},
                onModeSelected = {},
                onShareChange = { _, _ -> },
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    @Test
    fun edge_shared_limits() {
        capture {
            SharedCostsInputScreen(
                state = previewSharedCustomLimits,
                onBack = {},
                onKey = {},
                onBackspace = {},
                onNoteChange = {},
                onDecrementPeople = {},
                onIncrementPeople = {},
                onModeSelected = {},
                onShareChange = { _, _ -> },
                onContinue = {},
                showKeypad = false,
            )
        }
    }
}
