package com.arduia.expense.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.common.ext.setDayAsEnd
import com.arduia.expense.ui.common.ext.setDayAsStart
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.RangeSortingFilterEnt
import com.arduia.expense.ui.common.filter.Sorting
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
import java.lang.Exception
import java.util.*

class StatisticsViewModel @ViewModelInject constructor(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer,
    private val dateRangeFormatter: DateRangeFormatter
) : ViewModel() {


    private val filterConstraint = BaseLiveData<DateRangeSortingEnt>()

    private val _onFilterShow = EventLiveData<RangeSortingFilterEnt>()
    val onFilterShow get() = _onFilterShow.asLiveData()

    private lateinit var filterLimit: DateRangeSortingEnt

    val dateRange
        get() = filterConstraint.switchMap {
            BaseLiveData(createFilterInfo(it))
        }

    val categoryStatisticList get() = filterConstraint.switchMap {
       BaseLiveData( createStatisticsFromFilter(it))
    }

    private fun createStatisticsFromFilter(filter: DateRangeSortingEnt): List<CategoryStatisticVo>{
        val expenses = expenseRepo.getExpenseRangeAsc(
            filter.start,
            filter.end,
            0,
            Int.MAX_VALUE
        )
            .awaitValueOrError()
        return categoryAnalyzer.analyze(expenses)
    }

    private fun createFilterInfo(constraint: DateRangeSortingEnt): String {
        return dateRangeFormatter.format(constraint.start, constraint.end)
    }

    init {
        setDefaultDateRange()
    }

    private fun setDefaultDateRange() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateRecent = expenseRepo.getMostRecentDateSync().getDataOrError()
                val dateLatest = expenseRepo.getMostLatestDateSync().getDataOrError()
                filterConstraint post DateRangeSortingEnt(dateRecent, dateLatest, Sorting.DESC)
                filterLimit = DateRangeSortingEnt(dateRecent, dateLatest)
            } catch (e: Exception) {
                filterConstraint post DateRangeSortingEnt(0, 0, Sorting.ASC)
                filterLimit = DateRangeSortingEnt(0, 0)
            }
        }
    }

    fun setFilter(filter: DateRangeSortingEnt) {
        this.filterConstraint post filter
    }

    fun onFilterSelected() {
        val constraint  = filterConstraint.value?: return
        _onFilterShow post event(RangeSortingFilterEnt(constraint, filterLimit))
    }

}