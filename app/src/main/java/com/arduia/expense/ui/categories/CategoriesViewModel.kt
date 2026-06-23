package com.arduia.expense.ui.categories

import com.arduia.expense.R
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.domain.Category
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.orPost
import com.arduia.expense.ui.postIfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoriesScreenState(
    val list: CategoryListUiState = CategoryListUiState(defaults = emptyList(), custom = emptyList()),
)

/**
 * Read/write model for category management. Splits the stored categories into built-in defaults and
 * user-created custom ones and persists create/edit/delete. Consistent with the rest of the app, a
 * category's id doubles as its icon id (the badge resolves by id), so editing the icon re-keys the
 * row (delete old + insert new). Order is not persisted (no order column) — reorder stays in the UI.
 */
class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
) {
    private val _state = MutableStateFlow(CategoriesScreenState())
    val state: StateFlow<CategoriesScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    fun create(iconId: String, label: String) {
        scope.launch {
            categoryRepository.upsert(Category(id = iconId, name = label.trim(), isCustom = true))
                .postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    fun update(oldId: String, iconId: String, label: String) {
        scope.launch {
            if (oldId != iconId) categoryRepository.delete(oldId)
            categoryRepository.upsert(Category(id = iconId, name = label.trim(), isCustom = true))
                .postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    fun delete(id: String) {
        scope.launch {
            categoryRepository.delete(id).postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    private suspend fun reload() {
        val categories = categoryRepository.getAll().orPost(uiMessages, R.string.data_load_error, emptyList())
        _state.value = CategoriesScreenState(
            list = CategoryListUiState(
                defaults = categories.filter { !it.isCustom }.map { it.toRow() },
                custom = categories.filter { it.isCustom }.map { it.toRow() },
            ),
        )
    }

    private fun Category.toRow(): CategoryRowUi = CategoryRowUi(categoryId = id, label = name)
}
