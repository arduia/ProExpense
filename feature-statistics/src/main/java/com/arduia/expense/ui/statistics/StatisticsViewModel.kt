package com.arduia.expense.ui.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.di.StatisticDateRange
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.onError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.util.*
import javax.inject.Inject

data class StatisticsUiState(
    val isEmptyExpenseData: Boolean = false,
    val dateRange: String = ""
)

sealed class StatisticsUiEffect {
    data class ShowFilter(val filterInfo: ExpenseLogFilterInfo) : StatisticsUiEffect()
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer,
    @StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
) : BaseViewModel<StatisticsUiState, StatisticsUiEffect>(StatisticsUiState()) {

    private val filterLimit = MutableLiveData<DateRange>()
    private val filterConstraint = MutableLiveData<DateRange>()

    val dateRange
        get() = filterConstraint.switchMap { MutableLiveData(createFilterInfo(it)) }

    val categoryStatisticList
        get() = filterConstraint.switchMap { MutableLiveData(createStatisticsFromFilter(it)) }

    init {
        observeDateRangeInfo()
        observeIsEmptyData()
    }

    private fun createStatisticsFromFilter(filter: DateRange): List<CategoryStatisticUiModel> {
        val expenses = expenseRepo.getExpenseRangeAsc(filter.start, filter.end, 0, Int.MAX_VALUE)
            .awaitValueOrError()
        return categoryAnalyzer.analyze(expenses)
    }

    private fun createFilterInfo(constraint: DateRange): String {
        return dateRangeFormatter.format(constraint.start, constraint.end)
    }

    private fun observeIsEmptyData() {
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess { setState { copy(isEmptyExpenseData = it <= 0) } }
            .launchIn(viewModelScope)
    }

    private fun observeDateRangeInfo() {
        expenseRepo.getMaxAndMiniDateRange()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                val dateRange = ExpenseDateRange(it.minDate, it.maxDate)
                filterConstraint.postValue(dateRange)
                filterLimit.postValue(dateRange)
            }
            .onError {
                val dateRange = ExpenseDateRange(0, Date().time)
                filterConstraint.postValue(dateRange)
                filterLimit.postValue(dateRange)
            }
            .launchIn(viewModelScope)
    }

    fun setFilter(filter: ExpenseLogFilterInfo) {
        filterConstraint.value = filter.dateRangeSelected
    }

    fun onFilterSelected() {
        val constraint = filterConstraint.value ?: return
        val limit = filterLimit.value ?: return
        sendEffect(StatisticsUiEffect.ShowFilter(
            ExpenseLogFilterInfo(dateRangeLimit = limit, dateRangeSelected = constraint, sorting = Sorting.DESC)
        ))
    }

    fun onRangeSelect(range: String) {}
}