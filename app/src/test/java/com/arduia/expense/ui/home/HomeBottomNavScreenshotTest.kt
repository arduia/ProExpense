package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
@Config(sdk = [33], qualifiers = "w414dp-h120dp")
class HomeBottomNavScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeBottomNav_homeSelected_matchesDesignSpec() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    HomeBottomNav(
                        selectedTab = HomeTab.Home,
                        onTabSelected = {},
                        onAddExpense = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
