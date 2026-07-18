package com.arduia.expense.ui.categories

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.arduia.expense.feature.categories.R
import com.arduia.expense.feature.categories.ui.CategoryListScreen
import com.arduia.expense.feature.categories.ui.CategoryNewSheetContent
import com.arduia.expense.feature.categories.ui.preview.CategoryNewFormState
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Covers the Product UX lens check in AGENTS.md Step 3 (auto-focus the primary input) for a
 * representative "create a new record" sheet. Roborazzi/screenshot tests can't prove this —
 * they capture a static frame, not whether `FocusRequester.requestFocus()` actually landed — and
 * there's no adb/emulator in this environment to check on a real device, so a Compose UI Test
 * assertion is the only way to verify it.
 */
@RunWith(RobolectricTestRunner::class)
@Category(ComposeUiTests::class)
class CategoryNewSheetFocusTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun categoryNewSheet_autofocusesNameFieldOnOpen() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(Modifier.fillMaxSize()) {
                    CategoryListScreen(previewCategoryList, {}, {})
                    ProBottomSheetHost(
                        visible = true,
                        title = stringResource(R.string.categories_new_title),
                        onClose = {},
                    ) {
                        CategoryNewSheetContent(
                            form = CategoryNewFormState(),
                            onNameChange = {},
                            onIconSelected = {},
                            onAdd = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNode(hasSetTextAction()).assertIsFocused()
    }
}
