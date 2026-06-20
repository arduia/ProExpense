package com.arduia.expense.feature.logging

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CategoryListUiState(
    val categories: List<Pair<String, String>> = emptyList(),
    val selectedCategoryId: String? = null,
    val newCategoryName: String = "",
    val duplicateError: Boolean = false,
    val isLoading: Boolean = true,
)

class CategoryListViewModel(
    private val categoryRepository: CategoryRepository,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            when (val result = categoryRepository.getAll()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            categories = result.data.map { category -> category.id to category.name },
                            isLoading = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onNewCategoryChange(name: String) {
        _uiState.update { it.copy(newCategoryName = name.take(MAX_NAME_LENGTH), duplicateError = false) }
    }

    fun onAddCategory() {
        val trimmed = _uiState.value.newCategoryName.trim()
        if (trimmed.isBlank()) return
        val exists = _uiState.value.categories.any { it.second.equals(trimmed, ignoreCase = true) }
        if (exists) {
            _uiState.update { it.copy(duplicateError = true) }
            return
        }
        scope.launch {
            val id = "custom_${Random.nextLong().toString(16)}"
            val category = Category(id = id, name = trimmed, isCustom = true)
            when (categoryRepository.upsert(category)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            newCategoryName = "",
                            duplicateError = false,
                            selectedCategoryId = id,
                        )
                    }
                    refresh()
                }
                is Result.Error -> Unit
            }
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 20
    }
}
