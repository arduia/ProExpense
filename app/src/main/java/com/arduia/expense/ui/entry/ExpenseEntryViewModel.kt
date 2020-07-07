package com.arduia.expense.ui.entry

import android.app.Application
import androidx.lifecycle.*
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.ServiceLoader
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.*

class ExpenseEntryViewModel(private val app:Application) : AndroidViewModel(app){

    //--Caution-- Should be oneshot execution, Event LiveData
    private val _dataInserted = EventLiveData<Unit>()
    val dataInserted get() =  _dataInserted.asLiveData()

    private val _dataUpdated = EventLiveData<Unit>()
    val dataUpdated get() = _dataUpdated.asLiveData()

    private val _entryMode = EventLiveData<ExpenseEntryMode>()
    val entryMode get() = _entryMode

    private val _expenseData = BaseLiveData<ExpenseUpdateDataVto>()
    val expenseData get() = _expenseData.asLiveData()

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    private val accMapper: ExpenseMapper by lazy {
        serviceLoader.getTransactionMapper()
    }

    fun setUpdateMode(){
        _entryMode post event(ExpenseEntryMode.UPDATE)
    }

    fun setSaveMode(){
        _entryMode post event(ExpenseEntryMode.INSERT)
    }

    fun saveExpenseData(name: String,
                        cost:Long,
                        description: String,
                        category: String){

        viewModelScope.launch(Dispatchers.IO) {
            val saveTransaction = ExpenseEnt(
                name = name,
                value = cost,
                note = description,
                expense = "Income",
                category = category,
                finance_type = "CASH",
                created_date = Date().time,
                modified_date = Date().time
            )
            accRepository.insertExpense(saveTransaction)
            _dataInserted post EventUnit
        }
    }

    fun updateExpenseData(id: Int,
                      name: String,
                      cost:Long,
                      description: String,
                      category: String){
        viewModelScope.launch(Dispatchers.IO) {
            val saveTransaction = ExpenseEnt(
                expense_id = id,
                name = name,
                value = cost,
                note = description,
                expense = "Income",
                category = category,
                finance_type = "CASH",
                created_date = expenseData.value?.date?: throw Exception("Created Date Should not be null"),
                modified_date = Date().time
            )
            accRepository.updateExpense(saveTransaction)
            _dataUpdated post EventUnit
        }
    }

    fun observeExpenseData(id: Int){

        viewModelScope.launch(Dispatchers.IO){
            val repoData = accRepository.getExpense(id).first()

            val updateData = accMapper.mapToUpdateDetail(repoData)

            _expenseData post updateData
        }
    }

}
