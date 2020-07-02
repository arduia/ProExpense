package com.arduia.myacc.ui.entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.di.ServiceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ExpenseEntryViewModel(private val app:Application) : AndroidViewModel(app){

    //--Caution-- Should be oneshot execution, Event LiveData
    private val _expenseDataInserted = MutableLiveData<Unit>()
    val expenseDataInserted : LiveData<Unit> = _expenseDataInserted

    private val serviceLoader by lazy {
        ServiceLoader.getInstance(app)
    }

    private val accRepository: AccRepository by lazy {
        serviceLoader.getAccountingRepository()
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
            _expenseDataInserted.postValue(Unit)
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
                created_date = Date().time,
                modified_date = Date().time
            )
            accRepository.insertTransaction(saveTransaction)
            _expenseDataInserted.postValue(Unit)
        }
    }
}