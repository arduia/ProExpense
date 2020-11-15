package com.arduia.expense.ui.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.*

class ExpenseEntryViewModel @ViewModelInject constructor(
    private val repo: ExpenseRepository,
    private val mapper: ExpenseMapper
) : ViewModel() {

    private val _insertedEvent = EventLiveData<Unit>()
    val insertedEvent get() = _insertedEvent.asLiveData()

    private val _onNext = EventLiveData<Unit>()
    val onNext get() = _onNext.asLiveData()

    private val _updatedEvent = EventLiveData<Unit>()
    val updatedEvent get() = _updatedEvent.asLiveData()

    private val _currentModeEvent = EventLiveData<ExpenseEntryMode>()
    val currentModeEvent get() = _currentModeEvent.asLiveData()

    private val _data = BaseLiveData<ExpenseUpdateDataVto>()
    val data get() = _data.asLiveData()

    private val _selectedCategory = BaseLiveData<ExpenseCategory>()
    val selectedCategory get() = _selectedCategory.asLiveData()

    private val _lockMode = BaseLiveData<LockMode>()
    val lockMode get() = _lockMode.asLiveData()

    private val _selectedDate = BaseLiveData<Long>()
    val selectedDate get() = _selectedDate.asLiveData()

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading get() = _isLoading

    init {
        _lockMode.value = LockMode.UNLOCK
        updateSelectedDateAsCurrentTime()
    }

    fun chooseUpdateMode() {
        _currentModeEvent post event(ExpenseEntryMode.UPDATE)
    }

    fun chooseSaveMode() {
        _currentModeEvent post event(ExpenseEntryMode.INSERT)
    }

    fun selectDateTime(time: Long) {
        _selectedDate post time
    }

    private fun loadingOn() {
        _isLoading post true
    }

    private fun loadingOff() {
        _isLoading post false
    }

    private fun onDataInserted() {
        val isLocked = lockMode.value ?: return
        if (isLocked == LockMode.LOCKED) {
            _onNext post EventUnit
            updateSelectedDateAsCurrentTime()
        } else {
            _insertedEvent post EventUnit
        }

    }

    private fun onDataUpdated() {
        _updatedEvent post EventUnit
    }

    fun invertLockMode() {
        when (lockMode.value ?: return) {
            LockMode.UNLOCK -> {
                _lockMode post LockMode.LOCKED
            }
            LockMode.LOCKED -> {
                _lockMode post LockMode.UNLOCK
            }
        }
    }

    fun updateExpenseData(expense: ExpenseDetailsVto) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingOn()
            val oldData = data.value
            val createdDate = oldData?.date
            val expenseEnt = mapToExpenseEnt(
                expense,
                createdDate, modifiedDate = selectedDate.value
            )
            repo.updateExpense(expenseEnt)
            onDataUpdated()
            loadingOff()
        }
    }

    private fun updateSelectedDateAsCurrentTime(){
        _selectedDate post Calendar.getInstance(Locale.getDefault()).timeInMillis
    }

    fun saveExpenseData(expense: ExpenseDetailsVto) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingOn()
            val expenseEnt = mapToExpenseEnt(expense, modifiedDate = selectedDate.value)
            repo.insertExpense(expenseEnt)
            onDataInserted()
            loadingOff()
        }
    }

    private fun mapToExpenseEnt(
        vto: ExpenseDetailsVto,
        createdDate: Long? = null,
        modifiedDate: Long? = null
    ) = ExpenseEnt(
        expenseId = vto.id,
        name = vto.name,
        amount = vto.amount.toFloat(),
        note = vto.note,
        category = vto.category,
        createdDate = createdDate ?: Date().time,
        modifiedDate = modifiedDate ?: Date().time
    )

    fun setCurrentExpenseId(id: Int) {
        observeExpenseData(id)
    }


    private fun observeExpenseData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dataEnt = repo.getExpense(id).first()
                _selectedDate post dataEnt.modifiedDate
                val dataVto = mapper.mapToUpdateDetailVto(dataEnt)
                _data post dataVto

            } catch (e: Exception) {
                Timber.d("Exception ${e.printStackTrace()}")
            }
        }
    }

    fun selectCategory(category: ExpenseCategory) {
        _selectedCategory post category
    }

}
