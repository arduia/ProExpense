package com.arduia.expense.ui.entry

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class ExpenseEntryViewModel(private val accRepository: AccRepository,
                            private val accMapper: ExpenseMapper) : ViewModel(), LifecycleObserver{

    //--Caution-- Should be oneshot execution, Event LiveData
    private val _dataInserted = EventLiveData<Unit>()
    val dataInserted get() =  _dataInserted.asLiveData()

    private val _dataUpdated = EventLiveData<Unit>()
    val dataUpdated get() = _dataUpdated.asLiveData()

    private val _entryMode = EventLiveData<ExpenseEntryMode>()
    val entryMode get() = _entryMode

    private val _expenseData = BaseLiveData<ExpenseUpdateDataVto>()
    val expenseData get() = _expenseData.asLiveData()

    private val _selectedCategory = BaseLiveData<ExpenseCategory>()
    val selectedCategory get() = _selectedCategory.asLiveData()

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading get() = _isLoading

    fun setUpdateMode(){
        _entryMode post event(ExpenseEntryMode.UPDATE)
    }

    fun setSaveMode(){
        _entryMode post event(ExpenseEntryMode.INSERT)
    }

    fun saveExpenseData(detail: ExpenseDetailsVto){

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val saveExpenseEnt = ExpenseEnt(
                name = detail.name,
                amount = detail.amount.toLong(),
                note = detail.note,
                category = detail.category,
                created_date = Date().time,
                modified_date = Date().time
            )
            accRepository.insertExpense(saveExpenseEnt)
            _dataInserted post EventUnit
            _isLoading.postValue(false)
        }
    }

    fun updateExpenseData(detail: ExpenseDetailsVto){
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val updateExpenseEnt = ExpenseEnt(
                expense_id = detail.id,
                name = detail.name,
                amount = detail.amount.toLong(),
                note = detail.note,
                category = detail.category,
                created_date = expenseData.value?.date?: throw Exception("Created Date Should not be null"),
                modified_date = Date().time
            )
            accRepository.updateExpense(updateExpenseEnt)
            _dataUpdated post EventUnit
            _isLoading.postValue(false)
        }
    }

    fun setExpenseData(id: Int){
        observeExpenseData(id)
    }

    private fun observeExpenseData(id: Int){

        viewModelScope.launch(Dispatchers.IO){
            val repoData = accRepository.getExpense(id).first()

            val updateData = accMapper.mapToUpdateDetail(repoData)

            _expenseData post updateData
        }
    }

    fun selectCategory(category: ExpenseCategory){
        _selectedCategory post category
    }
}
