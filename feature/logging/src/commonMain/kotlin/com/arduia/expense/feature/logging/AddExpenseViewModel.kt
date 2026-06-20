package com.arduia.expense.feature.logging

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.parseAmountToCents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val amount: String = "",
    val selectedCategoryId: String = "food",
    val note: String = "",
    val selectedTag: ExpenseTagOption? = null,
    val availableTags: List<ExpenseTagOption> = emptyList(),
    val recordedAtEpochMillis: Long = 0L,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isEditMode: Boolean = false,
)

class AddExpenseViewModel(
    private val loggingRepository: LoggingRepository,
    private val eventRepository: EventRepository,
    private val debtRepository: DebtRepository,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
    private val nowEpochMillis: () -> Long,
    private val onSaved: () -> Unit,
    private val onSaveFailed: (String) -> Unit,
    val editingRecordId: String? = null,
) {
    private val _uiState = MutableStateFlow(
        AddExpenseUiState(
            recordedAtEpochMillis = nowEpochMillis(),
            isEditMode = editingRecordId != null,
        ),
    )
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            loadTagOptions()
            editingRecordId?.let { loadRecord(it) }
        }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value, saveError = null) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onNoteChanged(value: String) {
        _uiState.update { it.copy(note = value.take(MAX_NOTE_LENGTH)) }
    }

    fun onTagSelected(tag: ExpenseTagOption?) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    fun onRecordedAtChanged(epochMillis: Long) {
        _uiState.update { it.copy(recordedAtEpochMillis = epochMillis) }
    }

    fun onSaveFromAmountOnly() {
        save(noteOverride = null)
    }

    fun onSaveWithDetails() {
        save(noteOverride = _uiState.value.note)
    }

    fun reset() {
        _uiState.value = AddExpenseUiState(
            recordedAtEpochMillis = nowEpochMillis(),
            availableTags = _uiState.value.availableTags,
            isEditMode = editingRecordId != null,
        )
        if (editingRecordId != null) {
            scope.launch { loadRecord(editingRecordId) }
        }
    }

    private suspend fun loadRecord(recordId: String) {
        when (val result = loggingRepository.getRecord(recordId)) {
            is Result.Success -> {
                val record = result.data ?: return
                val tag = resolveTagOption(record)
                _uiState.update {
                    it.copy(
                        amount = centsToAmountInput(record.homeCurrencyAmount.valueInCents),
                        selectedCategoryId = record.categoryId,
                        note = record.note.orEmpty(),
                        selectedTag = tag,
                        recordedAtEpochMillis = record.recordedAtEpochMillis,
                    )
                }
            }
            is Result.Error -> Unit
        }
    }

    private suspend fun resolveTagOption(record: FinanceRecord): ExpenseTagOption? {
        val tagType = record.tagType ?: return null
        val tagId = record.tagId ?: return null
        return when (tagType) {
            ExpenseTagType.EVENT -> {
                when (val result = eventRepository.getById(tagId)) {
                    is Result.Success -> result.data?.let {
                        ExpenseTagOption(type = ExpenseTagType.EVENT, id = it.id, label = it.name)
                    }
                    is Result.Error -> null
                }
            }
            ExpenseTagType.DEBT -> {
                when (val result = debtRepository.getById(tagId)) {
                    is Result.Success -> result.data?.let {
                        ExpenseTagOption(type = ExpenseTagType.DEBT, id = it.id, label = it.personName)
                    }
                    is Result.Error -> null
                }
            }
        }
    }

    private suspend fun loadTagOptions() {
        val events = when (val result = eventRepository.getAll()) {
            is Result.Success -> result.data.filter { it.status == EventStatus.ACTIVE }
            is Result.Error -> emptyList()
        }
        val debts = when (val result = debtRepository.getAll()) {
            is Result.Success -> result.data.filter { !it.isSettled }
            is Result.Error -> emptyList()
        }
        val options = events.map {
            ExpenseTagOption(type = ExpenseTagType.EVENT, id = it.id, label = it.name)
        } + debts.map {
            ExpenseTagOption(type = ExpenseTagType.DEBT, id = it.id, label = it.personName)
        }
        _uiState.update { it.copy(availableTags = options) }
    }

    private fun save(noteOverride: String?) {
        val state = _uiState.value
        val cents = AmountInputLogic.toCents(state.amount) ?: return
        _uiState.update { it.copy(isSaving = true, saveError = null) }
        scope.launch {
            val tag = state.selectedTag
            if (editingRecordId != null) {
                when (val existing = loggingRepository.getRecord(editingRecordId)) {
                    is Result.Success -> {
                        val record = existing.data ?: run {
                            _uiState.update { it.copy(isSaving = false) }
                            return@launch
                        }
                        val updated = record.copy(
                            amount = Amount(cents),
                            homeCurrencyAmount = Amount(cents),
                            categoryId = state.selectedCategoryId,
                            note = noteOverride?.takeIf { it.isNotBlank() },
                            recordedAtEpochMillis = state.recordedAtEpochMillis,
                            tagType = tag?.type,
                            tagId = tag?.id,
                        )
                        when (val result = loggingRepository.updateRecord(updated)) {
                            is Result.Success -> {
                                _uiState.update { it.copy(isSaving = false) }
                                onSaved()
                            }
                            is Result.Error -> {
                                _uiState.update { it.copy(isSaving = false, saveError = result.message) }
                                onSaveFailed(result.message)
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isSaving = false, saveError = existing.message) }
                        onSaveFailed(existing.message)
                    }
                }
            } else {
                val input = LogRecordInput(
                    amount = Amount(cents),
                    currency = CurrencyCode(homeCurrencyCode),
                    homeCurrencyAmount = Amount(cents),
                    categoryId = state.selectedCategoryId,
                    type = RecordType.EXPENSE,
                    note = noteOverride?.takeIf { it.isNotBlank() },
                    recordedAtEpochMillis = state.recordedAtEpochMillis,
                    tagType = tag?.type,
                    tagId = tag?.id,
                )
                when (val result = loggingRepository.createRecord(input)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isSaving = false) }
                        reset()
                        onSaved()
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isSaving = false, saveError = result.message) }
                        onSaveFailed(result.message)
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_CATEGORY_ID = "food"
        private const val MAX_NOTE_LENGTH = 200

        internal fun centsToAmountInput(cents: Long): String {
            val whole = cents / 100
            val frac = cents % 100
            if (frac == 0L) return whole.toString()
            val fracStr = frac.toString().padStart(2, '0').trimEnd('0')
            return "$whole.$fracStr"
        }
    }
}
