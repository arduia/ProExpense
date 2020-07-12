package com.arduia.expense.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val app:Application) : AndroidViewModel(app), LifecycleObserver{

    private val _recentData =  BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() = _detailData.asLiveData()

    private val _totalCost = BaseLiveData<Long>()
    val totalCost get() = _totalCost.asLiveData()

    private val _costRates = BaseLiveData<Map<Int,Int>>()
    val costRate get() = _costRates.asLiveData()

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val transactionMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    private val accMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val rateCalculator:ExpenseRateCalculator by lazy {
        ExpenseRateCalculatorImpl()
    }

    fun selectItemForDetail(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepository.getExpense(selectedItem.id).first()
            val detailData = accMapper.mapToTransactionDetail(item)
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
                        this@HomeViewModel.transactionMapper.mapToTransactionVto(trans)
                    }
                _recentData.postValue(recentExpenses)
        }
}
