package com.arduia.expense.ui.statistics.molecule

import com.arduia.expense.domain.filter.ExpenseLogFilterInfo

sealed interface StatisticEvent {
    object FilterIconClicked : StatisticEvent
    object FilterDialogDismissed : StatisticEvent
    data class FilterApplied(val filter: ExpenseLogFilterInfo) : StatisticEvent
}
