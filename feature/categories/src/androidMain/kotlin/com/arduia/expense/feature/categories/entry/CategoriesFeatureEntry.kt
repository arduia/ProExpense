package com.arduia.expense.feature.categories.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import java.util.Locale
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

        val categories by categoryRepository.observeAll().collectAsState(emptyList())
        val defaults = categories.filter { !it.isCustom }.map { it.toRowUi() }
        val custom = categories.filter { it.isCustom }.map { it.toRowUi() }

        com.arduia.expense.feature.categories.ui.CategoryListFlow(
            onBack = onBack,
            state = CategoryListUiState(defaults = defaults, custom = custom),
            onReorderCustom = { reorderedCustomIds ->
                scope.launch {
                    val orderedIds = (defaults.map { it.categoryId } + reorderedCustomIds).map(::CategoryId)
                    categoryRepository.reorder(orderedIds)
                }
            },
            onSaveCategory = { editingId, name ->
                scope.launch {
                    val existing = editingId?.let { id -> categories.firstOrNull { it.id.value == id } }
                    categoryRepository.upsert(
                        Category(
                            id = CategoryId(existing?.id?.value ?: newCategoryId(name)),
                            name = name,
                            isCustom = true,
                            sortOrder = existing?.sortOrder ?: custom.size,
                        ),
                    )
                }
            },
            onDeleteCategory = { id ->
                scope.launch { categoryRepository.delete(CategoryId(id)) }
            },
            modifier = modifier,
        )
    }
}

object CategoriesFeatureUi : CategoriesFeatureEntry by CategoriesFeatureEntryImpl()

private fun newCategoryId(name: String): String =
    name.trim().lowercase(Locale.US).replace(" ", "-") + "-" + System.currentTimeMillis()

private fun Category.toRowUi(): CategoryRowUi = CategoryRowUi(categoryId = id.value, label = name)
