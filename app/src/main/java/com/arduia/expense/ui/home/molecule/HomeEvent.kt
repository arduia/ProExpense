package com.arduia.expense.ui.home.molecule

import com.arduia.expense.ui.expenselogs.ExpenseUiModel

sealed interface HomeEvent {
    object NavIconClicked : HomeEvent
    object MoreLogsClicked : HomeEvent
    data class RecentItemClicked(val item: ExpenseUiModel) : HomeEvent
    data class EditExpenseClicked(val expenseId: Int) : HomeEvent

    object AddExpenseClicked : HomeEvent
    data class DeleteExpensePrepared(val expenseId: Int) : HomeEvent
    object DeleteExpenseConfirmed : HomeEvent
    object DetailDialogDismissed : HomeEvent
    object DeleteDialogDismissed : HomeEvent
    object SnackbarShown : HomeEvent
    object NavigationHandled : HomeEvent
    object LifecycleOnResume : HomeEvent
}
