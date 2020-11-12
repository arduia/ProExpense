package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val mapper: ExpenseMapper,
    private val repo: ExpenseRepository,
    private val calculator: ExpenseRateCalculator) : ViewModel(), LifecycleObserver{

    private val _recentData =  BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() = _detailData.asLiveData()

    private val _totalCost = BaseLiveData<Float>()
    val totalCost get() = _totalCost.asLiveData()

    private val _costRates = BaseLiveData<Map<Int,Int>>()
    val costRate get() = _costRates.asLiveData()

    private val _onExpenseItemDeleted = EventLiveData<Unit>()
    val onExpenseItemDeleted get() = _onExpenseItemDeleted.asLiveData()


    fun selectItemForDetail(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = repo.getExpense(selectedItem.id).first()
            val detailData = mapper.mapToDetailVto(item)
            _detailData post event(detailData)
        }
    }

    fun deleteExpense(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteExpenseById(id)
            _onExpenseItemDeleted post EventUnit
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        observeRecentExpenses()
        observeWeekExpenses()
    }

    private fun observeRecentExpenses(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getRecentExpense().collect(recentFlowCollector)
        }
    }

    private fun observeWeekExpenses(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getWeekExpenses().collect(weekFlowCollector)
        }
    }

    private val weekFlowCollector : suspend (List<ExpenseEnt>) -> Unit = {

        val totalAmount = it.filter { expenseEnt ->
            //OutCome and Income Changes
            expenseEnt.category != ExpenseCategory.INCOME
        }.map { expenseEnt -> expenseEnt.amount }.sum()

        _totalCost.postValue(totalAmount)

        calculator.setWeekExpenses(it)

        _costRates post calculator.getRates()
    }

    private val recentFlowCollector : suspend (List<ExpenseEnt>) -> Unit = {
                val recentExpenses =
                    it.map { trans ->
                        this@HomeViewModel.mapper.mapToVto(trans)
                    }
                _recentData.postValue(recentExpenses)
        }
}
