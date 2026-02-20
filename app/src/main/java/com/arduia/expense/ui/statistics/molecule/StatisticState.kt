package com.arduia.expense.ui.statistics.molecule

import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.statistics.CategoryStatisticUiModel

data class StatisticState(
    val categoryStatisticList: List<CategoryStatisticUiModel> = emptyList(),
    val isEmptyExpenseData: Boolean = true,
    val dateRangeInfo: String = "",
    val showFilterDialogForInfo: ExpenseLogFilterInfo? = null
)
