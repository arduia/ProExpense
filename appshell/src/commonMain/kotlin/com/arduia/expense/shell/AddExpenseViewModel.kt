package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.logging.LogExpenseUseCase
import com.arduia.expense.feature.logging.SaveExpenseInput
import com.arduia.expense.feature.logging.SaveExpenseOutcome
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class CategoryOption(
    val id: String,
    val label: String,
)

data class AddExpenseUiState(
    val rawAmount: String = "",
    val note: String = "",
    val selectedCategoryId: String = "",
    val categories: List<CategoryOption> = emptyList(),
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
) {
    /** Formatted for display via the same grouping/decimal rules Android's amount field uses. */
    val amountDisplay: String get() = AmountInput.formatDisplay(rawAmount)

    val canSave: Boolean get() = (rawAmount.toDoubleOrNull() ?: 0.0) > 0.0 && selectedCategoryId.isNotEmpty()
}

/**
 * Quick-log form state for the SwiftUI Add Expense sheet.
 *
 * Keypad editing goes through the shared [AmountInput] rules and saving through the shared
 * [LogExpenseUseCase], so iOS inherits the max-amount guard and validation Android already enforces
 * rather than reimplementing them in Swift.
 */
class AddExpenseViewModel(
    private val logExpense: LogExpenseUseCase,
    private val categoryRepository: CategoryRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<AddExpenseUiState>(AddExpenseUiState(), dispatcher) {
    init {
        viewModelScope.launch {
            val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code ?: "USD"
            val categories =
                (categoryRepository.getAll() as? Result.Success)
                    ?.data
                    ?.sortedBy { it.sortOrder }
                    ?.map { CategoryOption(id = it.id.value, label = it.name) }
                    .orEmpty()
            setState {
                it.copy(
                    categories = categories,
                    selectedCategoryId = categories.firstOrNull()?.id.orEmpty(),
                    currencyCode = code,
                    currencySymbol = currencySymbol(code),
                )
            }
        }
    }

    fun onKey(key: String) {
        setState { it.copy(rawAmount = AmountInput.applyKey(it.rawAmount, key)) }
    }

    fun onBackspace() {
        setState { it.copy(rawAmount = AmountInput.applyBackspace(it.rawAmount)) }
    }

    fun onNoteChange(note: String) {
        setState { it.copy(note = note) }
    }

    fun onCategorySelected(categoryId: String) {
        setState { it.copy(selectedCategoryId = categoryId) }
    }

    suspend fun save(): SaveExpenseOutcome {
        val state = currentState()
        return logExpense(
            SaveExpenseInput(
                rawAmount = state.rawAmount,
                currencyCode = state.currencyCode,
                categoryId = state.selectedCategoryId,
                note = state.note,
                recordedAtEpochMillis = currentEpochMillis(),
            ),
        )
    }
}
