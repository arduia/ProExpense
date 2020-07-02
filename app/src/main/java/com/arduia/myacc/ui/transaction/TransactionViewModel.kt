package com.arduia.myacc.ui.transaction

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.arduia.myacc.R
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class TransactionViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSelectedMode = MutableLiveData<Boolean>()
    val isSelectedMode: LiveData<Boolean> = _isSelectedMode

    //--Caution-- Should be Event LiveData for once shot execution
    private val _itemSelectionChangeEvent = MutableLiveData<Unit>()
    val itemSelectionChangeEvent : LiveData<Unit> = _itemSelectionChangeEvent

    //Should be Event Observer
    private val _notifyMessage = MutableLiveData<String>()
    val notifyMessage : LiveData<String>  = _notifyMessage

    //--Caution-- Should be oneshot execution, Event LiveData
    private val _detailDataChanged = MutableLiveData<TransactionDetailsVto>()
    val detailDataChanged : LiveData<TransactionDetailsVto> = _detailDataChanged

    private val _editDataChangedEvent = MutableLiveData<TransactionDetailsVto>()
    val editDataChangedEvent : LiveData<TransactionDetailsVto> = _editDataChangedEvent

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
        _itemSelectionChangeEvent.value = Unit
        _isSelectedMode.value = true
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
            _notifyMessage.postValue("${selectedItems.size} Items Deleted")
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
            _detailDataChanged.postValue(detailData)
        }
    }

    private fun clearSelection(){
        selectedItems.clear()
        _itemSelectionChangeEvent.postValue(Unit)
        _isSelectedMode.postValue(false)
    }

    @ExperimentalCoroutinesApi
    suspend fun getAllTransactions() =
        accRepo.getAllTransaction()
            .flowOn(Dispatchers.IO)
            .map {
                it.filter { transVto ->
                    !selectedItems.contains(transVto.transaction_id)
                } .map {
                transaction -> accMapper.mapToCostVto(transaction)
            }
            }.asLiveData(Dispatchers.IO)

}
