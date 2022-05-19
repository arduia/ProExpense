package com.arduia.expense.ui.entry

import androidx.lifecycle.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Exception
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class ExpenseEntryViewModel @Inject constructor(
    private val repo: ExpenseRepository,
    private val mapper: Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>,
    private val currencyRepo: CurrencyRepository
) : ViewModel() {

    private val _onDataInserted = EventLiveData<Unit>()
    val onDataInserted get() = _onDataInserted.asLiveData()

    private val _onNext = EventLiveData<Unit>()
    val onNext get() = _onNext.asLiveData()

    private val _onDataUpdated = EventLiveData<Unit>()
    val onDataUpdated get() = _onDataUpdated.asLiveData()

    private val _onCurrentModeChanged = EventLiveData<ExpenseEntryMode>()
    val onCurrentModeChanged get() = _onCurrentModeChanged.asLiveData()

    private val _entryData = BaseLiveData<ExpenseUpdateDataUiModel>()
    val entryData get() = _entryData.asLiveData()

    private val _selectedCategory = BaseLiveData<ExpenseCategory>()
    val selectedCategory get() = _selectedCategory.asLiveData()

    private val _lockMode = BaseLiveData<LockMode>()
    val lockMode get() = _lockMode.asLiveData()

    private val _currentEntryTime = BaseLiveData<Long>()
    val currentEntryTime get() = _currentEntryTime.asLiveData()

    private val _onChooseTimeShow = EventLiveData<Calendar>()
    val onChooseTimeShow get() = _onChooseTimeShow.asLiveData()

    private val _onChooseDateShow = EventLiveData<Calendar>()
    val onChooseDateShow get() = _onChooseDateShow.asLiveData()

    private val _isLoading = BaseLiveData<Boolean>()
    val isLoading get() = _isLoading

    val currencySymbol: LiveData<String> = currencyRepo.getSelectedCacheCurrency()
        .flowOn(Dispatchers.IO)
        .map {
            if (it is SuccessResult) it.data.code else ""
        }.asLiveData()

    init {
        _lockMode.value = LockMode.UNLOCK
        updateSelectedDateAsCurrentTime()
    }

    fun chooseUpdateMode() {
        _onCurrentModeChanged post event(ExpenseEntryMode.UPDATE)
    }

    fun chooseSaveMode() {
        _onCurrentModeChanged post event(ExpenseEntryMode.INSERT)
    }

    fun selectDateTime(time: Long) {
        _currentEntryTime set time
    }

    fun onDateSelect() {
        val date = getCurrentEntryDateTime()
        _onChooseDateShow set event(date)
    }

    fun onTimeSelect() {
        val time = getCurrentEntryDateTime()
        _onChooseTimeShow set event(time)
    }

    private fun getCurrentEntryDateTime(): Calendar {
        val time = _currentEntryTime.value ?: throw Exception("Any Time is Not Selected Yet!")
        return Calendar.getInstance().apply {
            timeInMillis = time
        }
    }

    fun selectTime(hour: Int, min: Int, milliSec: Int) {
        val selectedTimeMilli = _currentEntryTime.value ?: throw Exception("time is not selected yet")

        val time = Calendar.getInstance().apply {
            timeInMillis = selectedTimeMilli
        }
        time[Calendar.HOUR_OF_DAY] = hour
        time[Calendar.MINUTE] = min
        time[Calendar.MILLISECOND] = milliSec

        _currentEntryTime set time.timeInMillis
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
            _onDataInserted post EventUnit
        }

    }

    private fun onDataUpdated() {
        _onDataUpdated post EventUnit
    }

    fun invertLockMode() {
        when (lockMode.value ?: return) {
            LockMode.UNLOCK -> {
                _lockMode set LockMode.LOCKED
            }
            LockMode.LOCKED -> {
                _lockMode set LockMode.UNLOCK
            }
        }
    }

    fun updateExpenseData(expense: ExpenseDetailUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingOn()
            val oldData = entryData.value
            val createdDate = oldData?.date
            val expenseEnt = mapToExpenseEnt(
                expense,
                createdDate, modifiedDate = currentEntryTime.value
            )
            repo.updateExpense(expenseEnt)
            onDataUpdated()
            loadingOff()
        }
    }

    private fun updateSelectedDateAsCurrentTime() {
        _currentEntryTime post  Date().time
    }

    fun saveExpenseData(expense: ExpenseDetailUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingOn()
            val expenseEnt = mapToExpenseEnt(expense, modifiedDate = currentEntryTime.value)
            repo.insertExpense(expenseEnt)
            onDataInserted()
            loadingOff()
        }
    }

    private fun mapToExpenseEnt(
        vto: ExpenseDetailUiModel,
        createdDate: Long? = null,
        modifiedDate: Long? = null
    ) = ExpenseEnt(
        expenseId = vto.id,
        name = vto.name,
        amount = Amount.createFromActual(BigDecimal(vto.amount)),
        note = vto.note,
        category = vto.category,
        createdDate = createdDate ?: Date().time,
        modifiedDate = modifiedDate ?: Date().time
    )

    fun setCurrentExpenseId(id: Int) {
        updateExpenseData(id)
    }


    private fun updateExpenseData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.getExpense(id).awaitValueOrError()
                _currentEntryTime post result.modifiedDate
                val dataVto = mapper.map(result)
                _entryData post dataVto

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectCategory(category: ExpenseCategory) {
        _selectedCategory post category
    }

}
