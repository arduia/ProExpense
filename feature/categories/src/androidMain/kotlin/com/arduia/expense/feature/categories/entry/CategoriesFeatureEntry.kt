package com.arduia.expense.feature.categories.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.categories.ui.CategoryListFlow as CategoryListFlowContent
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList

interface CategoriesFeatureEntry {
    @Composable
    fun CategoryListFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        state: CategoryListUiState = previewCategoryList,
        onCreate: (iconId: String, label: String) -> Unit = { _, _ -> },
        onUpdate: (oldId: String, iconId: String, label: String) -> Unit = { _, _, _ -> },
        onDelete: (id: String) -> Unit = {},
    )
}

internal class CategoriesFeatureEntryImpl : CategoriesFeatureEntry {
    @Composable
    override fun CategoryListFlow(
        onBack: () -> Unit,
        modifier: Modifier,
        state: CategoryListUiState,
        onCreate: (iconId: String, label: String) -> Unit,
        onUpdate: (oldId: String, iconId: String, label: String) -> Unit,
        onDelete: (id: String) -> Unit,
    ) {
        CategoryListFlowContent(
            onBack = onBack,
            modifier = modifier,
            state = state,
            onCreate = onCreate,
            onUpdate = onUpdate,
            onDelete = onDelete,
        )
    }
}

object CategoriesFeatureUi : CategoriesFeatureEntry by CategoriesFeatureEntryImpl()
