package com.arduia.expense.ui.common.formatter

import com.arduia.expense.ui.common.exception.validateDateRange
import java.util.*

abstract class ExpenseDateRangeFormatter: DateRangeFormatter {

    override fun format(start: Long, end: Long): String {
        validateDateRange(start, end)
        val tmpStart = Calendar.getInstance().apply { timeInMillis = start }
        val tmpEnd = Calendar.getInstance().apply { timeInMillis = end }

        val isSameYear = (tmpStart[Calendar.YEAR] == tmpEnd[Calendar.YEAR])
        val isSameMonth = (tmpStart[Calendar.MONTH] == tmpEnd[Calendar.MONTH])
        val isSameDay = (tmpStart[Calendar.DAY_OF_MONTH] == tmpEnd[Calendar.DAY_OF_MONTH])

        //Different Years ->  2019 Dec 12 - 2020 Nov 31
        if(isSameYear.not()){
            return getDateRangeDifferentYears(start, end)
        }

        //Same Year, Different Month -> 2020 Nov 12 - Dec 12
        if(isSameYear and isSameMonth.not()){
            return getDateRangeSameYearDifferentMonth(start, end)
        }

        //Same Year, Same Month, Different Day -> 2020 Nov 12 - 14
        if(isSameYear and isSameMonth and isSameDay.not()){
            return getDateRangeSameYearSameMonth(start, end)
        }

        //Same Day -> 2020 Nov 12
        return getDateRangeSameDate(start, end)
    }

    protected abstract fun getDateRangeSameYearSameMonth(start: Long, end: Long): String

    protected abstract fun getDateRangeSameYearDifferentMonth(start: Long, end: Long): String

    protected abstract fun getDateRangeSameDate(start: Long, end: Long): String

    protected abstract fun getDateRangeDifferentYears(start: Long, end: Long): String
}