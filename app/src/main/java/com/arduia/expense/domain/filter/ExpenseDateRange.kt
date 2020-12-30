package com.arduia.expense.domain.filter

import com.arduia.expense.ui.common.exception.validateDateRange

class ExpenseDateRange(override val start: Long, override val end: Long) : DateRange{
    init {
        validateDateRange(start, end)
    }
}