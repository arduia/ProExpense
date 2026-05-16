package com.arduia.expense.ui.common.exception

import java.lang.Exception

fun validateDateRange(start: Long, end: Long){
    if(start< 0 || end < 0) throw Exception("Invalid Date Range: start($start) or end($end) must be greater or equal zero.")
    if(start > end) throw Exception("Invalid Date Range: start($start) should be less than or equal end($end).")
}