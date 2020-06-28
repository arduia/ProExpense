package com.arduia.myacc.ui.home

import android.app.Application
import android.app.Service
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.AccRepositoryImpl
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.mapping.AccountingMapper
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val app:Application) : AndroidViewModel(app), LifecycleObserver{

    private val _recentData =  MutableLiveData<List<CostVto>>()
    val recentData get() = _recentData

    private val serviceLoader by lazy {
        ServiceLoader(app)
    }

    private val accountingMapper by lazy {
        AccountingMapper()
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    fun saveSpendData(transaction: Transaction){
        viewModelScope.launch {
            accRepository.insertTransaction(transaction)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){

        viewModelScope.launch(Dispatchers.IO){
            accRepository.getRecentTransaction().collect {
                _recentData.postValue(it.map { tran -> accountingMapper.mapToCostVto(tran) })
            }
        }

    }
}
