package com.arduia.expense.ui.statistics

import com.arduia.expense.ui.component.category.CategoryStatisticUiModel

data class StatisticsUiState(
    val selectedRange: String = "Month",
    val availableRanges: List<String> = listOf("Week", "Month", "Year"),
    val totalExpense: String = "",
    val categoryBreakdown: List<CategoryStatisticUiModel> = emptyList(),
    val weeklyGraphData: List<Float> = emptyList(),
    val isLoading: Boolean = false,
)
