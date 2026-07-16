package com.arduia.expense.feature.logging

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.launch

data class LoggingUiState(
    val tagOptions: List<TagOption> = emptyList(),
    /** True once the tag catalog's first emission has arrived — distinguishes "no tags exist"
     *  from "still loading", since [tagOptions] defaults to an empty list either way. */
    val tagOptionsLoaded: Boolean = false,
    val existingRecord: FinanceRecord? = null,
)

/** Screen-level business logic for the quick-log / edit-expense flows (design plan §AddExpenseViewModel). */
class LoggingViewModel(
    observeTagOptions: ObserveTagOptionsUseCase,
    private val logExpense: LogExpenseUseCase,
    private val updateExpense: UpdateExpenseUseCase,
    private val loadExpenseForEdit: LoadExpenseForEditUseCase,
) : StatefulViewModel<LoggingUiState>(LoggingUiState()) {
    init {
        viewModelScope.launch {
            observeTagOptions().collect { options ->
                setState { it.copy(tagOptions = options, tagOptionsLoaded = true) }
            }
        }
    }

    suspend fun save(input: SaveExpenseInput): SaveExpenseOutcome = logExpense(input)

    suspend fun loadForEdit(recordId: String) {
        val record = loadExpenseForEdit(recordId)
        setState { it.copy(existingRecord = record) }
    }

    suspend fun update(input: SaveExpenseInput): SaveExpenseOutcome {
        val existing =
            currentState().existingRecord
                ?: return SaveExpenseOutcome.Failed("Record not found")
        return updateExpense(existing, input)
    }
}
