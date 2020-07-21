package com.arduia.expense.ui.expense

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.AccRepositoryImpl
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ExpenseViewModel @ViewModelInject constructor(
                    private val accMapper: ExpenseMapper,
                    private val accRepo: AccRepository) : ViewModel(), LifecycleObserver{

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading  get() = _isLoading.asLiveData()

    private val _isSelectedMode = BaseLiveData<Boolean>()
    val isSelectedMode get() =   _isSelectedMode.asLiveData()

    private val _deleteEvent = EventLiveData<Int>()
    val deleteEvent = _deleteEvent.asLiveData()

    private val _detailDataChanged = EventLiveData<ExpenseDetailsVto>()
    val detailDataChanged get() = _detailDataChanged.asLiveData()

    private var livePagedListBuilder: FilterableLivePagedListBuilder<Int,ExpenseVto>? = null

    private val mSelectedItems = hashSetOf<Int>()


    fun onItemSelect(item: ExpenseVto){
        mSelectedItems.add(item.id)
       onSelectedItemChanged()
        _isSelectedMode set true
    }

    private fun onSelectedItemChanged(){
        livePagedListBuilder?.invalidate()
    }

    fun deleteConfirm(){
        viewModelScope.launch(Dispatchers.IO){
            //There is no Items to Delete
            if(mSelectedItems.isEmpty()){
                _isSelectedMode.postValue(true)
                return@launch
            }
            //Start Delete Progress
            _isLoading.postValue(true)
            accRepo.deleteAllExpense(mSelectedItems.toList())
            _isLoading.postValue(false)
            _deleteEvent post event(mSelectedItems.size)
            clearSelection()
        }
    }

    fun cancelDelete(){
        clearSelection()
    }

    @ExperimentalCoroutinesApi
    fun selectItemForDetail(selectedItem: ExpenseVto){
        viewModelScope.launch(Dispatchers.IO){
            val item = accRepo.getExpense(selectedItem.id).first()
            val detailData = accMapper.mapToExpenseDetailVto(item)
            _detailDataChanged post event(detailData)
        }
    }
    private fun clearSelection(){
        mSelectedItems.clear()
        onSelectedItemChanged()
        _isSelectedMode post false
    }

    suspend fun getExpenseLiveData(): LiveData<PagedList<ExpenseVto>> {
        livePagedListBuilder = createLivePagedList()
        return livePagedListBuilder!!.build()
    }

    private suspend fun createLivePagedList(): FilterableLivePagedListBuilder<Int, ExpenseVto> {
        val dataSourceFactory = accRepo.getAllExpense()
            .map { accMapper.mapToExpenseVto(it) }

        return FilterableLivePagedListBuilder(dataSourceFactory,10)
            .filter {item -> !mSelectedItems.contains(item.id) }
    }
}
