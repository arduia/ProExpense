package com.arduia.expense.ui.common.formatter

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MonthDateRangeFormatter @Inject constructor() : StatisticDateRangeFormatter() {
    override val startFormatter: SimpleDateFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
    override fun getDateRangeDifferentYears(start: Long, end: Long): String {
        return getDateRangeSameYearDifferentMonth(start, end)
    }
}