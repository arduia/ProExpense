package com.arduia.myacc.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val app:Application) : AndroidViewModel(app), LifecycleObserver{

    private val _recentData =  MutableLiveData<List<TransactionVto>>()
    val recentData get() = _recentData

    private val _recentDataInsertedEvent = MutableLiveData<Unit>()
    val recentDataInserted : LiveData<Unit> = _recentDataInsertedEvent

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val transactionMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    fun saveSpendData(transaction: Transaction){
        viewModelScope.launch(Dispatchers.IO) {
            accRepository.insertTransaction(transaction)
            _recentDataInsertedEvent.postValue(Unit)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){

        viewModelScope.launch(Dispatchers.IO){
            accRepository.getRecentTransaction().collect {
                val value = it.map { trans ->  this@HomeViewModel.transactionMapper.mapToCostVto(trans) }
                _recentData.postValue(value)
            }
        }

    }
}
