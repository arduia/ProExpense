package com.arduia.expense.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.vto.ExpenseVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val app:Application) : AndroidViewModel(app), LifecycleObserver{

    private val _recentData =  MutableLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val transactionMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        viewModelScope.launch(Dispatchers.IO){
            accRepository.getRecentExpense().collect {
                val value = it.map { trans ->  this@HomeViewModel.transactionMapper.mapToTransactionVto(trans) }
                _recentData.postValue(value)
            }
        }

    }
}
