package com.arduia.expense.di

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.feature.auth.InMemoryPinAuthRepository
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.history.AndroidRecordDateFormatter
import com.arduia.expense.feature.history.HistoryRepository
import com.arduia.expense.feature.history.HomeViewModel
import com.arduia.expense.feature.history.InMemoryHistoryRepository
import com.arduia.expense.feature.history.JournalDetailViewModel
import com.arduia.expense.feature.history.JournalViewModel
import com.arduia.expense.feature.history.RecordDateFormatter
import com.arduia.expense.feature.logging.AddExpenseViewModel
import com.arduia.expense.feature.logging.CategoryListViewModel
import com.arduia.expense.feature.logging.InMemoryCategoryRepository
import com.arduia.expense.feature.logging.InMemoryLoggingRepository
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.allLogCategories
import com.arduia.expense.feature.logging.logCategoryById
import com.arduia.expense.storage.InMemoryFinanceStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppGraph(
    applicationScope: CoroutineScope,
) {
    private val scope = CoroutineScope(applicationScope.coroutineContext + SupervisorJob())

    val financeStore = InMemoryFinanceStore()
    val loggingRepository: LoggingRepository = InMemoryLoggingRepository(financeStore)
    val historyRepository: HistoryRepository = InMemoryHistoryRepository(financeStore)
    val categoryRepository: CategoryRepository = InMemoryCategoryRepository()
    val pinAuthRepository: PinAuthRepository = InMemoryPinAuthRepository()
    val securityStateReader: SecurityStateReader = pinAuthRepository as SecurityStateReader
    val dateFormatter: RecordDateFormatter = AndroidRecordDateFormatter()

    private val homeCurrencyCode = "USD"

    private val categoryLabel: (String) -> String = { id ->
        categoryRepositoryLabel(id)
    }

    val homeViewModel = HomeViewModel(
        historyRepository = historyRepository,
        securityState = securityStateReader,
        formatter = dateFormatter,
        categoryLabel = categoryLabel,
        homeCurrencyCode = homeCurrencyCode,
        scope = scope,
    )

    val journalViewModel = JournalViewModel(
        historyRepository = historyRepository,
        formatter = dateFormatter,
        categoryLabel = categoryLabel,
        homeCurrencyCode = homeCurrencyCode,
        categoryFilters = listOf("All", "Food", "Transport", "Bills", "Entertainment"),
        scope = scope,
    )

    val categoryListViewModel = CategoryListViewModel(
        categoryRepository = categoryRepository,
        scope = scope,
    )

    private var addExpenseViewModel: AddExpenseViewModel? = null

    fun prewarmAddExpenseViewModel(
        onSaved: () -> Unit,
        onSaveFailed: (String) -> Unit,
    ): AddExpenseViewModel {
        val existing = addExpenseViewModel
        if (existing != null) {
            return existing
        }
        return AddExpenseViewModel(
            loggingRepository = loggingRepository,
            homeCurrencyCode = homeCurrencyCode,
            scope = scope,
            nowEpochMillis = dateFormatter::nowEpochMillis,
            onSaved = onSaved,
            onSaveFailed = onSaveFailed,
        ).also {
            addExpenseViewModel = it
        }
    }

    fun clearAddExpenseViewModel() {
        addExpenseViewModel = null
    }

    fun createJournalDetailViewModel(
        recordId: String,
        onDeleted: () -> Unit,
    ): JournalDetailViewModel = JournalDetailViewModel(
        recordId = recordId,
        historyRepository = historyRepository,
        formatter = dateFormatter,
        categoryLabel = categoryLabel,
        homeCurrencyCode = homeCurrencyCode,
        scope = scope,
        onDeleted = onDeleted,
    )

    fun refreshAfterDataChange() {
        homeViewModel.refresh()
        journalViewModel.refresh()
    }

    private fun categoryRepositoryLabel(id: String): String {
        val fromStatic = allLogCategories.firstOrNull { it.id == id }?.label
        if (fromStatic != null) return fromStatic
        return logCategoryById(id).label
    }
}
