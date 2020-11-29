package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.home.ExpenseDetailMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.*


class ExpenseViewModel @ViewModelInject constructor(
    private val expenseVoMapper: Mapper<ExpenseEnt, ExpenseVto>,
    private val expenseDetailMapper: Mapper<ExpenseEnt, ExpenseDetailsVto>,
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
            val expenseEnt = getExpenseItemByID(item.id)
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
        repo.getExpense(itemId).awaitValueOrError()


    fun restoreDeletion(){
        viewModelScope.launch(Dispatchers.IO){
            restoreDeletedItem()
            clearDeletedItemList()
            deletedModeOff()
        }
    }

    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getExpense(selectedItem.id).awaitValueOrError()
            _detailDataChanged post event(expenseDetailMapper.map(result))
        }
    }

    private fun createPagedListLiveData(): LiveData<PagedList<ExpenseVto>> {
        val dataSourceFactory = repo.getExpenseSourceAll()
            .map(expenseVoMapper::map)
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
