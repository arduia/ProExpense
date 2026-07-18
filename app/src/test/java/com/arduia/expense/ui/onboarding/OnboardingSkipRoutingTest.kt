package com.arduia.expense.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.onboarding.ui.OnboardingScreenContent
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
 * US-ONB-1/US-ONB-2: `Skip` is present on every slide except the last, `Get started` is always
 * present, and `Back` only appears once you've advanced past the first slide.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class OnboardingSkipRoutingTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun render(
        initialPage: Int = 0,
        onGetStarted: () -> Unit = {},
        onSkip: () -> Unit = {},
    ) {
        rule.setContent {
            ProExpenseTheme {
                OnboardingScreenContent(onGetStarted = onGetStarted, onSkip = onSkip, initialPage = initialPage)
            }
        }
    }

    @Test
    fun firstSlide_showsSkipAndNext_hidesBack() {
        render(initialPage = 0)

        rule.onNodeWithText("Skip").assertIsDisplayed()
        rule.onNodeWithText("Next").assertIsDisplayed()
        rule.onNodeWithText("Back").assertDoesNotExist()
        rule.onNodeWithText("Get started").assertIsDisplayed()
    }

    @Test
    fun lastSlide_hidesSkipAndNext_showsBack() {
        render(initialPage = 4)

        rule.onNodeWithText("Skip").assertDoesNotExist()
        rule.onNodeWithText("Next").assertDoesNotExist()
        rule.onNodeWithText("Back").assertIsDisplayed()
        rule.onNodeWithText("Get started").assertIsDisplayed()
    }

    @Test
    fun tappingSkip_invokesOnSkip() {
        var skipCalls = 0
        render(onSkip = { skipCalls++ })

        rule.onNodeWithText("Skip").performClick()

        assert(skipCalls == 1) { "Expected onSkip to fire once, got $skipCalls" }
    }

    @Test
    fun tappingGetStarted_invokesOnGetStarted_fromAnySlide() {
        var getStartedCalls = 0
        render(initialPage = 2, onGetStarted = { getStartedCalls++ })

        rule.onNodeWithText("Get started").performClick()

        assert(getStartedCalls == 1) { "Expected onGetStarted to fire once, got $getStartedCalls" }
    }
}
