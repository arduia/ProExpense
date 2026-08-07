package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.categories.DeleteCategoryUseCase
import com.arduia.expense.feature.categories.ReorderCategoriesUseCase
import com.arduia.expense.feature.categories.SaveCategoryUseCase
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class CategoryRow(
    val id: String,
    val name: String,
    val iconId: String,
    val isCustom: Boolean,
    val isIncome: Boolean,
)

data class CategoriesUiState(
    val rows: List<CategoryRow> = emptyList(),
    val editingId: String? = null,
    val draftName: String = "",
    val draftIconId: String = "",
    val draftIsIncome: Boolean = false,
    val isEditorOpen: Boolean = false,
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && rows.isEmpty()

    val canSave: Boolean get() = draftName.isNotBlank()

    /** Default categories are renameable but not deletable — deleting one would orphan its records. */
    val canDeleteEditing: Boolean get() = rows.firstOrNull { it.id == editingId }?.isCustom == true
}

/**
 * 11 · Category List — manage the category catalog.
 *
 * The list is observed, not fetched once, so an add/delete performed here (or a category created
 * inline from Add Expense) reflects immediately without a manual refresh.
 */
class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val saveCategory: SaveCategoryUseCase,
    private val deleteCategory: DeleteCategoryUseCase,
    private val reorderCategories: ReorderCategoriesUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<CategoriesUiState>(CategoriesUiState(), dispatcher) {
    private var categories: List<Category> = emptyList()

    init {
        viewModelScope.launch {
            categoryRepository.observeAll().collect { all ->
                categories = all.sortedBy { it.sortOrder }
                setState { state ->
                    state.copy(
                        rows =
                            categories.map { category ->
                                CategoryRow(
                                    id = category.id.value,
                                    name = category.name,
                                    iconId = category.iconId,
                                    isCustom = category.isCustom,
                                    isIncome = category.type == RecordType.INCOME,
                                )
                            },
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun openCreate() {
        setState {
            it.copy(isEditorOpen = true, editingId = null, draftName = "", draftIconId = "", draftIsIncome = false)
        }
    }

    fun openEdit(categoryId: String) {
        val row = currentState().rows.firstOrNull { it.id == categoryId } ?: return
        setState {
            it.copy(
                isEditorOpen = true,
                editingId = row.id,
                draftName = row.name,
                draftIconId = row.iconId,
                draftIsIncome = row.isIncome,
            )
        }
    }

    fun closeEditor() {
        setState { it.copy(isEditorOpen = false, editingId = null, draftName = "") }
    }

    fun onNameChange(name: String) {
        setState { it.copy(draftName = name) }
    }

    fun onIconChange(iconId: String) {
        setState { it.copy(draftIconId = iconId) }
    }

    fun onTypeChange(isIncome: Boolean) {
        setState { it.copy(draftIsIncome = isIncome) }
    }

    suspend fun save() {
        val state = currentState()
        if (!state.canSave) return
        saveCategory(
            categories = categories,
            editingId = state.editingId,
            name = state.draftName.trim(),
            iconId = state.draftIconId,
            type = if (state.draftIsIncome) RecordType.INCOME else RecordType.EXPENSE,
        )
        closeEditor()
    }

    /** Records pointing at the deleted category are reassigned to Uncategorized by the use case. */
    suspend fun delete(categoryId: String) {
        deleteCategory(categoryId)
        closeEditor()
    }

    suspend fun reorder(orderedIds: List<String>) {
        reorderCategories(orderedIds)
    }
}
