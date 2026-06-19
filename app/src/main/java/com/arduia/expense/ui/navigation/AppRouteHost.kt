package com.arduia.expense.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import androidx.compose.runtime.collectAsState
import com.arduia.expense.di.AppGraph
import com.arduia.expense.feature.importexport.ExportFormat
import com.arduia.expense.ui.auth.PinEntryScreenContent
import com.arduia.expense.ui.categories.CategoryListScreenContent
import com.arduia.expense.ui.currency.CurrencySettingScreenContent
import com.arduia.expense.ui.data.ClearDataScreenContent
import com.arduia.expense.ui.data.DataExportScreenContent
import com.arduia.expense.ui.debt.DebtAddScreenContent
import com.arduia.expense.ui.debt.DebtDetailScreenContent
import com.arduia.expense.ui.debt.DebtTrackerScreenContent
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.events.EventCreateScreenContent
import com.arduia.expense.ui.events.EventDetailScreenContent
import com.arduia.expense.ui.journal.JournalDetailScreenContent
import com.arduia.expense.ui.reports.ReportsScreenContent
import com.arduia.expense.ui.shared.SharedCostsScreenContent
import com.arduia.expense.ui.shared.SharedHistoryScreenContent
import com.arduia.expense.ui.shared.SharedSummaryScreenContent
import com.arduia.expense.ui.util.toDebtRecordItem
import com.arduia.expense.ui.util.toHomeTransactionItem
import com.arduia.expense.ui.util.toReportsCategoryTriple
import com.arduia.expense.ui.util.toSharedHistoryItem
import com.arduia.expense.ui.util.toUiPinEntryMode
import com.arduia.expense.domain.CurrencyCode
import kotlinx.coroutines.launch

