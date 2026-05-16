package com.arduia.expense.ui.common.formatter

import java.util.*

abstract class ExpenseDateFormatter() : DateFormatter {
    private val tmpCalendar = Calendar.getInstance()
    private val todayCalendar = Calendar.getInstance()

    @Synchronized
    override fun format(time: Long): String {
        tmpCalendar.timeInMillis = time

        val isSameYear = (tmpCalendar[Calendar.YEAR] == todayCalendar[Calendar.YEAR])
        val isSameMonth = (tmpCalendar[Calendar.MONTH] == todayCalendar[Calendar.MONTH])
        val isSameDay = (tmpCalendar[Calendar.DAY_OF_MONTH] == todayCalendar[Calendar.DAY_OF_MONTH])
        val isYesterday = (tmpCalendar[Calendar.DAY_OF_MONTH] == (todayCalendar[Calendar.DAY_OF_MONTH]-1))

        if (isSameYear and isSameMonth and isSameDay) {
            return getTodayDateFormat(tmpCalendar)
        }

        if(isSameYear and isSameMonth and isYesterday){
            return getYesterdayDateFormat(tmpCalendar)
        }

        if(isSameYear and isSameMonth.not()){
            return getSameYearDateFormat(tmpCalendar)
        }

        return getCompleteDateFormat(tmpCalendar)
    }

    // Today 5:00 PM
    abstract fun getTodayDateFormat(calendar: Calendar): String

    // Yesterday 5:00 PM
    abstract fun getYesterdayDateFormat(calendar: Calendar): String

    // Dec 31
    abstract fun getSameYearDateFormat(calendar: Calendar): String

    // 2020 Dec 1
    abstract fun getCompleteDateFormat(calendar: Calendar): String



}