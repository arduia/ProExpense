package com.arduia.expense.ui.common.filter

import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.ui.common.exception.validateDateRange

data class DateRangeSortingEnt(val dateRange: DateRange, val sorting: Sorting = Sorting.DESC) {

}

enum class Sorting {
    ASC, DESC
}