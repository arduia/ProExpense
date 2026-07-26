package com.arduia.expense.ui.events

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arduia.expense.feature.eventbudget.ui.EventsFlow
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
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

/**
 * US-EVT-1 Scenario/FR: a new event name that case-insensitively matches an existing active
 * event's name is blocked with "An event with this name already exists" (`EventsFlow.isDuplicate`
 * — case-insensitive, trims whitespace). `previewEventList` includes "Bali Trip".
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class EventDuplicateNameTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun creatingEvent_withCaseInsensitiveDuplicateName_showsDuplicateError() {
        rule.setContent {
            ProExpenseTheme {
                EventsFlow(
                    onTabSelected = {},
                    onAddClick = {},
                    events = previewEventList,
                )
            }
        }

        rule.onNodeWithText("New").performClick()
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("  bali trip  ")

        rule.onNodeWithText("An event with this name already exists").assertIsDisplayed()
    }
}