@Composable
fun AppRouteHost(
    route: String,
    navigator: AppNavigator,
    appGraph: AppGraph,
    modifier: Modifier = Modifier,
) {
    var toastMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when (route) {
            AppRoutes.REPORTS -> ReportsRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
            )

            AppRoutes.CATEGORIES -> CategoriesRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
            )

            AppRoutes.CURRENCY -> CurrencyRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
            )

            AppRoutes.EXPORT -> ExportRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
                onExported = { toastMessage = "export" },
            )

            AppRoutes.CLEAR -> ClearDataRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
                onCleared = { toastMessage = "clear" },
            )

            AppRoutes.EVENT_CREATE -> EventCreateRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
                onSaved = {
                    appGraph.eventBudgetViewModel.refresh()
                    navigator.pop()
                },
            )

            AppRoutes.DEBT_TRACKER -> DebtTrackerRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
                onAdd = { isLent -> navigator.push(AppRoutes.debtAdd(isLent)) },
                onRecordClick = { id -> navigator.push(AppRoutes.debtDetail(id)) },
            )

            else -> {
                val debtAddIsLent = AppRoutes.debtAddIsLent(route)
                when {
                    debtAddIsLent != null -> DebtAddRoute(
                        modifier = Modifier.fillMaxSize(),
                        appGraph = appGraph,
                        isLent = debtAddIsLent,
                        onClose = navigator::pop,
                        onSave = {
                            appGraph.debtTrackerViewModel.refresh()
                            navigator.pop()
                        },
                    )
                    else -> {
                        val debtId = AppRoutes.debtDetailId(route)
                        when {
                            debtId != null -> DebtDetailRoute(
                                debtId = debtId,
                                modifier = Modifier.fillMaxSize(),
                                appGraph = appGraph,
                                onBack = navigator::pop,
                            )
                            else -> {
                                val sharedSummaryId = AppRoutes.sharedSummaryId(route)
                                when {
                                    sharedSummaryId != null -> SharedSummaryRoute(
                                        sharedCostId = sharedSummaryId,
                                        modifier = Modifier.fillMaxSize(),
                                        appGraph = appGraph,
                                        onBack = navigator::pop,
                                        onSave = { navigator.replace(AppRoutes.SHARED_HISTORY) },
                                    )
                                    route == AppRoutes.SHARED_INPUT -> SharedInputRoute(
                                        modifier = Modifier.fillMaxSize(),
                                        appGraph = appGraph,
                                        onBack = navigator::pop,
                                        onCalculate = { id -> navigator.push(AppRoutes.sharedSummary(id)) },
                                    )
                                    route == AppRoutes.SHARED_HISTORY -> SharedHistoryRoute(
                                        modifier = Modifier.fillMaxSize(),
                                        appGraph = appGraph,
                                        onBack = navigator::pop,
                                    )
                                    else -> {
                                        val recordId = AppRoutes.journalDetailId(route)
                                        when {
                                            recordId != null -> JournalDetailRoute(
                                                recordId = recordId,
                                                modifier = Modifier.fillMaxSize(),
                                                appGraph = appGraph,
                                                onBack = navigator::pop,
                                            )
                                            else -> {
                                                val eventId = AppRoutes.eventDetailId(route)
                                                if (eventId != null) {
                                                    EventDetailRoute(
                                                        eventId = eventId,
                                                        modifier = Modifier.fillMaxSize(),
                                                        appGraph = appGraph,
                                                        onBack = navigator::pop,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val resolvedToast = when (toastMessage) {
            "export" -> stringResource(R.string.toast_export_success)
            "clear" -> stringResource(R.string.toast_data_cleared)
            else -> toastMessage
        }
        ProToastHost(
            message = resolvedToast,
            onDismiss = {
                val shouldPop = toastMessage == "export" || toastMessage == "clear"
                toastMessage = null
                if (shouldPop) navigator.pop()
            },
        )
    }
}

@Composable
private fun ReportsRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by appGraph.reportsViewModel.uiState.collectAsState()

    ReportsScreenContent(
        modifier = modifier,
        monthLabel = state.monthLabel.ifBlank { stringResource(R.string.reports_monthly) },
        totalSpend = state.totalSpend,
        categories = state.categories.map { it.toReportsCategoryTriple() },
        onBack = onBack,
        showUncategorizedFallback = state.categories.isEmpty(),
    )
}

@Composable
private fun CategoriesRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by appGraph.categoryListViewModel.uiState.collectAsState()

    CategoryListScreenContent(
        modifier = modifier,
        categories = state.categories,
        selectedCategoryId = state.selectedCategoryId,
        onCategorySelected = appGraph.categoryListViewModel::onCategorySelected,
        newCategoryName = state.newCategoryName,
        onNewCategoryChange = appGraph.categoryListViewModel::onNewCategoryChange,
        duplicateError = state.duplicateError,
        onBack = onBack,
        onAddCategory = appGraph.categoryListViewModel::onAddCategory,
    )
}

@Composable
private fun CurrencyRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var selectedCode by rememberSaveable { mutableStateOf(appGraph.homeCurrency()) }

    LaunchedEffect(Unit) {
        appGraph.currencyRepository.getSettings().let { result ->
            if (result is com.arduia.expense.data.Result.Success) {
                selectedCode = result.data.homeCurrency.code
            }
        }
    }

    CurrencySettingScreenContent(
        modifier = modifier,
        selectedCode = selectedCode,
        showPicker = showPicker,
        onOpenPicker = { showPicker = true },
        onClosePicker = { showPicker = false },
        onCurrencySelected = { code ->
            selectedCode = code
            showPicker = false
            scope.launch {
                appGraph.currencyRepository.setHomeCurrency(CurrencyCode(code))
            }
        },
        onBack = onBack,
    )
}

@Composable
private fun ExportRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    onExported: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    DataExportScreenContent(
        modifier = modifier,
        onBack = onBack,
        onExport = {
            scope.launch {
                when (appGraph.importExportRepository.exportAll(ExportFormat.CSV)) {
                    is com.arduia.expense.data.Result.Success -> onExported()
                    is com.arduia.expense.data.Result.Error -> Unit
                }
            }
        },
    )
}

@Composable
private fun ClearDataRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    onCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var showConfirmStep by rememberSaveable { mutableStateOf(false) }

    ClearDataScreenContent(
        modifier = modifier,
        showConfirmStep = showConfirmStep,
        onRequestClear = { showConfirmStep = true },
        onCancelConfirm = { showConfirmStep = false },
        onBack = onBack,
        onConfirmClear = {
            showConfirmStep = false
            scope.launch {
                appGraph.clearAllData()
                onCleared()
            }
        },
    )
}

@Composable
private fun JournalDetailRoute(
    recordId: String,
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember(recordId) {
        appGraph.createJournalDetailViewModel(
            recordId = recordId,
            onDeleted = {
                appGraph.refreshAfterDataChange()
                onBack()
            },
        )
    }
    val state by viewModel.uiState.collectAsState()
    var showActions by rememberSaveable { mutableStateOf(false) }

    JournalDetailScreenContent(
        modifier = modifier,
        categoryId = state.categoryId,
        note = state.note,
        meta = state.meta,
        amount = state.amount,
        eventTag = state.eventTag,
        showActionsSheet = showActions,
        onBack = onBack,
        onMore = { showActions = true },
        onDismissActions = { showActions = false },
        onEdit = { showActions = false },
        onDelete = {
            showActions = false
            viewModel.deleteRecord()
        },
    )
}

@Composable
private fun EventCreateRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember {
        appGraph.createEventCreateViewModel(onSaved = onSaved)
    }
    val state by viewModel.uiState.collectAsState()

    EventCreateScreenContent(
        modifier = modifier,
        name = state.name,
        onNameChange = viewModel::onNameChange,
        dateRange = state.dateRangeLabel,
        amountText = state.amountText,
        onAmountChange = viewModel::onAmountChange,
        showErrors = state.showErrors,
        onBack = onBack,
        onSave = viewModel::onSave,
    )
}

@Composable
private fun EventDetailRoute(
    eventId: String,
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember(eventId) { appGraph.createEventDetailViewModel(eventId) }
    val state by viewModel.uiState.collectAsState()

    EventDetailScreenContent(
        modifier = modifier,
        title = state.title,
        dateRange = state.dateRange,
        spentLabel = state.spentLabel,
        budgetLabel = state.budgetLabel,
        progress = state.progress,
        isClosed = state.isClosed,
        transactions = state.transactions.map { it.toHomeTransactionItem() },
        onBack = onBack,
    )
}

@Composable
private fun DebtTrackerRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    onAdd: (Boolean) -> Unit,
    onRecordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by appGraph.debtTrackerViewModel.uiState.collectAsState()
    var debtTab by rememberSaveable { mutableStateOf(0) }

    DebtTrackerScreenContent(
        modifier = modifier,
        selectedTab = debtTab,
        lentRecords = state.lentRecords.map { it.toDebtRecordItem() },
        oweRecords = state.oweRecords.map { it.toDebtRecordItem() },
        onTabSelected = { debtTab = it },
        onAddClick = { onAdd(debtTab == 0) },
        onRecordClick = onRecordClick,
        onBack = onBack,
    )
}

@Composable
private fun DebtAddRoute(
    appGraph: AppGraph,
    isLent: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember(isLent) {
        appGraph.createDebtAddViewModel(isLent = isLent, onSaved = onSave)
    }
    val state by viewModel.uiState.collectAsState()
    val dueDate = stringResource(R.string.debt_due_preview)

    DebtAddScreenContent(
        modifier = modifier,
        personName = state.personName,
        onPersonNameChange = viewModel::onPersonNameChange,
        amountText = state.amountText,
        onAmountChange = viewModel::onAmountChange,
        dueDate = dueDate,
        onClose = onClose,
        onSave = viewModel::onSave,
        saveEnabled = state.personName.isNotBlank() && state.amountText.isNotBlank(),
    )
}

@Composable
private fun DebtDetailRoute(
    debtId: String,
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember(debtId) {
        appGraph.createDebtDetailViewModel(
            debtId = debtId,
            onDeleted = {
                appGraph.debtTrackerViewModel.refresh()
                onBack()
            },
        )
    }
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    DebtDetailScreenContent(
        modifier = modifier,
        personName = state.personName,
        isLent = state.isLent,
        amount = state.amount,
        dueLabel = state.dueLabel,
        isSettled = state.isSettled,
        showDeleteConfirm = showDeleteConfirm,
        onBack = onBack,
        onMarkSettled = viewModel::markSettled,
        onRequestDelete = { showDeleteConfirm = true },
        onCancelDelete = { showDeleteConfirm = false },
        onConfirmDelete = {
            showDeleteConfirm = false
            viewModel.deleteDebt()
        },
    )
}

@Composable
private fun SharedInputRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    onCalculate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember {
        appGraph.createSharedCostInputViewModel(onCalculated = onCalculate)
    }
    val state by viewModel.uiState.collectAsState()

    SharedCostsScreenContent(
        modifier = modifier,
        amountText = state.amountText,
        onAmountChange = viewModel::onAmountChange,
        peopleCount = state.peopleCount,
        showZeroValidation = state.showZeroValidation,
        onIncrementPeople = viewModel::incrementPeople,
        onDecrementPeople = viewModel::decrementPeople,
        onCalculate = viewModel::onCalculate,
        onBack = onBack,
    )
}

@Composable
private fun SharedSummaryRoute(
    sharedCostId: String,
    appGraph: AppGraph,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember(sharedCostId) {
        appGraph.createSharedSummaryViewModel(sharedCostId)
    }
    val state by viewModel.uiState.collectAsState()

    SharedSummaryScreenContent(
        modifier = modifier,
        people = state.people,
        onBack = onBack,
        onSave = onSave,
    )
}

@Composable
private fun SharedHistoryRoute(
    appGraph: AppGraph,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember {
        appGraph.createSharedSummaryViewModel("")
    }
    val state by viewModel.uiState.collectAsState()

    SharedHistoryScreenContent(
        modifier = modifier,
        items = state.history.map { it.toSharedHistoryItem() },
        onBack = onBack,
    )
}

@Composable
fun PinEntryRouteHost(
    appGraph: AppGraph,
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = remember {
        appGraph.createPinEntryViewModel(
            onUnlocked = onUnlocked,
            onRecoverySuccess = onUnlocked,
        )
    }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onScreenLoaded()
    }

    PinEntryScreenContent(
        modifier = modifier,
        mode = state.mode.toUiPinEntryMode(),
        filledDots = state.filledDots,
        lockoutSeconds = state.lockoutSeconds,
        recoveryAnswer = state.recoveryAnswer,
        onRecoveryAnswerChange = viewModel::onRecoveryAnswerChange,
        onDigit = viewModel::onDigit,
        onBackspace = viewModel::onBackspace,
        onForgotPin = viewModel::onForgotPin,
        onUnlockRecovery = viewModel::onUnlockRecovery,
        onBiometricUnlock = viewModel::onBiometricUnlock,
    )
}
