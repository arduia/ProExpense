package com.arduia.expense.ui.entry

import com.arduia.expense.ui.component.category.CategoryUiModel

data class ExpenseEntryUiState(
    val expenseId: Long = -1L,
    val isEditMode: Boolean = false,
    val name: String = "",
    val amount: String = "",
    val note: String = "",
    val selectedCategoryId: Int? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val currencySymbol: String = "",
    val date: String = "",
    val nameError: String? = null,
    val amountError: String? = null,
    val isSaving: Boolean = false,
)

sealed interface ExpenseEntryUiEvent {
    data object SaveSuccess : ExpenseEntryUiEvent
    data object NavigateBack : ExpenseEntryUiEvent
    data class ShowError(val message: String) : ExpenseEntryUiEvent
}
