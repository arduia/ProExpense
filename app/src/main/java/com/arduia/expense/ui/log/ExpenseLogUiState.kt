package com.arduia.expense.ui.log

import com.arduia.expense.ui.component.expense.ExpenseRowUiModel

data class ExpenseLogUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedCount: Int = 0,
    val isInSelectionMode: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface ExpenseLogItem {
    data class Header(val date: String) : ExpenseLogItem
    data class Row(val expense: ExpenseRowUiModel) : ExpenseLogItem
}

sealed interface ExpenseLogUiEvent {
    data class NavigateToEdit(val expenseId: Long) : ExpenseLogUiEvent
    data object ShowDeleteConfirm : ExpenseLogUiEvent
}
