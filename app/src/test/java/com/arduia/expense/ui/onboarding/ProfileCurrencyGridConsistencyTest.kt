package com.arduia.expense.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.height
import com.arduia.expense.feature.onboarding.ui.ProfileSetupScreenContent
import com.arduia.expense.feature.onboarding.ui.ProfileSetupState
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.absoluteValue

/**
 * Retrospective guard (2026-07): the currency quick-pick grid pairs two [ProfileCurrencyRow]s per
 * `Row` with `Modifier.weight(1f)` only — without a shared height budget, a row whose currency
 * name wraps to two lines (e.g. "South Korean Won") grew taller than its single-line sibling
 * (e.g. "British Pound"), so the two boxes in the same row visibly mismatched in height.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class ProfileCurrencyGridConsistencyTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun longCurrencyName_matchesRowSiblingHeight() {
        rule.setContent {
            ProExpenseTheme {
                ProfileSetupScreenContent(
                    state = ProfileSetupState(name = "Maya", selectedCurrencyCode = "KRW"),
                    onNameChange = {},
                    onStartTracking = {},
                    onCurrencySelected = {},
                    onOpenCurrencySheet = {},
                    onCloseCurrencySheet = {},
                    onCurrencySearchChange = {},
                )
            }
        }

        val longNameRowHeight = rule.onNodeWithText("KRW").getUnclippedBoundsInRoot().height
        val siblingRowHeight = rule.onNodeWithText("GBP").getUnclippedBoundsInRoot().height

        val diff = (longNameRowHeight.value - siblingRowHeight.value).absoluteValue
        assert(diff <= 1f) {
            "Currency row height for a wrapped label ($longNameRowHeight) must match its " +
                "same-row sibling ($siblingRowHeight) — CurrencyQuickGrid's Row needs " +
                "height(IntrinsicSize.Max) and each ProfileCurrencyRow needs fillMaxHeight() " +
                "so both boxes stretch to the tallest sibling."
        }
    }
}
