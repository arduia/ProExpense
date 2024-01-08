package com.arduia.expense.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.di.StatisticDateRange
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.onError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.mvvm.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer,
    @StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
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

    private val _isEmptyExpenseData = BaseLiveData<Boolean>()
    val isEmptyExpenseData get() = _isEmptyExpenseData.asLiveData()


    val categoryStatisticList
        get() = filterConstraint.switchMap {
            BaseLiveData(createStatisticsFromFilter(it))
        }

    private fun createStatisticsFromFilter(filter: DateRange): List<CategoryStatisticUiModel> {
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
        observeIsEmptyData()
    }

    private fun observeIsEmptyData() {
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                if (it <= 0) {
                    _isEmptyExpenseData post true
                } else {
                    _isEmptyExpenseData post false
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeDateRangeInfo() {
        expenseRepo.getMaxAndMiniDateRange()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                val dateRange = ExpenseDateRange(it.minDate, it.maxDate)
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