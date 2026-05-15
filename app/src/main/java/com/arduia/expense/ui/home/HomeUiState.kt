package com.arduia.expense.ui.home

import com.arduia.expense.ui.component.expense.ExpenseRowUiModel

data class HomeUiState(
    val totalExpense: String = "",
    val totalIncome: String = "",
    val totalBalance: String = "",
    val currencySymbol: String = "",
    val recentExpenses: List<ExpenseRowUiModel> = emptyList(),
    val weeklyGraphData: List<Float> = emptyList(),
    val isLoading: Boolean = false,
)

sealed interface HomeUiEvent {
    data object NavigateToExpenseEntry : HomeUiEvent
    data class NavigateToExpenseDetail(val expenseId: Long) : HomeUiEvent
    data object NavigateToExpenseLog : HomeUiEvent
}
