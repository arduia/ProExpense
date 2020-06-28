package com.arduia.myacc.ui.transaction

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.mapping.AccountingMapper
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransactionViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _transactions = MutableLiveData<PagingData<TransactionVto>>()
    val transactions: LiveData<PagingData<TransactionVto>> get() = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val serviceLoader by lazy {
        ServiceLoader(app)
    }

    private val accMapper by lazy {
        AccountingMapper()
    }

    private val accRepo by lazy {
        serviceLoader.getAccountingRepository()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.postValue(true)
            accRepo.getAllTransaction().collect {
                _transactions.postValue(
                    it.map {
                            trans-> accMapper.mapToCostVto(trans)
                    }
                )
                _isLoading.postValue(false)
            }
        }
    }

}
