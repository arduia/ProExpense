package com.arduia.expense.ui.design

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w427dp-h952dp")
@Category(ScreenshotTests::class)
class HomeBottomNavScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun captureBottomNav(selectedTab: HomeNavTab) {
        composeTestRule.setContent {
            ProExpenseTheme {
                val colors = ProExpenseTheme.colors
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.paper),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    HomeBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = {},
                        onAddClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun home_bottom_nav_home_active() {
        captureBottomNav(HomeNavTab.Home)
    }

    @Test
    fun home_bottom_nav_journal_active() {
        captureBottomNav(HomeNavTab.Journal)
    }
}
