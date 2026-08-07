package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.RecordId
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.UpdateRecordNoteUseCase
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class JournalDetailUiState(
    val row: ProTransactionRowModel? = null,
    val noteDraft: String = "",
    val isLoading: Boolean = true,
    val deleted: Boolean = false,
    val errorMessage: String? = null,
) {
    val notFound: Boolean get() = !isLoading && row == null && !deleted
}

/**
 * 06 · Journal Detail — one record's full view, with inline note editing and delete.
 *
 * The row is rebuilt through [RecordRowProjection] rather than passed in from the list, so the
 * detail's labels stay correct after an edit instead of showing whatever the list rendered.
 */
class JournalDetailViewModel(
    private val financeRecordRepository: FinanceRecordRepository,
    private val categoryRepository: CategoryRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val deleteRecord: DeleteRecordUseCase,
    private val updateRecordNote: UpdateRecordNoteUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<JournalDetailUiState>(JournalDetailUiState(), dispatcher) {
    private var recordId: String = ""

    fun load(recordId: String) {
        this.recordId = recordId
        viewModelScope.launch { reload() }
    }

    fun onNoteDraftChange(note: String) {
        setState { it.copy(noteDraft = note) }
    }

    /** Both use cases return Unit — re-read after writing so the view reflects what persisted. */
    suspend fun saveNote() {
        updateRecordNote(recordId, currentState().noteDraft.trim())
        reload()
    }

    suspend fun delete() {
        deleteRecord(recordId)
        setState { it.copy(deleted = true) }
    }

    private suspend fun reload() {
        val record = (financeRecordRepository.getById(RecordId(recordId)) as? Result.Success)?.data
        val categoryNames =
            (categoryRepository.getAll() as? Result.Success)
                ?.data
                ?.associate { it.id.value to it.name }
                .orEmpty()
        val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
        val symbol = currencySymbol(code ?: "USD")
        setState {
            it.copy(
                row = record?.let { r -> RecordRowProjection.toRow(r, categoryNames, symbol) },
                noteDraft = record?.note.orEmpty(),
                isLoading = false,
            )
        }
    }
}
