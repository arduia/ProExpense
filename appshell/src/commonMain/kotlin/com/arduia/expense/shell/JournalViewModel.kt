package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class JournalUiState(
    val query: String = "",
    val dayGroups: List<HomeDayGroup> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && dayGroups.isEmpty()
}

/**
 * Full record history grouped by day, with a note/category text filter.
 *
 * Deliberately filters in memory over the observed record list rather than through
 * `getRecordsPage`'s SQL pushdown: this slice needs a correct, shared Journal on both platforms
 * first. Adopting the paged path (and the debounce/cursor bookkeeping that Android's
 * `HistoryFeatureEntry` already implements) is the follow-up that retires that Android-only file.
 */
class JournalViewModel(
    private val financeRecordRepository: FinanceRecordRepository,
    private val categoryRepository: CategoryRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<JournalUiState>(JournalUiState(), dispatcher) {
    private var allRecords: List<FinanceRecord> = emptyList()
    private var categoryNames: Map<String, String> = emptyMap()
    private var symbol: String = "$"

    init {
        viewModelScope.launch {
            val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
            symbol = currencySymbol(code ?: "USD")
            financeRecordRepository
                .observeAll()
                .combine(categoryRepository.observeAll()) { records, categories ->
                    records to categories.associate { category -> category.id.value to category.name }
                }.collect { (records, names) ->
                    allRecords = records
                    categoryNames = names
                    reproject()
                }
        }
    }

    fun onQueryChange(query: String) {
        setState { it.copy(query = query) }
        reproject()
    }

    private fun reproject() {
        val query = currentState().query.trim()
        val matching =
            if (query.isEmpty()) {
                allRecords
            } else {
                allRecords.filter { record ->
                    val category = categoryNames[record.categoryId.value].orEmpty()
                    record.note.orEmpty().contains(query, ignoreCase = true) ||
                        category.contains(query, ignoreCase = true)
                }
            }
        val groups =
            RecordRowProjection.toDayGroups(
                records = matching,
                categoryNames = categoryNames,
                currencySymbol = symbol,
                nowEpochMillis = currentEpochMillis(),
            )
        setState { it.copy(dayGroups = groups, isLoading = false) }
    }
}
