package com.arduia.expense.ui.debt

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.debt.ui.DebtFlow
import com.arduia.expense.feature.debt.ui.preview.previewDebtLent
import com.arduia.expense.feature.debt.ui.preview.previewDebtOwe
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
 * Bugfix guard: back from Debt Detail opened via a Journal/Home-Recents deep link used to always
 * land on DebtListScreen first (a second back-press was needed to leave), regardless of where the
 * deep link came from. `deepLinkBackLabel`/`onDeepLinkBack` let the caller (ExpenseApp) route the
 * first back-press straight to the origin instead.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    // Min SDK (24), a scoped-storage/back-navigation milestone (30), and target SDK (36) — this
    // navigation-affordance logic has no Android-version-specific code path, so it's a reasonable
    // representative sample for cross-SDK coverage rather than every screen needing this treatment.
    sdk = [24, 30, 36],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class DebtDeepLinkBackNavigationTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deepLinkedDetail_backTapInvokesDeepLinkBack_notList() {
        var deepLinkBackCalls = 0
        rule.setContent {
            ProExpenseTheme {
                DebtFlow(
                    onDismiss = {},
                    lentState = previewDebtLent,
                    oweState = previewDebtOwe,
                    initialSelectedRecordId = "john",
                    deepLinkBackLabel = "Journals",
                    onDeepLinkBack = { deepLinkBackCalls++ },
                )
            }
        }

        rule.onNodeWithContentDescription("Journals").assertIsDisplayed()
        rule.onNodeWithContentDescription("Journals").performClick()

        assert(deepLinkBackCalls == 1) { "Expected onDeepLinkBack to fire once, got $deepLinkBackCalls" }
        // Still on Detail (or dismissed) — never fell through to DebtListScreen's own rows.
        rule.onNodeWithText("Maya").assertDoesNotExist()
    }

    @Test
    fun deepLinkedDetail_withoutOnDeepLinkBack_fallsBackToList() {
        rule.setContent {
            ProExpenseTheme {
                DebtFlow(
                    onDismiss = {},
                    lentState = previewDebtLent,
                    oweState = previewDebtOwe,
                    initialSelectedRecordId = "john",
                )
            }
        }

        // Default label unchanged when no deep-link back is wired (e.g. More-tab open).
        rule.onNodeWithContentDescription("Debt").assertIsDisplayed()
        rule.onNodeWithContentDescription("Debt").performClick()

        // Back landed on DebtListScreen — Maya (another active record) is now visible.
        rule.onNodeWithText("Maya").assertIsDisplayed()
    }

    @Test
    fun afterLeavingViaAnotherAction_viewingADifferentRecord_backNoLongerUsesDeepLinkBack() {
        var deepLinkBackCalls = 0
        rule.setContent {
            ProExpenseTheme {
                DebtFlow(
                    onDismiss = {},
                    lentState = previewDebtLent,
                    oweState = previewDebtOwe,
                    initialSelectedRecordId = "john",
                    deepLinkBackLabel = "Journals",
                    onDeepLinkBack = { deepLinkBackCalls++ },
                )
            }
        }

        // Settling the deep-linked record returns to the list without going through the back
        // button (DebtFlow.onMarkSettled clears selectedRecordId directly) — from there, open a
        // different record ("Maya", untouched by the settle action above).
        rule.onNodeWithText("Mark as settled").performClick()
        rule.onNodeWithText("Maya").performClick()

        // Now viewing "maya", not the original deep-linked "john" — deepLinkBackLabel/
        // onDeepLinkBack must not apply here even though both are still non-null on this DebtFlow.
        rule.onNodeWithContentDescription("Debt").assertIsDisplayed()
        rule.onNodeWithContentDescription("Debt").performClick()
        assert(deepLinkBackCalls == 0) { "onDeepLinkBack must not fire for a different record" }
        rule.onNodeWithText("John").assertIsDisplayed()
    }
}
