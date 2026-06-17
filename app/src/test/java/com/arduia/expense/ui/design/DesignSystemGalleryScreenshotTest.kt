package com.arduia.expense.ui.design

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
class DesignSystemGalleryScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun designSystemGallery_matchesGolden() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground {
                    DesignSystemGallery()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
