package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: opening a saved split from History and tapping the actions sheet's Edit used to
 * land on the amount keypad instead of the split-detail screen (people/mode/participants) —
 * `toDraft()` never set `amountConfirmed = true`, so `SharedCostsInputScreen`'s `showDetails` gate
 * evaluated false for a draft rebuilt from an already-saved split. "Review" only renders once
 * `showDetails` is true, so its presence right after Edit confirms the detail view opened directly.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class SharedCostEditNavigationTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val historyItem =
        SharedCostHistoryItemUi(
            id = "1",
            title = "Dinner at Nobu",
            peopleCount = 2,
            perPersonLabel = "$60",
            dateLabel = "May 24",
            totalLabel = "$120",
        )

    private val historyDetail =
        SharedCostUiState(
            rawTotal = "120",
            note = "Dinner at Nobu",
            peopleCount = 2,
            participants =
                listOf(
                    SharedCostParticipantUi("You", "$60"),
                    SharedCostParticipantUi("Person 2", "$60"),
                ),
            shareRaws = listOf("60", "60"),
        )

    @Test
    fun editingASavedSplit_opensSplitDetailNotAmountKeypad() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                )
            }
        }

        rule.onNodeWithText("Dinner at Nobu").performClick()
        rule.onNodeWithContentDescription("More").performClick()
        rule.onNodeWithText("Edit").performClick()

        rule.onNodeWithText("Review").assertExists()
    }
}
