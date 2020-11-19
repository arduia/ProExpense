package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.data
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class ExpenseViewModel @ViewModelInject constructor(
    private val mapper: ExpenseMapper,
    private val repo: ExpenseRepository
) : ViewModel() {

    private val _isDeleteMode = BaseLiveData<Boolean>()
    val isDeleteMode get() = _isDeleteMode.asLiveData()

    private val _detailDataChanged = EventLiveData<ExpenseDetailsVto>()
    val detailDataChanged get() = _detailDataChanged.asLiveData()

    private val _itemDeletedEvent = EventLiveData<Int>()
    val itemDeletedEvent get() = _itemDeletedEvent.asLiveData()

    private var livePagedListBuilder: LivePagedListBuilder<Int, ExpenseVto>? = null

    private val deletedItemList = mutableListOf<ExpenseEnt>()

    fun getExpenseLiveData(): LiveData<PagedList<ExpenseVto>> {
        return createPagedListLiveData()
    }

    fun deleteItem(item: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseEnt = getExpenseItemByID(item.id).data ?: return@launch
            repo.deleteExpense(expenseEnt)
            deletedItemList.add(expenseEnt)
            deletedModeOn()
        }
    }

    fun deleteItemById(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteExpenseById(id)
            _itemDeletedEvent post event(1)
        }
    }
    private suspend fun getExpenseItemByID(itemId: Int) =
        repo.getExpense(itemId).single()

    fun restoreDeletion(){
        viewModelScope.launch(Dispatchers.IO){
            restoreDeletedItem()
            clearDeletedItemList()
            deletedModeOff()
        }
    }

    @ExperimentalCoroutinesApi
    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getExpense(selectedItem.id).single()
            result.data?.let {
                val detailData = mapper.mapToDetailVto(it)
                _detailDataChanged post event(detailData)
            }?: return@launch //Error Show
        }
    }

    private fun createPagedListLiveData(): LiveData<PagedList<ExpenseVto>> {
        val dataSourceFactory = repo.getExpenseSourceAll()
            .map { mapper.mapToVto(it) }
        return LivePagedListBuilder(dataSourceFactory, 100)
            .also {
                livePagedListBuilder = it
            }.build()
    }

    private fun clearDeletedItemList() {
        deletedItemList.clear()
    }

    private fun deletedModeOn() {
        _isDeleteMode post true
    }

    private fun deletedModeOff() {
        _isDeleteMode post false
    }

    private suspend fun restoreDeletedItem() {
        repo.insertExpenseAll(deletedItemList)
    }

}
