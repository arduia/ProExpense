package com.arduia.expense.ui.categories

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.R
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.preview.previewCategoryList
import com.arduia.expense.ui.preview.previewCategoryNewDuplicate
import com.arduia.expense.ui.theme.ProArtboard
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
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ScreenshotTests::class)
class CategoryListScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @Composable () -> Unit) {
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
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun categories() = capture {
        CategoryListScreen(previewCategoryList, {}, {})
    }

    @Test
    fun edge_cat_dup() = capture {
        Box(Modifier.fillMaxSize()) {
            CategoryListScreen(previewCategoryList, {}, {})
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.categories_new_title),
                onClose = {},
            ) {
                CategoryNewSheetContent(
                    form = previewCategoryNewDuplicate,
                    onNameChange = {},
                    onIconSelected = {},
                    onAdd = {},
                )
            }
        }
    }
}
