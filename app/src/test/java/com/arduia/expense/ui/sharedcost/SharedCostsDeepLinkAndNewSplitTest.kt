package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
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
 * Bugfix guard: back from Split Summary opened via a Journal/Home-Recents deep link used to
 * always land on SharedCostsHistoryScreen first (a second back-press was needed to leave).
 * `deepLinkBackLabel`/`onDeepLinkBack` let the caller (ExpenseApp) route the first back-press
 * straight to the origin instead. `startAtNewSplit` covers Home's Split quick-access tile, which
 * must land directly on the amount-input screen instead of History.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class SharedCostsDeepLinkAndNewSplitTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    // Deliberately not "Dinner at Nobu" — that string is also the Input screen's note-field
    // placeholder (shared_note_placeholder), which would make onNodeWithText ambiguous/wrongly
    // pass for the startAtNewSplit test below.
    private val historyItem =
        SharedCostHistoryItemUi(
            id = "1",
            title = "Airbnb weekend",
            peopleCount = 2,
            perPersonLabel = "$60",
            dateLabel = "May 24",
            totalLabel = "$120",
        )

    private val historyDetail =
        SharedCostUiState(
            rawTotal = "120",
            note = "Airbnb weekend",
            peopleCount = 2,
            participants =
                listOf(
                    SharedCostParticipantUi("You", "$60"),
                    SharedCostParticipantUi("Person 2", "$60"),
                ),
            shareRaws = listOf("60", "60"),
        )

    @Test
    fun deepLinkedSummary_backTapInvokesDeepLinkBack_notHistory() {
        var deepLinkBackCalls = 0
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                    initialViewingId = historyItem.id,
                    deepLinkBackLabel = "Journals",
                    onDeepLinkBack = { deepLinkBackCalls++ },
                )
            }
        }

        rule.onNodeWithText("Journals").assertIsDisplayed()
        rule.onNodeWithText("Journals").performClick()

        assert(deepLinkBackCalls == 1) { "Expected onDeepLinkBack to fire once, got $deepLinkBackCalls" }
    }

    @Test
    fun deepLinkedSummary_withoutOnDeepLinkBack_fallsBackToHistory() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                    initialViewingId = historyItem.id,
                )
            }
        }

        // Default label unchanged when no deep-link back is wired (e.g. More-tab open).
        rule.onNodeWithText("History").assertIsDisplayed()
        rule.onNodeWithText("History").performClick()

        // Back landed on SharedCostsHistoryScreen — its own title is now visible.
        rule.onNodeWithText("Shared Costs").assertIsDisplayed()
    }

    @Test
    fun deepLinkedSummary_neverRendersHistoryListDuringTransition() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                    initialViewingId = historyItem.id,
                )
            }
        }

        // AnimatedContent's exiting placeholder frame is composed multiple times while it fades
        // out (Compose re-runs the content lambda for the outgoing step on every recomposition,
        // not just once) — step frame-by-frame through the whole History→Summary crossfade
        // (280ms screen + 200ms fade) and confirm the real split-history list (title "Shared
        // Costs", row title "Airbnb weekend") never renders, even mid-transition.
        repeat(30) {
            rule.mainClock.advanceTimeByFrame()
            rule.onNodeWithText("Shared Costs").assertDoesNotExist()
        }
    }

    @Test
    fun deepLinkedSummary_alreadyLoaded_rendersAtRestWithoutAnyCrossfadeAnimation() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                    initialViewingId = historyItem.id,
                )
            }
        }

        // sharedCostDetails already has this split's detail at mount, so the flow must start
        // directly on Summary (no Loading step, no History→Summary transition to animate). If it
        // instead started on Loading and animated over to Summary, Summary's content would still
        // be mid-slide (translated off the visible viewport by the forward-transition's
        // slideInHorizontally) at this frozen first frame, and this assertion would fail.
        rule.onNodeWithText("Airbnb weekend").assertIsDisplayed()
    }

    @Test
    fun startAtNewSplit_rendersInputScreenDirectly() {
        rule.setContent {
            ProExpenseTheme {
                SharedCostsFlow(
                    onDismiss = {},
                    history = listOf(historyItem),
                    sharedCostDetails = mapOf(historyItem.id to historyDetail),
                    startAtNewSplit = true,
                )
            }
        }

        // Input screen's own title is showing, not the History list of past splits.
        rule.onNodeWithText("Split a bill").assertIsDisplayed()
        rule.onNodeWithText("Airbnb weekend").assertDoesNotExist()
    }
}
