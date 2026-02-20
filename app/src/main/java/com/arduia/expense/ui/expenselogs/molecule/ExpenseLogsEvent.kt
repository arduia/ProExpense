package com.arduia.expense.ui.expenselogs.molecule

import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.expenselogs.ExpenseLogUiModel

sealed interface ExpenseLogsEvent {

    object FilterButtonClicked : ExpenseLogsEvent
    object DeleteButtonClicked : ExpenseLogsEvent
    object FilterDialogDismissed : ExpenseLogsEvent
    data class FilterApplied(val filter: ExpenseLogFilterInfo) : ExpenseLogsEvent
    
    object NavDrawerClicked : ExpenseLogsEvent
    object SelectionCloseClicked : ExpenseLogsEvent
    data class SwipeSelectionCountChanged(val count: Int) : ExpenseLogsEvent
    
    // Selection
    data class ToggleSelection(val id: Int) : ExpenseLogsEvent
    object ClearSelection : ExpenseLogsEvent
    
    // Delete
    object MultiDeleteConfirmed : ExpenseLogsEvent
    object MultiDeleteDismissed : ExpenseLogsEvent
    
    data class SingleDeletePrepared(val id: Int) : ExpenseLogsEvent
    object SingleDeleteConfirmed : ExpenseLogsEvent
    object SingleDeleteDismissed : ExpenseLogsEvent
    
    // Detail
    data class ShowItemDetail(val item: ExpenseLogUiModel.Log) : ExpenseLogsEvent
    data class ShowDetailDialog(val detail: ExpenseDetailUiModel) : ExpenseLogsEvent
    object DetailDialogDismissed : ExpenseLogsEvent
}
