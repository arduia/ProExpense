package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
                    private val expenseMapper: ExpenseMapper,
                    private val accRepository: AccRepository,
                    private val rateCalculator: ExpenseRateCalculator) : ViewModel(), LifecycleObserver{

    private val _recentData =  BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() = _detailData.asLiveData()

    private val _totalCost = BaseLiveData<Long>()
    val totalCost get() = _totalCost.asLiveData()

    private val _costRates = BaseLiveData<Map<Int,Int>>()
    val costRate get() = _costRates.asLiveData()

    fun selectItemForDetail(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepository.getExpense(selectedItem.id).first()
            val detailData = expenseMapper.mapToExpenseDetailVto(item)
            _detailData post event(detailData)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        observeRecentExpenses()
        observeWeekExpenses()
    }

    private fun observeRecentExpenses(){
        viewModelScope.launch(Dispatchers.IO){
            accRepository.getRecentExpense().collect(recentFlowCollector)
        }
    }

    private fun observeWeekExpenses(){
        viewModelScope.launch(Dispatchers.IO){
            accRepository.getWeekExpenses().collect(weekFlowCollector)
        }
    }

    private val weekFlowCollector : suspend (List<ExpenseEnt>) -> Unit = {

        val totalAmount = it.filter { expenseEnt ->
            //OutCome and Income Changes
            expenseEnt.category != ExpenseCategory.INCOME
        }.map { expenseEnt -> expenseEnt.amount }.sum()

        _totalCost.postValue(totalAmount)

        rateCalculator.setWeekExpenses(it)

        _costRates post rateCalculator.getRates()
    }

    private val recentFlowCollector : suspend (List<ExpenseEnt>) -> Unit = {
                val recentExpenses =
                    it.map { trans ->
                        this@HomeViewModel.expenseMapper.mapToExpenseVto(trans)
                    }
                _recentData.postValue(recentExpenses)
        }
}
