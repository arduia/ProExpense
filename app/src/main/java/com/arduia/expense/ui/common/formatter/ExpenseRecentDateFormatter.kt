package com.arduia.expense.ui.common.formatter

import android.content.Context
import com.arduia.expense.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpenseRecentDateFormatter @Inject constructor(): ExpenseDateFormatter() {

    private val todayText = "Today"
    private val yesterdayText = "Yesterday"
    private val formatter = SimpleDateFormat(COMPLETE_DATE_PATTERN, Locale.ENGLISH)

    override fun getTodayDateFormat(calendar: Calendar): String {
        formatter.applyPattern(TIME_DATE_PATTERN)
        return todayText+" ${formatter.format(calendar.time)}"
    }

    override fun getSameYearDateFormat(calendar: Calendar): String {
        formatter.applyPattern(SAME_YEAR_DATE_PATTERN)
        return formatter.format(calendar.time)
    }

    override fun getCompleteDateFormat(calendar: Calendar): String {
        formatter.applyPattern(COMPLETE_DATE_PATTERN)
        return formatter.format(calendar.time)
    }

    override fun getYesterdayDateFormat(calendar: Calendar): String {
        formatter.applyPattern(TIME_DATE_PATTERN)
        return yesterdayText+" ${formatter.format(calendar.time)}"
    }

    companion object{
        private const val TIME_DATE_PATTERN = "h:mm a"
        private const val SAME_YEAR_DATE_PATTERN = "MMM d"
        private const val COMPLETE_DATE_PATTERN = "yyyy MMM d"
    }
}