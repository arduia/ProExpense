package com.arduia.expense.ui.entry

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

data class ExpenseEntryUiState(
    val expenseId: Long = 0,
    val isEditMode: Boolean = false,
    val name: String = "",
    val amount: String = "",
    val note: String = "",
    val selectedCategory: ExpenseCategory? = null,
    val currencySymbol: String = "",
    val currentEntryTime: Long = Date().time,
    val lockMode: LockMode = LockMode.UNLOCK,
    val entryMode: ExpenseEntryMode? = null,
    val entryData: ExpenseUpdateDataUiModel? = null,
    val isSaving: Boolean = false
)

sealed class ExpenseEntryUiEffect {
    object DataInserted : ExpenseEntryUiEffect()
    object DataUpdated : ExpenseEntryUiEffect()
    object Next : ExpenseEntryUiEffect()
    data class ShowDatePicker(val calendar: Calendar) : ExpenseEntryUiEffect()
    data class ShowTimePicker(val calendar: Calendar) : ExpenseEntryUiEffect()
}

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repo: ExpenseRepository,
    private val mapper: Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>,
    private val currencyRepo: CurrencyRepository
) : BaseViewModel<ExpenseEntryUiState, ExpenseEntryUiEffect>(ExpenseEntryUiState()) {

    init {
        setState { copy(lockMode = LockMode.UNLOCK, currentEntryTime = Date().time) }
        observeCurrencySymbol()
    }

    private fun observeCurrencySymbol() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .map { if (it is SuccessResult) it.data.code else "" }
            .onEach { symbol -> setState { copy(currencySymbol = symbol) } }
            .launchIn(viewModelScope)
    }

    fun chooseUpdateMode() {
        setState { copy(entryMode = ExpenseEntryMode.UPDATE) }
    }

    fun chooseSaveMode() {
        setState { copy(entryMode = ExpenseEntryMode.INSERT) }
    }

    fun selectDateTime(time: Long) {
        setState { copy(currentEntryTime = time) }
    }

    fun onDateSelect() {
        val calendar = getCurrentEntryDateTime()
        sendEffect(ExpenseEntryUiEffect.ShowDatePicker(calendar))
    }

    fun onTimeSelect() {
        val calendar = getCurrentEntryDateTime()
        sendEffect(ExpenseEntryUiEffect.ShowTimePicker(calendar))
    }

    private fun getCurrentEntryDateTime(): Calendar {
        val time = uiState.value.currentEntryTime
        return Calendar.getInstance().apply { timeInMillis = time }
    }

    fun selectTime(hour: Int, min: Int, milliSec: Int) {
        val time = Calendar.getInstance().apply {
            timeInMillis = uiState.value.currentEntryTime
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.MILLISECOND, milliSec)
        }
        setState { copy(currentEntryTime = time.timeInMillis) }
    }

    private fun onDataInserted() {
        val isLocked = uiState.value.lockMode
        if (isLocked == LockMode.LOCKED) {
            sendEffect(ExpenseEntryUiEffect.Next)
            setState { copy(currentEntryTime = Date().time) }
        } else {
            sendEffect(ExpenseEntryUiEffect.DataInserted)
        }
    }

    fun setLockMode(isLocked: Boolean) {
        setState { copy(lockMode = if (isLocked) LockMode.LOCKED else LockMode.UNLOCK) }
    }

    fun updateExpenseData(expense: ExpenseDetailUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isSaving = true) }
            val oldData = uiState.value.entryData
            val createdDate = oldData?.date
            val expenseEnt = mapToExpenseEnt(expense, createdDate, modifiedDate = uiState.value.currentEntryTime)
            repo.updateExpense(expenseEnt)
            setState { copy(isSaving = false) }
            sendEffect(ExpenseEntryUiEffect.DataUpdated)
        }
    }

    fun saveExpenseData(expense: ExpenseDetailUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isSaving = true) }
            val expenseEnt = mapToExpenseEnt(expense, modifiedDate = uiState.value.currentEntryTime)
            repo.insertExpense(expenseEnt)
            setState { copy(isSaving = false) }
            onDataInserted()
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
        loadExpenseData(id)
    }

    private fun loadExpenseData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.getExpense(id).awaitValueOrError()
                val dataVto = mapper.map(result)
                setState { copy(currentEntryTime = result.modifiedDate, entryData = dataVto) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectCategory(category: ExpenseCategory) {
        setState { copy(selectedCategory = category) }
    }

    fun onNameChange(name: String) {}
    fun onAmountChange(amount: String) {}
    fun onNoteChange(note: String) {}
    fun onCategorySelect(categoryId: Int) {}
    fun onSave() {}
}
