package com.arduia.expense.ui.design

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.wrapContentSize
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
@Config(sdk = [33])
@Category(ScreenshotTests::class)
class DesignSystemSpecScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun captureSpec(content: @androidx.compose.runtime.Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                SpecCaptureHost(modifier = Modifier.wrapContentSize()) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test fun button_variants() = captureSpec { SpecButtonVariantsCapture() }

    @Test fun button_sizes() = captureSpec { SpecButtonSizesCapture() }

    @Test fun button_states() = captureSpec { SpecButtonStatesCapture() }

    @Test fun cat_badges() = captureSpec { SpecCategoryBadgesCapture() }

    @Test fun cat_chips() = captureSpec { SpecCategoryChipsCapture() }

    @Test fun filter_chips() = captureSpec { SpecFilterChipsCapture() }

    @Test fun txn_daygroup() = captureSpec { SpecTransactionDayGroupCapture() }

    @Test fun spent_card() = captureSpec { SpecSpentCardCapture() }

    @Test fun keypad() = captureSpec { SpecKeypadCapture() }

    @Test fun search_empty() = captureSpec { SpecSearchEmptyCapture() }

    @Test fun search_filled() = captureSpec { SpecSearchFilledCapture() }

    @Test fun quick_access() = captureSpec { SpecQuickAccessCapture() }

    @Test fun validation() = captureSpec { SpecValidationCapture() }

    @Test fun nav_home() = captureSpec { SpecBottomNavHomeCapture() }

    @Test fun nav_budget() = captureSpec { SpecBottomNavBudgetCapture() }

    @Test fun bottom_sheet() = captureSpec { SpecBottomSheetCapture() }

    @Test fun toast() = captureSpec { SpecToastCapture() }

    @Test fun icons() = captureSpec { SpecIconsCapture() }
}
