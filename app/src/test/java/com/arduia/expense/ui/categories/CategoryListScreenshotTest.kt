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
import com.arduia.expense.feature.categories.R
import com.arduia.expense.feature.categories.ui.CategoryActionsSheetContent
import com.arduia.expense.feature.categories.ui.CategoryListScreen
import com.arduia.expense.feature.categories.ui.CategoryNewSheetContent
import com.arduia.expense.feature.categories.ui.preview.CategoryNewFormState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.feature.categories.ui.preview.previewCategoryNewDuplicate
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.captureRoboImageWithTolerance
import com.arduia.expense.ui.design.ProBottomSheetHost
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
class CategoryListScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(
        darkTheme: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            ProExpenseTheme(darkTheme = darkTheme) {
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
    fun categories() =
        capture {
            CategoryListScreen(previewCategoryList, {}, {})
        }

    @Test
    fun categories_dark() =
        capture(darkTheme = true) {
            CategoryListScreen(previewCategoryList, {}, {})
        }

    @Test
    fun edge_cat_dup() =
        capture {
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

    @Test
    fun cat_actions() =
        capture {
            Box(Modifier.fillMaxSize()) {
                CategoryListScreen(previewCategoryList, {}, {})
                ProBottomSheetHost(visible = true, title = null, onClose = {}) {
                    CategoryActionsSheetContent(
                        row = CategoryRowUi("coffee", "Coffee runs"),
                        onEdit = {},
                        onDelete = {},
                        onCancel = {},
                    )
                }
            }
        }

    @Test
    fun cat_edit() =
        capture {
            Box(Modifier.fillMaxSize()) {
                CategoryListScreen(previewCategoryList, {}, {})
                ProBottomSheetHost(
                    visible = true,
                    title = stringResource(R.string.categories_edit_title),
                    onClose = {},
                ) {
                    CategoryNewSheetContent(
                        form = CategoryNewFormState(name = "Coffee runs", selectedIconId = "coffee"),
                        onNameChange = {},
                        onIconSelected = {},
                        onAdd = {},
                        confirmLabel = stringResource(R.string.categories_save),
                    )
                }
            }
        }
}
