package com.arduia.expense.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.common.ext.setDayAsEnd
import com.arduia.expense.ui.common.ext.setDayAsStart
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.RangeSortingFilterEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.formatter.ExpenseDateRangeFormatter
import com.arduia.mvvm.*
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


    private val filterLimit = BaseLiveData<DateRange>()
    private val filterConstraint = BaseLiveData<DateRange>()

    private val _filterRestored = EventLiveData<Unit>()
    val filterRestored get() = _filterRestored.asLiveData()

    private val _onFilterShow = EventLiveData<ExpenseLogFilterInfo>()
    val onFilterShow get() = _onFilterShow.asLiveData()

    val dateRange
        get() = filterConstraint.switchMap {
            BaseLiveData(createFilterInfo(it))
        }

    val categoryStatisticList
        get() = filterConstraint.switchMap {
            BaseLiveData(createStatisticsFromFilter(it))
        }

    private fun createStatisticsFromFilter(filter: DateRange): List<CategoryStatisticVo> {
        val expenses = expenseRepo.getExpenseRangeAsc(
            filter.start,
            filter.end,
            0,
            Int.MAX_VALUE
        )
            .awaitValueOrError()
        return categoryAnalyzer.analyze(expenses)
    }

    private fun createFilterInfo(constraint: DateRange): String {
        return dateRangeFormatter.format(constraint.start, constraint.end)
    }

    init {
        observeDateRangeInfo()
    }

    private fun observeDateRangeInfo() {
        expenseRepo.getMaxAndMiniDateRange()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                val dateRange = ExpenseDateRange(it.minDate, it.maxDate)
                if(filterConstraint.value != null){
                    _filterRestored post EventUnit
                }
                filterConstraint post dateRange
                filterLimit post dateRange
            }
            .onError {
                val dateRange = ExpenseDateRange(0, Date().time)
                filterConstraint post dateRange
                filterLimit post dateRange
            }
            .launchIn(viewModelScope)
    }

    fun setFilter(filter: ExpenseLogFilterInfo) {
        this.filterConstraint set filter.dateRangeSelected
    }

    fun onFilterSelected() {
        val constraint = filterConstraint.value ?: return
        val limit = filterLimit.value ?: return
        _onFilterShow post event(
            ExpenseLogFilterInfo(
                dateRangeLimit = limit,
                dateRangeSelected = constraint, sorting = Sorting.DESC
            )
        )
    }

}