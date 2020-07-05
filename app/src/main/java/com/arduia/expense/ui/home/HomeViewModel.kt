package com.arduia.expense.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.BaseLiveData
import com.arduia.expense.ui.common.EventLiveData
import com.arduia.expense.ui.common.event
import com.arduia.expense.ui.common.post
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat

class HomeViewModel(private val app:Application) : AndroidViewModel(app), LifecycleObserver{

    private val _recentData =  BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() =  _detailData.asLiveData()

    private val _weeklyCost = BaseLiveData<String>()
    val weeklyCost get() = _weeklyCost.asLiveData()

    private val currencyFormatter = DecimalFormat("###,###.0")

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

    fun selectItemForDetail(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepository.getExpense(selectedItem.id).first()
            val detailData = accMapper.mapToTransactionDetail(item)
            _detailData post event(detailData)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch(Dispatchers.IO){
            accRepository.getRecentExpense().collect {
                val value = it.map { trans ->  this@HomeViewModel.transactionMapper.mapToTransactionVto(trans) }
                _recentData.postValue(value)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _weeklyCost post currencyFormatter.format(0)
            accRepository.getCostPerWeek(0).collect {
                _weeklyCost post currencyFormatter.format(it)
            }
        }
    }
}
