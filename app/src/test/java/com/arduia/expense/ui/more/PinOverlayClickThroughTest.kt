package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.ui.PinEntryScreen
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Regression guard: PinEntryScreen (used by PinLockFlow and PinVerifyFlow) is rendered as an
 * overlay sibling above other screens — e.g. the disable-PIN re-verify step sits above the
 * still-composed More hub. A tap landing on empty space (margins, gaps between keypad buttons)
 * previously fell through to whatever was rendered underneath, since the screen's root had no
 * click-consuming modifier of its own — reported as "disabling PIN navigates to the Category
 * list screen" when the stray tap happened to land on that hub card.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    // Security-critical click-through guard — worth checking across the supported SDK range
    // (min 24, scoped-storage/back-nav milestone 30, target 36) rather than one pinned version.
    sdk = [24, 30, 36],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class PinOverlayClickThroughTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tapOnEmptySpace_doesNotReachContentBehindTheOverlay() {
        var behindClicked = false

        rule.setContent {
            ProExpenseTheme {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                                .clickable { behindClicked = true },
                    )
                    PinEntryScreen(
                        state = PinEntryUiState(filledDots = 0),
                        onDigit = {},
                        onBackspace = {},
                        onBiometric = {},
                        onForgot = {},
                    )
                }
            }
        }

        // Top-left corner: padding only, never a keypad button or text element.
        rule.onRoot().performTouchInput { click(Offset(8f, 8f)) }

        assertFalse("Tap on empty overlay space reached content behind it", behindClicked)
    }
}
