package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.SharedCostActionsSheetContent
import com.arduia.expense.feature.sharedcost.ui.SharedCostsEditPersonSheetContent
import com.arduia.expense.feature.sharedcost.ui.SharedCostsHistoryScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsInputScreen
import com.arduia.expense.feature.sharedcost.ui.SharedCostsSummaryScreen
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostPerPersonCard
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedCustomLimits
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedEditPersonCustom
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedEditPersonEqual
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputConfirmed
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedInputEqual
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummary
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedSummaryCustom
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedZeroValidation
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
class SharedCostsScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(
        darkTheme: Boolean = false,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            ProExpenseTheme(darkTheme = darkTheme) {
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
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    @Test
    fun shared_input_dark() {
        capture(darkTheme = true) {
            SharedCostsInputScreen(
                state = previewSharedInputEqual,
                onBack = {},
                onKey = {},
                onBackspace = {},
                onNoteChange = {},
                onDecrementPeople = {},
                onIncrementPeople = {},
                onModeSelected = {},
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    @Test
    fun shared_input_confirmed() {
        capture {
            SharedCostsInputScreen(
                state = previewSharedInputConfirmed,
                onBack = {},
                onKey = {},
                onBackspace = {},
                onNoteChange = {},
                onDecrementPeople = {},
                onIncrementPeople = {},
                onModeSelected = {},
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    /** Regression guard: a near-max-digit amount (product cap 999,999,999.99) must shrink to fit
     *  on one line instead of overflowing/wrapping past the card's edge. */
    @Test
    fun edge_shared_person_card_large_amount() {
        capture {
            SharedCostPerPersonCard(
                headerEyebrow = "Custom shares",
                headerAmount = "$999,999,999.99",
                participants =
                    listOf(
                        "Aiko" to "$500,000,000.00",
                        "Ben" to "$499,999,999.99",
                    ),
                onHeaderEditClick = {},
                headerEditContentDescription = "Edit split",
                onPersonEditClick = {},
                personEditContentDescription = { "Edit person" },
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
                onRecordAsTransactionChange = {},
            )
        }
    }

    @Test
    fun shared_summary_dark() {
        capture(darkTheme = true) {
            SharedCostsSummaryScreen(
                state = previewSharedSummary,
                onBack = {},
                onSwitchToCustom = {},
                onSave = {},
                onRecordAsTransactionChange = {},
            )
        }
    }

    @Test
    fun shared_summary_saved() {
        capture {
            SharedCostsSummaryScreen(
                state = previewSharedSummary,
                onBack = {},
                onSwitchToCustom = {},
                onSave = {},
                onRecordAsTransactionChange = {},
                readOnly = true,
                onMore = {},
            )
        }
    }

    /** Custom split: Sum shows beside the total, but Per person does not (no single figure). */
    @Test
    fun shared_summary_custom() {
        capture {
            SharedCostsSummaryScreen(
                state = previewSharedSummaryCustom,
                onBack = {},
                onSwitchToCustom = {},
                onSave = {},
                onRecordAsTransactionChange = {},
            )
        }
    }

    @Test
    fun shared_actions_sheet() {
        capture {
            SharedCostActionsSheetContent(
                onEdit = {},
                onArchive = {},
                onDelete = {},
                onCancel = {},
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
                onBack = {},
            )
        }
    }

    @Test
    fun shared_history_dark() {
        capture(darkTheme = true) {
            SharedCostsHistoryScreen(
                items = previewSharedHistoryItems,
                onNewSplit = {},
                onItemClick = {},
                onBack = {},
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
                onContinue = {},
                showKeypad = false,
            )
        }
    }

    @Test
    fun shared_edit_person_equal() {
        capture {
            SharedCostsEditPersonSheetContent(
                people = previewSharedEditPersonEqual,
                activeIndex = 0,
                mode = SharedSplitMode.Equal,
                activeAmountRaw = "30",
                equalShareLabel = "$30.00",
                onPickPerson = {},
                onNameChange = {},
                onAmountKey = { _, _ -> },
                onAmountBackspace = {},
                onDone = {},
                onNext = {},
            )
        }
    }

    @Test
    fun shared_edit_person_custom() {
        capture {
            SharedCostsEditPersonSheetContent(
                people = previewSharedEditPersonCustom,
                activeIndex = 2,
                mode = SharedSplitMode.Custom,
                activeAmountRaw = "40",
                equalShareLabel = "$30.00",
                onPickPerson = {},
                onNameChange = {},
                onAmountKey = { _, _ -> },
                onAmountBackspace = {},
                onDone = {},
                onNext = {},
            )
        }
    }

    @Test
    fun shared_edit_person_last() {
        capture {
            SharedCostsEditPersonSheetContent(
                people = previewSharedEditPersonCustom,
                activeIndex = previewSharedEditPersonCustom.lastIndex,
                mode = SharedSplitMode.Custom,
                activeAmountRaw = "25",
                equalShareLabel = "$30.00",
                onPickPerson = {},
                onNameChange = {},
                onAmountKey = { _, _ -> },
                onAmountBackspace = {},
                onDone = {},
                onNext = {},
            )
        }
    }
}
