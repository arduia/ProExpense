package com.arduia.myacc.ui.transaction

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.common.*
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TransactionViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading  get() = _isLoading.asLiveData()

    private val _isSelectedMode = BaseLiveData<Boolean>()
    val isSelectedMode get() =   _isSelectedMode.asLiveData()

    private val _deleteEvent = EventLiveData<Int>()
    val deleteEvent = _deleteEvent.asLiveData()

    private val _itemSelectionChangeEvent = EventLiveData<Unit>()
    val itemSelectionChangeEvent get()  = _itemSelectionChangeEvent.asLiveData()

    private val _detailDataChanged = EventLiveData<TransactionDetailsVto>()
    val detailDataChanged get() = _detailDataChanged.asLiveData()

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val accMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val selectedItems = hashSetOf<Int>()

    private val accRepo by lazy {
        serviceLoader.getAccountingRepository()
    }

    fun onItemSelect(item: TransactionVto){
        selectedItems.add(item.id)
        _itemSelectionChangeEvent set EventUnit
        _isSelectedMode set true
    }

    fun deleteConfirm(){
        viewModelScope.launch(Dispatchers.IO){
            //There is no Items to Delete
            if(selectedItems.isEmpty()){
                _isSelectedMode.postValue(true)
                return@launch
            }
            //Start Delete Progress
            _isLoading.postValue(true)
            accRepo.deleteAllTransaction(selectedItems.toList())
            _isLoading.postValue(false)
            _deleteEvent post event(selectedItems.size)
            clearSelection()
        }
    }

    fun cancelDelete(){
        clearSelection()
    }

    @ExperimentalCoroutinesApi
    fun showDetailData(selectedItem: TransactionVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepo.getTransaction(selectedItem.id).first()
            val detailData = accMapper.mapToTransactionDetail(item)
            _detailDataChanged post event(detailData)
        }
    }

    private fun clearSelection(){
        selectedItems.clear()
        _itemSelectionChangeEvent post EventUnit
        _isSelectedMode post false
    }

    suspend fun getExpenseLiveData(): LiveData<PagedList<TransactionVto>> {
        val dataSource = accRepo.getAllTransaction()
            .map { accMapper.mapToTransactionVto(it) }

          return LivePagedListBuilder(dataSource , 50).build().asFlow()
              .flowOn(Dispatchers.IO)
              .asLiveData(Dispatchers.Main)
    }
}
