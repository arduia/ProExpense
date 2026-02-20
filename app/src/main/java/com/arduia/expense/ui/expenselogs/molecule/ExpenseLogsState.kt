package com.arduia.expense.ui.expenselogs.molecule

import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.expenselogs.ExpenseMode

data class ExpenseLogsState(
    val mode: ExpenseMode = ExpenseMode.NORMAL,
    val selectedCount: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val isEmptyLogs: Boolean = true,
    val filterDateRangeText: String = "",
    val isFilterEnabled: Boolean = false,
    val showFilterDialogForInfo: ExpenseLogFilterInfo? = null,
    val showMultiDeleteConfirmCount: Int? = null,
    val showSingleDeleteConfirm: Boolean = false,
    val showDetailDialogFor: ExpenseDetailUiModel? = null,
    val filterConstraint: DateRangeSortingEnt? = null,
    val currencySymbol: String = ""
)
