package com.arduia.expense.di

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.feature.auth.InMemoryPinAuthRepository
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.PinEntryViewModel
import com.arduia.expense.feature.auth.PinSetupViewModel
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.currency.InMemoryCurrencyRepository
import com.arduia.expense.feature.history.AndroidRecordDateFormatter
import com.arduia.expense.feature.history.DebtAddViewModel
import com.arduia.expense.feature.history.DebtDetailViewModel
import com.arduia.expense.feature.history.DebtTrackerViewModel
import com.arduia.expense.feature.history.EventBudgetViewModel
import com.arduia.expense.feature.history.EventCreateViewModel
import com.arduia.expense.feature.history.EventDetailViewModel
import com.arduia.expense.feature.history.HistoryRepository
import com.arduia.expense.feature.history.HomeViewModel
import com.arduia.expense.feature.history.InMemoryHistoryRepository
import com.arduia.expense.feature.history.JournalDetailViewModel
import com.arduia.expense.feature.history.JournalViewModel
import com.arduia.expense.feature.history.RecordDateFormatter
import com.arduia.expense.feature.history.ReportsViewModel
import com.arduia.expense.feature.importexport.ImportExportRepository
import com.arduia.expense.feature.importexport.InMemoryImportExportRepository
import com.arduia.expense.feature.logging.AddExpenseViewModel
import com.arduia.expense.feature.logging.CategoryListViewModel
import com.arduia.expense.feature.logging.InMemoryCategoryRepository
import com.arduia.expense.feature.logging.InMemoryLoggingRepository
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.allLogCategories
import com.arduia.expense.feature.logging.logCategoryById
import com.arduia.expense.feature.sharedcost.InMemorySharedCostRepository
import com.arduia.expense.feature.sharedcost.SharedCostInputViewModel
import com.arduia.expense.feature.sharedcost.SharedCostRepository
import com.arduia.expense.feature.sharedcost.SharedSummaryViewModel
import com.arduia.expense.storage.InMemoryBudgetRepository
import com.arduia.expense.storage.InMemoryDataStore
import com.arduia.expense.storage.InMemoryDebtRepository
import com.arduia.expense.storage.InMemoryEventRepository
import com.arduia.expense.storage.InMemoryLockoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppGraph(
    applicationScope: CoroutineScope,
) {
    private val scope = CoroutineScope(applicationScope.coroutineContext + SupervisorJob())

    val dataStore = InMemoryDataStore()
    val loggingRepository: LoggingRepository = InMemoryLoggingRepository(dataStore)
    val historyRepository: HistoryRepository = InMemoryHistoryRepository(dataStore)
    val categoryRepository: CategoryRepository = InMemoryCategoryRepository()
    val eventRepository: EventRepository = InMemoryEventRepository(dataStore)
    val debtRepository: DebtRepository = InMemoryDebtRepository(dataStore)
    val budgetRepository: BudgetRepository = InMemoryBudgetRepository(dataStore)
    val lockoutRepository: LockoutRepository = InMemoryLockoutRepository(dataStore)
    val pinAuthRepository: PinAuthRepository = InMemoryPinAuthRepository(dataStore)
    val securityStateReader: SecurityStateReader = pinAuthRepository as SecurityStateReader
    val currencyRepository: CurrencyRepository = InMemoryCurrencyRepository(dataStore)
    val sharedCostRepository: SharedCostRepository = InMemorySharedCostRepository(dataStore)
    val importExportRepository: ImportExportRepository = InMemoryImportExportRepository(dataStore)
    val dateFormatter: RecordDateFormatter = AndroidRecordDateFormatter()

    private val homeCurrencyCode: String = dataStore.homeCurrencyCode

    private val categoryLabel: (String) -> String = { id -> categoryRepositoryLabel(id) }

    val homeViewModel = HomeViewModel(
        historyRepository = historyRepository,
        budgetRepository = budgetRepository,
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

    val eventBudgetViewModel = EventBudgetViewModel(
        eventRepository = eventRepository,
        historyRepository = historyRepository,
        formatter = dateFormatter,
        homeCurrencyCode = homeCurrencyCode,
        scope = scope,
    )

    val reportsViewModel = ReportsViewModel(
        historyRepository = historyRepository,
        budgetRepository = budgetRepository,
        formatter = dateFormatter,
        homeCurrencyCode = homeCurrencyCode,
        categoryLabel = categoryLabel,
        scope = scope,
    )

    val debtTrackerViewModel = DebtTrackerViewModel(
        debtRepository = debtRepository,
        homeCurrencyCode = homeCurrencyCode,
        scope = scope,
    )

    private var addExpenseViewModel: AddExpenseViewModel? = null

    fun homeCurrency(): String = homeCurrencyCode

    fun prewarmAddExpenseViewModel(
        onSaved: () -> Unit,
        onSaveFailed: (String) -> Unit,
    ): AddExpenseViewModel {
        val existing = addExpenseViewModel
        if (existing != null) return existing
        return AddExpenseViewModel(
            loggingRepository = loggingRepository,
            homeCurrencyCode = homeCurrencyCode,
            scope = scope,
            nowEpochMillis = dateFormatter::nowEpochMillis,
            onSaved = onSaved,
            onSaveFailed = onSaveFailed,
        ).also { addExpenseViewModel = it }
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

    fun createPinSetupViewModel(onComplete: () -> Unit): PinSetupViewModel =
        PinSetupViewModel(pinAuthRepository, scope, onComplete)

    fun createPinEntryViewModel(
        onUnlocked: () -> Unit,
        onRecoverySuccess: () -> Unit,
    ): PinEntryViewModel = PinEntryViewModel(
        pinAuthRepository = pinAuthRepository,
        lockoutRepository = lockoutRepository,
        nowEpochMillis = dateFormatter::nowEpochMillis,
        scope = scope,
        onUnlocked = onUnlocked,
        onRecoverySuccess = onRecoverySuccess,
    )

    fun createEventCreateViewModel(onSaved: () -> Unit): EventCreateViewModel =
        EventCreateViewModel(eventRepository, dateFormatter, scope, onSaved)

    fun createDebtAddViewModel(isLent: Boolean, onSaved: () -> Unit): DebtAddViewModel =
        DebtAddViewModel(debtRepository, isLent, scope, onSaved)

    fun createDebtDetailViewModel(
        debtId: String,
        onDeleted: () -> Unit,
    ): DebtDetailViewModel = DebtDetailViewModel(
        debtId = debtId,
        debtRepository = debtRepository,
        homeCurrencyCode = homeCurrencyCode,
        scope = scope,
        onDeleted = onDeleted,
    )

    fun createEventDetailViewModel(eventId: String): EventDetailViewModel =
        EventDetailViewModel(
            eventId = eventId,
            eventRepository = eventRepository,
            historyRepository = historyRepository,
            formatter = dateFormatter,
            homeCurrencyCode = homeCurrencyCode,
            categoryLabel = categoryLabel,
            scope = scope,
        )

    fun createSharedCostInputViewModel(onCalculated: (String) -> Unit): SharedCostInputViewModel =
        SharedCostInputViewModel(
            sharedCostRepository = sharedCostRepository,
            homeCurrencyCode = homeCurrencyCode,
            nowEpochMillis = dateFormatter::nowEpochMillis,
            scope = scope,
            onCalculated = onCalculated,
        )

    fun createSharedSummaryViewModel(sharedCostId: String): SharedSummaryViewModel =
        SharedSummaryViewModel(sharedCostRepository, sharedCostId, homeCurrencyCode, scope)

    suspend fun clearAllData() {
        dataStore.clearAll()
        pinAuthRepository.clearPin()
        homeViewModel.refresh()
        journalViewModel.refresh()
        eventBudgetViewModel.refresh()
        debtTrackerViewModel.refresh()
        reportsViewModel.refresh()
    }

    fun refreshAfterDataChange() {
        homeViewModel.refresh()
        journalViewModel.refresh()
        eventBudgetViewModel.refresh()
        debtTrackerViewModel.refresh()
        reportsViewModel.refresh()
    }

    private fun categoryRepositoryLabel(id: String): String {
        val fromStatic = allLogCategories.firstOrNull { it.id == id }?.label
        if (fromStatic != null) return fromStatic
        return logCategoryById(id).label
    }
}
