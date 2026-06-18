package com.arduia.expense.ui.design

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
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
class ProDesignSystemScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setDesignSystemContent(content: @androidx.compose.runtime.Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                val colors = ProExpenseTheme.colors
                val dimens = ProExpenseTheme.dimensions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.paper)
                        .padding(dimens.space18),
                ) {
                    content()
                }
            }
        }
    }

    @Test
    fun design_system_buttons() {
        setDesignSystemContent { ProDesignSystemButtonsContent() }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun design_system_categories() {
        setDesignSystemContent { ProDesignSystemCategoriesContent() }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun design_system_fields() {
        setDesignSystemContent { ProDesignSystemFieldsContent() }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun design_system_lists() {
        setDesignSystemContent { ProDesignSystemListsContent() }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun design_system_chrome() {
        setDesignSystemContent { ProDesignSystemChromeContent() }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun design_system_gallery() {
        composeTestRule.setContent {
            ProExpenseTheme { ProDesignSystemComponents() }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
