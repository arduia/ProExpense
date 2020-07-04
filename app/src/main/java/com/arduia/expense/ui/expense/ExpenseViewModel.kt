package com.arduia.expense.ui.expense

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExpenseViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver{

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading  get() = _isLoading.asLiveData()

    private val _isSelectedMode = BaseLiveData<Boolean>()
    val isSelectedMode get() =   _isSelectedMode.asLiveData()

    private val _deleteEvent = EventLiveData<Int>()
    val deleteEvent = _deleteEvent.asLiveData()

    private val _itemSelectionChangeEvent = EventLiveData<Unit>()
    val itemSelectionChangeEvent get()  = _itemSelectionChangeEvent.asLiveData()

    private val _detailDataChanged = EventLiveData<ExpenseDetailsVto>()
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

    fun onItemSelect(item: ExpenseVto){
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
            accRepo.deleteAllExpense(selectedItems.toList())
            _isLoading.postValue(false)
            _deleteEvent post event(selectedItems.size)
            clearSelection()
        }
    }

    fun cancelDelete(){
        clearSelection()
    }

    @ExperimentalCoroutinesApi
    fun showDetailData(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepo.getExpense(selectedItem.id).first()
            val detailData = accMapper.mapToTransactionDetail(item)
            _detailDataChanged post event(detailData)
        }
    }

    private fun clearSelection(){
        selectedItems.clear()
        _itemSelectionChangeEvent post EventUnit
        _isSelectedMode post false
    }

    suspend fun getExpenseLiveData(): LiveData<PagedList<ExpenseVto>> {
        val dataSource = accRepo.getAllExpense()
            .map { accMapper.mapToTransactionVto(it) }

          return LivePagedListBuilder(dataSource , 50).build().asFlow()
              .flowOn(Dispatchers.IO)
              .asLiveData(Dispatchers.Main)
    }
}
