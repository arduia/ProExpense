package com.arduia.myacc.ui.transaction

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.arduia.myacc.R
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class TransactionViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _transactions = MutableLiveData<PagingData<TransactionVto>>()
    val transactions: LiveData<PagingData<TransactionVto>> get() = _transactions

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

    private val selectedItems = mutableListOf<Int>()


    private val enterAnimDuration = app.resources.getInteger(R.integer.expense_anim_left_duration)

    private val accRepo by lazy {
        serviceLoader.getAccountingRepository()
    }

    fun onItemSelect(item: TransactionVto){
        selectedItems.add(item.id)
        _itemSelectionChangeEvent.postValue(Unit)
        _isSelectedMode.postValue(true)
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

    fun deleteConfirm(){
        viewModelScope.launch(Dispatchers.IO){

            //There is no Items to Delete
            if(selectedItems.isEmpty()){
                 _isSelectedMode.postValue(true)
                return@launch
            }

            //Start Delete Progress
            _isLoading.postValue(true)
            accRepo.deleteAllTransaction(selectedItems)
            _isLoading.postValue(false)
            _notifyMessage.postValue("${selectedItems.size} Items Deleted")
            clearSelection()
        }
    }

    fun cancelDelete(){
        clearSelection()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){

        viewModelScope.launch(Dispatchers.IO){
            //Wait for Animation Finished
            delay(enterAnimDuration.toLong()+100)
            observeTransaction()
        }
    }

    private fun observeTransaction(){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.postValue(true)
            accRepo.getAllTransaction().collect {
                _transactions.postValue(
                    it.map {
                            trans -> accMapper.mapToCostVto(trans)
                    } .filter {
                            vto -> !selectedItems.contains(vto.id)
                    }
                )
                _isLoading.postValue(false)
            }
        }
    }

}
