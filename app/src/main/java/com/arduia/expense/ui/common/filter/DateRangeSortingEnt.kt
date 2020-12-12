package com.arduia.expense.ui.common.filter

data class DateRangeSortingEnt(val start: Long, val end: Long, val sorting: Sorting = Sorting.ASC)

enum class Sorting{
    ASC, DESC
}