package com.arduia.expense.ui.common.formatter

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class StatisticDateRangeFormatter @Inject constructor() : ExpenseDateRangeFormatter() {

    protected open val startFormatter = SimpleDateFormat(PATTERN_DATE_BASE, Locale.ENGLISH)
    private val endFormatter = SimpleDateFormat("", Locale.ENGLISH)

    @Synchronized
    override fun getDateRangeSameYearSameMonth(start: Long, end: Long): String {
        // 2020 Dec 1-4
        endFormatter.applyPattern(PATTERN_DATE_DAY_ONLY)
        return startFormatter.format(Date(start)) + SPACE_DATE_RANGE + endFormatter.format(Date(end))
    }

    @Synchronized
    override fun getDateRangeSameYearDifferentMonth(start: Long, end: Long): String {
        // 2020 Nov 1- Dec 4
        endFormatter.applyPattern(PATTERN_DATE_MONTH_AND_DAY)
        return startFormatter.format(Date(start)) + SPACE_DATE_RANGE + endFormatter.format(Date(end))
    }

    @Synchronized
    override fun getDateRangeSameDate(start: Long, end: Long): String {
        // 2020 Dec 1
        return startFormatter.format(Date(start))
    }

    @Synchronized
    override fun getDateRangeDifferentYears(start: Long, end: Long): String {
        // 2019 Dec 2 - 2020 Dec 1
        endFormatter.applyPattern(PATTERN_DATE_BASE)
        return startFormatter.format(Date(start)) + SPACE_DATE_RANGE + endFormatter.format(Date(end))
    }

    companion object {
        private const val PATTERN_DATE_BASE = "yyyy MMM d"
        private const val PATTERN_DATE_DAY_ONLY = "d"
        private const val PATTERN_DATE_MONTH_AND_DAY = "MMM d"
        private const val SPACE_DATE_RANGE = " - "
    }
}