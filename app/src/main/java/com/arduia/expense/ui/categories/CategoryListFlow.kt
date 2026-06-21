package com.arduia.expense.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.preview.CategoryNewFormState
import com.arduia.expense.ui.preview.previewCategoryList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryListFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val state = previewCategoryList
    val existingNames = remember(state) {
        (state.defaults + state.custom).map { it.label.lowercase() }.toSet()
    }

    var showNew by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf(CategoryNewFormState()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        CategoryListScreen(
            state = state,
            onBack = onBack,
            onCreate = {
                form = CategoryNewFormState()
                showNew = true
            },
        )

        ProBottomSheetHost(
            visible = showNew,
            title = stringResource(R.string.categories_new_title),
            onClose = { showNew = false },
        ) {
            CategoryNewSheetContent(
                form = form,
                onNameChange = { name ->
                    form = form.copy(
                        name = name,
                        duplicate = name.isNotBlank() && name.trim().lowercase() in existingNames,
                    )
                },
                onIconSelected = { form = form.copy(selectedIconId = it) },
                onAdd = { if (form.canAdd) showNew = false },
            )
        }
    }
}

@Preview(
    name = "Category list flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryListFlowPreview() {
    ProExpenseTheme {
        CategoryListFlow(onBack = {})
    }
}
