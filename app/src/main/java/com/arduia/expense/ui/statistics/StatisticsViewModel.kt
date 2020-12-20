package com.arduia.expense.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.common.ext.setDayAsEnd
import com.arduia.expense.ui.common.ext.setDayAsStart
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.RangeSortingFilterEnt
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class StatisticsViewModel @ViewModelInject constructor(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer,
    private val dateRangeFormatter: DateRangeFormatter
) : ViewModel() {

    private val _categoryStatisticList = BaseLiveData<List<CategoryStatisticVo>>()
    val categoryStatisticList get() = _categoryStatisticList.asLiveData()

    private val _dateRange = BaseLiveData<String>()
    val dateRange get() = _dateRange.asLiveData()

    private val _onFilterShow = EventLiveData<RangeSortingFilterEnt>()
    val onFilterShow get() = _onFilterShow.asLiveData()

    private lateinit var dateRangeFilter: DateRangeSortingEnt
    private lateinit var dateRangeLimit: DateRangeSortingEnt

    private var observeExpenseJob: Job? = null

    init {
        setDefaultDateRange()
        configDefaultDateRange()
        showDateRange()
        updateStatistics()
    }

    private fun setDefaultDateRange() {
        val defaultTime = Calendar.getInstance()
        val startTime = defaultTime.setDayAsStart().timeInMillis
        val endTime = defaultTime.setDayAsEnd().timeInMillis
        dateRangeFilter = DateRangeSortingEnt(startTime, endTime)
        dateRangeLimit = dateRangeFilter
    }

    fun setFilter(filter: DateRangeSortingEnt) {
        this.dateRangeFilter = filter
        showDateRange()
        updateStatistics()
    }

    fun onFilterSelected() {
        _onFilterShow post event(RangeSortingFilterEnt(dateRangeFilter, dateRangeLimit))
    }

    private fun showDateRange() {
        _dateRange post dateRangeFormatter.format(dateRangeFilter.start, dateRangeFilter.end)
    }

    private fun configDefaultDateRange() {
        viewModelScope.launch(Dispatchers.IO) {
            val expense = expenseRepo.getExpenseAllSync().getDataOrError()
            val maxTime = expense.maxOfOrNull { it.modifiedDate }?: return@launch
            val minTime = expense.maxOfOrNull { it.modifiedDate }?: return@launch
            dateRangeLimit = DateRangeSortingEnt(minTime, maxTime)
            dateRangeFilter = dateRangeLimit
            showDateRange()
            updateStatistics()
        }
    }

    private fun updateStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("updateStatistics")
            val expenses = expenseRepo.getExpenseRangeAsc(
                dateRangeFilter.start,
                dateRangeFilter.end,
                0,
                Int.MAX_VALUE
            )
                .awaitValueOrError()
            Timber.d("expenses $expenses")
            val statistics = categoryAnalyzer.analyze(expenses)
            Timber.d("statistics $statistics")
            _categoryStatisticList post statistics
        }
    }

//    private fun observeExpenses() {
//        cleanObserveJob()
//        observeExpenseJob = expenseRepo.getExpenseRangeAsc(
//            dateRangeFilter.start,
//            dateRangeFilter.end,
//            0,
//            Int.MAX_VALUE
//        )
//            .flowOn(Dispatchers.IO)
//            .onSuccess {
//                _categoryStatisticList post categoryAnalyzer.analyze(it)
//            }
//            .launchIn(viewModelScope)
//    }

    private fun cleanObserveJob() {
        observeExpenseJob?.cancel()
        observeExpenseJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cleanObserveJob()
    }
}