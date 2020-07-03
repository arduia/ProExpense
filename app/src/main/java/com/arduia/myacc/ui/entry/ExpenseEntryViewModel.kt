package com.arduia.myacc.ui.entry

import android.app.Application
import androidx.lifecycle.*
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.di.ServiceLoader
import com.arduia.myacc.ui.common.*
import com.arduia.myacc.ui.mapping.TransactionMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    private val _expenseData = BaseLiveData<UpdateDataVto>()
    val expenseData get() = _expenseData.asLiveData()

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
    }

    private val accMapper: TransactionMapper by lazy {
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
            val saveTransaction = Transaction(
                name = name,
                value = cost,
                note = description,
                expense = "Income",
                category = category,
                finance_type = "CASH",
                created_date = Date().time,
                modified_date = Date().time
            )
            accRepository.insertTransaction(saveTransaction)
            _dataInserted post EventUnit
        }
    }

    fun updateExpenseData(id: Int,
                      name: String,
                      cost:Long,
                      description: String,
                      category: String){
        viewModelScope.launch(Dispatchers.IO) {
            val saveTransaction = Transaction(
                transaction_id = id,
                name = name,
                value = cost,
                note = description,
                expense = "Income",
                category = category,
                finance_type = "CASH",
                created_date = expenseData.value?.date?: throw Exception("Created Date Should not be null"),
                modified_date = Date().time
            )
            accRepository.updateTransaction(saveTransaction)
            _dataUpdated post EventUnit
        }
    }

    fun observeExpenseData(id: Int){

        viewModelScope.launch(Dispatchers.IO){
            val repoData = accRepository.getTransaction(id).first()

            val updateData = accMapper.mapToUpdateDetail(repoData)

            _expenseData post updateData
        }
    }
}