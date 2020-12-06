package com.arduia.expense.ui.expense.filter

data class ExpenseLogFilterVo(val startTime: Long, val endTime: Long, val sorting: Sorting)

enum class Sorting{
    ASC, DESC
}