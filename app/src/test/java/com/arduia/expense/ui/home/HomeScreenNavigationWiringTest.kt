package com.arduia.expense.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.preview.previewHomeMixedKinds
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
 * US-HOME-2/US-HOME-4: Home's recent rows and quick-access tiles must report taps with enough
 * information for the caller ([com.arduia.expense.ui.ExpenseApp]) to navigate — previously only
 * exercised with empty click lambdas (see `HomeScreenScreenshotTest`), so a wrong/missing row id
 * or a tile wired to the wrong callback would never have been caught.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class HomeScreenNavigationWiringTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Suppress("LongParameterList")
    private fun render(
        onRowClick: (ProTransactionRowModel) -> Unit = {},
        onSeeAll: () -> Unit = {},
        onReportsClick: () -> Unit = {},
        onDebtClick: () -> Unit = {},
        onSplitClick: () -> Unit = {},
        onEventsClick: () -> Unit = {},
    ) {
        rule.setContent {
            ProExpenseTheme {
                HomeScreenContent(
                    state = previewHomeMixedKinds,
                    onReportsClick = onReportsClick,
                    onDebtClick = onDebtClick,
                    onSplitClick = onSplitClick,
                    onEventsClick = onEventsClick,
                    onSeeAll = onSeeAll,
                    onRowClick = onRowClick,
                )
            }
        }
    }

    @Test
    fun tappingExpenseRow_invokesOnRowClick_withMatchingIdAndKind() {
        var clicked: ProTransactionRowModel? = null
        render(onRowClick = { clicked = it })

        rule.onNodeWithText("Lunch with M.").performClick()

        assert(clicked?.id == "r1") { "Expected row id \"r1\", got ${clicked?.id}" }
        assert(clicked?.rowKind == ProRowKind.EXPENSE) { "Expected EXPENSE, got ${clicked?.rowKind}" }
    }

    @Test
    fun tappingSplitRow_invokesOnRowClick_withSplitKindAndLinkedId() {
        var clicked: ProTransactionRowModel? = null
        render(onRowClick = { clicked = it })

        rule.onNodeWithText("Split · Dinner").performClick()

        assert(clicked?.rowKind == ProRowKind.SPLIT) { "Expected SPLIT, got ${clicked?.rowKind}" }
        assert(clicked?.linkedId == "sc1") { "Expected linkedId \"sc1\", got ${clicked?.linkedId}" }
    }

    @Test
    fun tappingDebtRow_invokesOnRowClick_withDebtKindAndLinkedId() {
        var clicked: ProTransactionRowModel? = null
        render(onRowClick = { clicked = it })

        rule.onNodeWithText("Lent to John").performClick()

        assert(clicked?.rowKind == ProRowKind.DEBT_LENT) { "Expected DEBT_LENT, got ${clicked?.rowKind}" }
        assert(clicked?.linkedId == "d1") { "Expected linkedId \"d1\", got ${clicked?.linkedId}" }
    }

    @Test
    fun tappingSeeAll_invokesOnSeeAll() {
        var seeAllCalls = 0
        render(onSeeAll = { seeAllCalls++ })

        rule.onNodeWithText("See all").performClick()

        assert(seeAllCalls == 1) { "Expected onSeeAll to fire once, got $seeAllCalls" }
    }

    @Test
    fun tappingEachQuickAccessTile_invokesItsOwnCallback_notAnother() {
        var reportsCalls = 0
        var debtCalls = 0
        var splitCalls = 0
        var eventsCalls = 0
        render(
            onReportsClick = { reportsCalls++ },
            onDebtClick = { debtCalls++ },
            onSplitClick = { splitCalls++ },
            onEventsClick = { eventsCalls++ },
        )

        rule.onNodeWithText("Reports").performClick()
        rule.onNodeWithText("Debt").performClick()
        rule.onNodeWithText("Split").performClick()
        rule.onNodeWithText("Goals").performClick()

        assert(reportsCalls == 1 && debtCalls == 1 && splitCalls == 1 && eventsCalls == 1) {
            "Expected each tile to fire its own callback exactly once, got " +
                "reports=$reportsCalls debt=$debtCalls split=$splitCalls events=$eventsCalls"
        }
    }
}
