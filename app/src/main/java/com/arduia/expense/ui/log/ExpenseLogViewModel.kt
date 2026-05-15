package com.arduia.expense.ui.log

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExpenseLogViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseLogUiState())
    val uiState: StateFlow<ExpenseLogUiState> = _uiState.asStateFlow()

    private val _expenseItems = MutableStateFlow<List<ExpenseLogItem>>(emptyList())
    val expenseItems: StateFlow<List<ExpenseLogItem>> = _expenseItems.asStateFlow()

    fun onDelete(id: Long) { /* TODO */ }
}
