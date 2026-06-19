package com.arduia.expense.feature.history

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.formatWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DebtRecordUiState(
    val id: String,
    val personName: String,
    val amount: String,
    val dueLabel: String,
    val isSettled: Boolean = false,
)

data class DebtTrackerUiState(
    val lentRecords: List<DebtRecordUiState> = emptyList(),
    val oweRecords: List<DebtRecordUiState> = emptyList(),
)

class DebtTrackerViewModel(
    private val debtRepository: DebtRepository,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(DebtTrackerUiState())
    val uiState: StateFlow<DebtTrackerUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            when (val result = debtRepository.getAll()) {
                is Result.Success -> {
                    val lent = result.data.filter { it.direction == DebtDirection.OWED_TO_ME }.map { it.toUi(homeCurrencyCode) }
                    val owe = result.data.filter { it.direction == DebtDirection.I_OWE }.map { it.toUi(homeCurrencyCode) }
                    _uiState.update { it.copy(lentRecords = lent, oweRecords = owe) }
                }
                is Result.Error -> Unit
            }
        }
    }

    private fun Debt.toUi(currency: String) = DebtRecordUiState(
        id = id,
        personName = personName,
        amount = amount.formatWithSymbol(currency),
        dueLabel = "Due",
        isSettled = isSettled,
    )
}

data class DebtAddUiState(
    val personName: String = "",
    val amountText: String = "",
    val saveError: String? = null,
)

class DebtAddViewModel(
    private val debtRepository: DebtRepository,
    private val isLent: Boolean,
    private val scope: CoroutineScope,
    private val onSaved: () -> Unit,
) {
    private val _uiState = MutableStateFlow(DebtAddUiState())
    val uiState: StateFlow<DebtAddUiState> = _uiState.asStateFlow()

    fun onPersonNameChange(name: String) {
        _uiState.update { it.copy(personName = name.take(30), saveError = null) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amountText = amount, saveError = null) }
    }

    fun onSave() {
        val state = _uiState.value
        val cents = state.amountText.replace(",", "").toDoubleOrNull()?.let { (it * 100).toLong() }
        if (state.personName.isBlank()) return
        if (cents == null || cents <= 0) {
            _uiState.update { it.copy(saveError = "Amount must be greater than zero") }
            return
        }
        scope.launch {
            val debt = Debt(
                id = "debt_${Random.nextLong().toString(16)}",
                personName = state.personName.trim(),
                amount = Amount(cents),
                direction = if (isLent) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE,
            )
            when (debtRepository.upsert(debt)) {
                is Result.Success -> onSaved()
                is Result.Error -> _uiState.update { it.copy(saveError = "Could not save") }
            }
        }
    }
}

data class DebtDetailUiState(
    val personName: String = "",
    val isLent: Boolean = true,
    val amount: String = "",
    val dueLabel: String = "",
    val isSettled: Boolean = false,
    val isLoading: Boolean = true,
)

class DebtDetailViewModel(
    private val debtId: String,
    private val debtRepository: DebtRepository,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
    private val onDeleted: () -> Unit,
) {
    private val _uiState = MutableStateFlow(DebtDetailUiState())
    val uiState: StateFlow<DebtDetailUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            when (val result = debtRepository.getById(debtId)) {
                is Result.Success -> {
                    val debt = result.data
                    if (debt == null) {
                        _uiState.update { it.copy(isLoading = false) }
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            personName = debt.personName,
                            isLent = debt.direction == DebtDirection.OWED_TO_ME,
                            amount = debt.amount.formatWithSymbol(homeCurrencyCode),
                            dueLabel = "Due",
                            isSettled = debt.isSettled,
                            isLoading = false,
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun markSettled() {
        scope.launch {
            when (val result = debtRepository.getById(debtId)) {
                is Result.Success -> {
                    val debt = result.data ?: return@launch
                    debtRepository.upsert(debt.copy(isSettled = true))
                    _uiState.update { it.copy(isSettled = true) }
                }
                is Result.Error -> Unit
            }
        }
    }

    fun deleteDebt() {
        scope.launch {
            when (debtRepository.delete(debtId)) {
                is Result.Success -> onDeleted()
                is Result.Error -> Unit
            }
        }
    }
}
