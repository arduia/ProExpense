package com.arduia.expense.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class HomeScreenCasualScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenCasual_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                HomeShell(
                    homeContent = {
                        HomeScreen(
                            state = HomeSampleData.casual,
                            onSeeAll = {},
                            onLogFirstExpense = {},
                            onQuickAccess = {},
                        )
                    },
                    selectedTab = HomeTab.Home,
                    onTabSelected = {},
                    onAddExpense = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
