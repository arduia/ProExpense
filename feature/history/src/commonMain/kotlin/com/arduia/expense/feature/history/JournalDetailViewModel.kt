package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JournalDetailUiState(
    val isLoading: Boolean = true,
    val record: FinanceRecord? = null,
    val categoryId: String = "",
    val note: String = "",
    val meta: String = "",
    val amount: String = "",
    val eventTag: String? = null,
    val notFound: Boolean = false,
)

class JournalDetailViewModel(
    private val recordId: String,
    private val historyRepository: HistoryRepository,
    private val formatter: RecordDateFormatter,
    private val categoryLabel: (String) -> String,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
    private val onDeleted: () -> Unit,
) {
    private val _uiState = MutableStateFlow(JournalDetailUiState())
    val uiState: StateFlow<JournalDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = historyRepository.getById(recordId)) {
                is Result.Success -> {
                    val record = result.data
                    if (record == null) {
                        _uiState.update { it.copy(isLoading = false, notFound = true) }
                    } else {
                        val item = record.toListItem(formatter, homeCurrencyCode, categoryLabel)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                record = record,
                                categoryId = item.categoryId,
                                note = item.note,
                                meta = item.meta,
                                amount = item.amount,
                                eventTag = item.tag,
                                notFound = false,
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, notFound = true) }
                }
            }
        }
    }

    fun deleteRecord() {
        scope.launch {
            when (historyRepository.deleteRecord(recordId)) {
                is Result.Success -> onDeleted()
                is Result.Error -> Unit
            }
        }
    }
}
