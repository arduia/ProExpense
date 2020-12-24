package com.arduia.expense.ui.common.filter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.common.ext.setDayAsEnd
import com.arduia.expense.ui.common.ext.setDayAsStart
import com.arduia.mvvm.*
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFilterViewModel @ViewModelInject constructor() : ViewModel() {

    private val filterDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
    private val filterTimeFormat = SimpleDateFormat("h: mm a", Locale.ENGLISH)

    private val _startDateTime = BaseLiveData<Calendar>()

    val startDate
        get() = _startDateTime.switchMap {
            BaseLiveData(filterDateFormat.format(it.time))
        }

    val starTime
        get() = _startDateTime.switchMap {
            BaseLiveData(filterTimeFormat.format(it.time))
        }

    private val _endDateTime = BaseLiveData<Calendar>()

    val endDate
        get() = _endDateTime.switchMap {
            BaseLiveData(filterDateFormat.format(it.time))
        }

    val endTime
        get() = _endDateTime.switchMap {
            BaseLiveData(filterTimeFormat.format(it.time))
        }

    private val _sortingType = BaseLiveData<Sorting>()
    val sortingType get() = _sortingType.asLiveData()

    private val _onResult = EventLiveData<ExpenseLogFilterInfo>()
    val onResult get() = _onResult.asLiveData()

    private val startDateTimeLimit = BaseLiveData<Calendar>()
    private val endDateTimeLimit = BaseLiveData<Calendar>()

    private val _onStartDateFilterShow = EventLiveData<ExpenseLogFilterInfo>()
    val onStartDateFilterShow get() = _onStartDateFilterShow.asLiveData()

    private val _onEndDateFilterShow = EventLiveData<ExpenseLogFilterInfo>()
    val onEndDateFilterShow get() = _onEndDateFilterShow.asLiveData()

    private val _onStartTimeFilterShow = EventLiveData<Calendar>()
    val onStartTimeFilterShow get() = _onStartTimeFilterShow.asLiveData()

    private val _onEndTimeFilterShow = EventLiveData<Calendar>()
    val onEndTimeFilterShow get() = _onEndTimeFilterShow.asLiveData()

    private val _onStartChangedAsEnd = EventLiveData<Unit>()
    val onStartChangedAsEnd get() = _onStartChangedAsEnd.asLiveData()

    private val _onEndChangedAsStart = EventLiveData<Unit>()
    val onEndChangedAsStart get() = _onEndChangedAsStart.asLiveData()

    fun setFilterInfo(info: ExpenseLogFilterInfo) {
        setSelectedDateTime(info.dateRangeSelected)
        setLimitDateTime(info.dateRangeLimit)
        setSorting(info.sorting)
    }

    fun setSorting(sorting: Sorting) {
        _sortingType set sorting
    }

    private fun setLimitDateTime(range: DateRange) {
        startDateTimeLimit set Calendar.getInstance().apply {
            timeInMillis = range.start
        }.setDayAsStart()

        endDateTimeLimit set Calendar.getInstance().apply {
            timeInMillis = range.end
        }.setDayAsEnd()
    }

    private fun setSelectedDateTime(range: DateRange) {
        _startDateTime set Calendar.getInstance().apply {
            timeInMillis = range.start
        }

        _endDateTime set Calendar.getInstance().apply {
            timeInMillis = range.end
        }
    }

    fun applyFilter() {
        _onResult set event(getFilterInfo())
    }

    fun onStartTimeSelect() {
        _onStartTimeFilterShow set event(getStartDateTime())
    }

    fun onEndTimeSelect() {
        _onEndTimeFilterShow set event(getEndDateTime())
    }

    fun onStartDateSelect() {
        _onStartDateFilterShow set event(getFilterInfo())
    }

    fun onEndDateSelect() {
        _onEndDateFilterShow set event(getFilterInfo())
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int) {

        val startDateTime = getStartDateTime()
        val endDateTime = getEndDateTime()

        val startDateSelected = Calendar.getInstance().apply {
            set(
                year,
                month,
                dayOfMonth,
                startDateTime[Calendar.HOUR_OF_DAY],
                startDateTime[Calendar.MINUTE],
                startDateTime[Calendar.SECOND]
            )
        }

        changeStartDateTime(startDateSelected)

    }

    private fun getStartDateTime() =
        _startDateTime.value ?: throw Exception("selected start time not set yet!")

    private fun getEndDateTime() =
        _endDateTime.value ?: throw Exception("selected start time not set yet!")

    private fun getFilterInfo(): ExpenseLogFilterInfo {
        val start = getStartDateTime().timeInMillis
        val end = getEndDateTime().timeInMillis
        val startLimit = startDateTimeLimit.value!!.timeInMillis
        val endLimit = endDateTimeLimit.value!!.timeInMillis
        return ExpenseLogFilterInfo(
            dateRangeLimit = ExpenseDateRange(startLimit, endLimit),
            dateRangeSelected = ExpenseDateRange(start, end),
            sorting = sortingType.value!!
        )
    }

    fun setEndDate(year: Int, month: Int, dayOfMonth: Int) {

        val endTime = getEndDateTime()

        val endTimeSelected = Calendar.getInstance().apply {
            set(
                year,
                month,
                dayOfMonth,
                endTime[Calendar.HOUR_OF_DAY],
                endTime[Calendar.MINUTE],
                endTime[Calendar.SECOND]
            )
        }

        changeEndDateTime(endTimeSelected)

    }

    fun setStartTime(hourOfDay: Int, minute: Int) {
        val startDateTime = getStartDateTime()
        startDateTime[Calendar.HOUR_OF_DAY] = hourOfDay
        startDateTime[Calendar.MINUTE] = minute
        startDateTime[Calendar.SECOND] = 0
        //Notify Data Changed!
        Timber.d("startDate $startDateTime")

        changeStartDateTime(startDateTime)
    }

    fun setEndTime(hourOfDay: Int, minute: Int) {
        val endDateTime = getEndDateTime()
        endDateTime[Calendar.HOUR_OF_DAY] = hourOfDay
        endDateTime[Calendar.MINUTE] = minute
        endDateTime[Calendar.SECOND] = 59

        Timber.d("endDatetime $endDateTime")
        changeEndDateTime(endDateTime)
    }

    private fun changeEndDateTime(time: Calendar) {
        _endDateTime set time
        changeStartIfEndUnderflow(endTime = time)
    }


    private fun changeStartDateTime(time: Calendar) {
        _startDateTime set time
        changeEndIfStartOverflow(startTime = time)
    }

    private fun changeStartIfEndUnderflow(endTime: Calendar){
        val startTime = getStartDateTime()

        val isUnderflow = (endTime.timeInMillis < startTime.timeInMillis)
        if (isUnderflow) {
            _startDateTime set Calendar.getInstance().apply {
                timeInMillis = endTime.timeInMillis
            }.setDayAsStart()
            _onStartChangedAsEnd set EventUnit
        }
    }

    private fun changeEndIfStartOverflow(startTime: Calendar){
        val endDateTime = getEndDateTime()
        val isOverFlow = (startTime.timeInMillis > endDateTime.timeInMillis)
        if (isOverFlow) {
            _endDateTime set Calendar.getInstance().apply {
                timeInMillis = startTime.timeInMillis
            }.setDayAsEnd()
            _onEndChangedAsStart set EventUnit
        }
    }

}