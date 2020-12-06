package com.arduia.expense.ui.expense.mapper

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.expense.ExpenseLogVo
import java.text.DateFormat
import java.util.*

class ExpenseLogTransform (
    private val headerDateFormat: DateFormat,
    private val logMapper: Mapper<ExpenseEnt, ExpenseLogVo.Log>
) : Mapper<List<ExpenseEnt>, List<ExpenseLogVo>> {

    private val timeHolder = Calendar.getInstance()
    private var monthHolder = -1
    private var yearHolder = -1

    override fun map(input: List<ExpenseEnt>): List<ExpenseLogVo> {
        val list = mutableListOf<ExpenseLogVo>()

        val sortedList = input /*.sortedBy { ent -> ent.createdDate } */

        sortedList.forEach { ent ->
            val date = ent.modifiedDate
            if (isMonthOrYearChangedAndUpdate(date)) {
                val headerVo = createHeaderVo(date)
                list.add(headerVo)
            }
            list.add(logMapper.map(ent))
        }
        return list
    }

    private fun createHeaderVo(time: Long): ExpenseLogVo.Header {
        timeHolder.timeInMillis = time
        val date = timeHolder.time
        return ExpenseLogVo.Header(headerDateFormat.format(date))
    }

    private fun isMonthOrYearChangedAndUpdate(time: Long): Boolean {
        timeHolder.timeInMillis = time
        val currentMonth = timeHolder.get(Calendar.MONTH)
        val currentYear = timeHolder.get(Calendar.YEAR)
        val isChanged = (currentMonth != monthHolder || currentYear != yearHolder)

        if(isChanged){
            monthHolder = currentMonth
            yearHolder = currentYear
        }

        return isChanged
    }
}