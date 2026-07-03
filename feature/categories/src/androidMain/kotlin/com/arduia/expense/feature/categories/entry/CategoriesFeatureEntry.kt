package com.arduia.expense.feature.categories.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.domain.Category
import com.arduia.expense.feature.categories.DeleteCategoryUseCase
import com.arduia.expense.feature.categories.ReorderCategoriesUseCase
import com.arduia.expense.feature.categories.SaveCategoryUseCase
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface CategoriesFeatureEntry {
    @Composable
    fun CategoryListFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class CategoriesFeatureEntryImpl : CategoriesFeatureEntry {
    @Composable
    override fun CategoryListFlow(onBack: () -> Unit, modifier: Modifier) {
        val scope = rememberCoroutineScope()
        val categoryRepository: CategoryRepository = koinInject()
        val saveCategory: SaveCategoryUseCase = koinInject()
        val deleteCategory: DeleteCategoryUseCase = koinInject()
        val reorderCategories: ReorderCategoriesUseCase = koinInject()

        val categories by categoryRepository.observeAll().collectAsState(emptyList())
        val defaults = categories.filter { !it.isCustom }.map { it.toRowUi() }
        val custom = categories.filter { it.isCustom }.map { it.toRowUi() }

        com.arduia.expense.feature.categories.ui.CategoryListFlow(
            onBack = onBack,
            state = CategoryListUiState(defaults = defaults, custom = custom),
            onReorderCustom = { reorderedCustomIds ->
                scope.launch { reorderCategories(defaults.map { it.categoryId }, reorderedCustomIds) }
            },
            onSaveCategory = { editingId, name, iconId ->
                scope.launch { saveCategory(categories, editingId, name, iconId) }
            },
            onDeleteCategory = { id ->
                scope.launch { deleteCategory(id) }
            },
            modifier = modifier,
        )
    }
}

object CategoriesFeatureUi : CategoriesFeatureEntry by CategoriesFeatureEntryImpl()

private fun Category.toRowUi(): CategoryRowUi = CategoryRowUi(categoryId = id.value, label = name, iconId = iconId)
