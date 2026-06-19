package com.arduia.expense.feature.history

import com.arduia.expense.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JournalUiState(
    val groupedEntries: List<ExpenseDayGroup> = emptyList(),
    val searchResults: List<ExpenseListItem>? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = true,
    val categoryFilters: List<String> = listOf("All"),
    val selectedCategoryFilter: String = "All",
)

class JournalViewModel(
    private val historyRepository: HistoryRepository,
    private val formatter: RecordDateFormatter,
    private val categoryLabel: (String) -> String,
    private val homeCurrencyCode: String,
    private val categoryFilters: List<String>,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(JournalUiState(categoryFilters = categoryFilters))
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadGrouped()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearchActive = query.isNotBlank()) }
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            if (query.isBlank()) {
                _uiState.update { it.copy(searchResults = null, isSearchActive = false) }
                loadGrouped()
            } else {
                performSearch(query)
            }
        }
    }

    fun onSearchActivated() {
        _uiState.update { it.copy(isSearchActive = true) }
    }

    fun onSearchDismissed() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(searchQuery = "", searchResults = null, isSearchActive = false)
        }
        scope.launch { loadGrouped() }
    }

    fun onCategoryFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedCategoryFilter = filter) }
        val query = _uiState.value.searchQuery
        if (query.isNotBlank()) {
            scope.launch { performSearch(query) }
        } else {
            scope.launch { loadGrouped() }
        }
    }

    private suspend fun loadGrouped() {
        val filter = buildFilter(query = null)
        val records = historyRepository.getRecords(filter)
        val list = when (records) {
            is Result.Success -> records.data
            is Result.Error -> emptyList()
        }
        val groups = groupRecordsByDay(
            records = list,
            formatter = formatter,
            homeCurrencyCode = homeCurrencyCode,
            categoryLabel = categoryLabel,
        )
        _uiState.update { it.copy(groupedEntries = groups, searchResults = null) }
    }

    private suspend fun performSearch(query: String) {
        val filter = buildFilter(query = query)
        val records = historyRepository.getRecords(filter)
        val list = when (records) {
            is Result.Success -> records.data
            is Result.Error -> emptyList()
        }
        val results = list.map { it.toListItem(formatter, homeCurrencyCode, categoryLabel) }
        _uiState.update { it.copy(searchResults = results, groupedEntries = emptyList()) }
    }

    private fun buildFilter(query: String?): RecordHistoryFilter {
        val selected = _uiState.value.selectedCategoryFilter
        val categoryId = if (selected == "All") null else selected.lowercase()
        return RecordHistoryFilter(
            categoryId = categoryId,
            query = query,
        )
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 250L
    }
}
