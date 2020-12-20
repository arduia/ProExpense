package com.arduia.expense.ui.common.filter

import com.arduia.expense.ui.common.exception.validateDateRange

data class DateRangeSortingEnt(val start: Long, val end: Long, val sorting: Sorting = Sorting.ASC) {
    init {
        validateDateRange(start = start,end =  end)
    }
}

enum class Sorting {
    ASC, DESC
}