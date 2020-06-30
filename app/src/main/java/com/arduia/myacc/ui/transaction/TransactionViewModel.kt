package com.arduia.myacc.ui.transaction

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.vto.TransactionVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransactionViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _transactions = MutableLiveData<PagingData<TransactionVto>>()
    val transactions: LiveData<PagingData<TransactionVto>> get() = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSelectedMode = MutableLiveData<Boolean>()
    val isSelectedMode: LiveData<Boolean> = _isSelectedMode

    //Should be Event Observer
    private val _notifyMessage = MutableLiveData<String>()
    val notifyMessage : LiveData<String>  = _notifyMessage

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val accMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    private val selectedItems = mutableListOf<Int>()

    private val accRepo by lazy {
        serviceLoader.getAccountingRepository()
    }

    fun onItemSelect(item: TransactionVto){
        selectedItems.add(item.id)
        _isSelectedMode.postValue(true)
    }

    private fun clearSelection(){
        selectedItems.clear()
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
            _notifyMessage.postValue("${selectedItems.size} is Deleted")
            clearSelection()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        _isSelectedMode.value = false
        observeTransaction()
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
