package com.arduia.expense.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.model.onSuccess
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StatisticsViewModel @ViewModelInject constructor(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer
) : ViewModel() {

    private val _categoryStatisticList = BaseLiveData<List<CategoryStatisticVo>>()
    val categoryStatisticList get() = _categoryStatisticList.asLiveData()

    private val _dateRange = BaseLiveData<String>()
    val dateRange get() = _dateRange.asLiveData()

    private var expenseFilter = StatisticFilterEnt(startTime = 0L, endTime = Long.MAX_VALUE)

    private var observeExpenseJob: Job? = null

    init {
        observeExpenses()
    }

    fun setFilter(filter: StatisticFilterEnt) {
        this.expenseFilter = filter
    }

    private fun observeExpenses() {
        cleanObserveJob()
        observeExpenseJob = expenseRepo.getExpenseRangeAsc(
            expenseFilter.startTime,
            expenseFilter.endTime,
            0,
            Int.MAX_VALUE
        )
            .flowOn(Dispatchers.IO)
            .onSuccess {
                _categoryStatisticList post categoryAnalyzer.analyze(it)
            }
            .launchIn(viewModelScope)
    }

    private fun cleanObserveJob() {
        observeExpenseJob?.cancel()
        observeExpenseJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cleanObserveJob()
    }
}