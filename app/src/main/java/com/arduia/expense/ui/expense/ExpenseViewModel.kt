package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arduia.expense.data.AccRepository
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class ExpenseViewModel @ViewModelInject constructor(
    private val accMapper: ExpenseMapper,
    private val accRepo: AccRepository
) : ViewModel(), LifecycleObserver {

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading get() = _isLoading.asLiveData()

    private val _isSelectedMode = BaseLiveData<Boolean>()
    val isSelectedMode get() = _isSelectedMode.asLiveData()

    private val _deleteEvent = EventLiveData<Int>()
    val deleteEvent get() = _deleteEvent.asLiveData()

    private val _selectedItemChangeEvent = EventLiveData<Unit>()
    val selectedItemChangeEvent get() = _selectedItemChangeEvent.asLiveData()

    private val _detailDataChanged = EventLiveData<ExpenseDetailsVto>()
    val detailDataChanged get() = _detailDataChanged.asLiveData()

    private var livePagedListBuilder: LivePagedListBuilder<Int, ExpenseVto>? = null

    private val mSelectedItems = hashSetOf<Int>()

    fun onItemSelect(item: ExpenseVto) {
        addNewSelectedItemId(item.id)
        setSelectedModeOn()
        onSelectedItemChanged()
    }

    private fun onSelectedItemChanged() {
        _selectedItemChangeEvent post EventUnit
    }

    private fun setSelectedModeOn(){
        _isSelectedMode set true
    }

    private fun addNewSelectedItemId(id: Int) {
        mSelectedItems.add(id)
    }


    fun deleteConfirm() {
        viewModelScope.launch(Dispatchers.IO) {
            //There is no Items to Delete
            if (mSelectedItems.isEmpty()) {
                _isSelectedMode post true
                return@launch
            }
            //Start Delete Progress
            _isLoading.postValue(true)
            accRepo.deleteAllExpense(mSelectedItems.toList())
            _isLoading post false
            _deleteEvent post event(mSelectedItems.size)
            clearSelection()
        }
    }

    fun cancelDeletion() {
        clearSelection()
    }

    @ExperimentalCoroutinesApi
    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = accRepo.getExpense(selectedItem.id).first()
            val detailData = accMapper.mapToExpenseDetailVto(item)
            _detailDataChanged post event(detailData)
        }
    }

    private fun clearSelection() {
        mSelectedItems.clear()
        onSelectedItemChanged()
        _isSelectedMode post false
    }

    suspend fun getExpenseLiveData(): LiveData<PagedList<ExpenseVto>> {
        return createPagedListLiveData()
    }

    private suspend fun createPagedListLiveData(): LiveData<PagedList<ExpenseVto>> {
        val dataSourceFactory = accRepo.getExpenseSourceAll()
            .map { accMapper.mapToExpenseVto(it) }

        return LivePagedListBuilder(dataSourceFactory, 100)
            .also {
                livePagedListBuilder = it
            }.build()
    }

}
