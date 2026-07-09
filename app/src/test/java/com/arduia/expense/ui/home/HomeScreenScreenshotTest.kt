package com.arduia.expense.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.captureRoboImageWithTolerance
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.preview.previewHomeBudget
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewHomeEvent
import com.arduia.expense.ui.preview.previewHomeLoading
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
class HomeScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun captureHome(content: @androidx.compose.runtime.Composable () -> Unit) {
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
        composeTestRule.onRoot().captureRoboImageWithTolerance()
    }

    @Test
    fun home_casual() {
        captureHome {
            HomeShell(
                state = previewHomeCasual,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }

    @Test
    fun home_budget() {
        captureHome {
            HomeShell(
                state = previewHomeBudget,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }

    @Test
    fun home_event() {
        captureHome {
            HomeShell(
                state = previewHomeEvent,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }

    @Test
    fun home_empty() {
        captureHome {
            HomeShell(
                state = previewHomeEmpty,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }

    @Test
    fun home_loading() {
        captureHome {
            HomeShell(
                state = previewHomeLoading,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }

    @Test
    fun home_casual_with_pin_banner() {
        captureHome {
            HomeShell(
                state = previewHomeCasual,
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
                showPinSetupBanner = true,
            )
        }
    }

    @Test
    fun home_content_only_casual() {
        captureHome {
            HomeScreenContent(
                state = previewHomeCasual,
                onReportsClick = {},
                onDebtClick = {},
                onSplitClick = {},
                onEventsClick = {},
            )
        }
    }
}
