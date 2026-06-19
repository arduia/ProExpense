package com.arduia.expense.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import androidx.compose.runtime.collectAsState
import com.arduia.expense.di.AppGraph
import com.arduia.expense.ui.auth.PinEntryMode
import com.arduia.expense.ui.auth.PinEntryScreenContent
import com.arduia.expense.ui.categories.CategoryListScreenContent
import com.arduia.expense.ui.currency.CurrencySettingScreenContent
import com.arduia.expense.ui.data.ClearDataScreenContent
import com.arduia.expense.ui.data.DataExportScreenContent
import com.arduia.expense.ui.debt.DebtAddScreenContent
import com.arduia.expense.ui.debt.DebtDetailScreenContent
import com.arduia.expense.ui.debt.DebtTrackerScreenContent
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.events.EventCreateScreenContent
import com.arduia.expense.ui.events.EventDetailScreenContent
import com.arduia.expense.ui.journal.JournalDetailScreenContent
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.preview.previewEventDetailTransactions
import com.arduia.expense.ui.preview.previewReportsCategories
import com.arduia.expense.ui.preview.previewSharedHistory
import com.arduia.expense.ui.reports.ReportsScreenContent
import com.arduia.expense.ui.shared.SharedCostsScreenContent
import com.arduia.expense.ui.shared.SharedHistoryScreenContent
import com.arduia.expense.ui.shared.SharedSummaryScreenContent
import com.arduia.expense.ui.util.PinDemo
import kotlinx.coroutines.delay

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
            AppRoutes.REPORTS -> ReportsScreenContent(
                modifier = Modifier.fillMaxSize(),
                monthLabel = stringResource(R.string.reports_monthly),
                totalSpend = "$80.90",
                categories = previewReportsCategories,
                onBack = navigator::pop,
            )

            AppRoutes.CATEGORIES -> CategoriesRoute(
                modifier = Modifier.fillMaxSize(),
                appGraph = appGraph,
                onBack = navigator::pop,
            )

            AppRoutes.CURRENCY -> CurrencyRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
            )

            AppRoutes.EXPORT -> DataExportScreenContent(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
                onExport = {
                    toastMessage = null
                    toastMessage = "export"
                },
            )

            AppRoutes.CLEAR -> ClearDataRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
                onCleared = {
                    toastMessage = null
                    toastMessage = "clear"
                },
            )

            AppRoutes.EVENT_CREATE -> EventCreateRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
                onSaved = navigator::pop,
            )

            AppRoutes.DEBT_TRACKER -> DebtTrackerRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
                onAdd = { navigator.push(AppRoutes.DEBT_ADD) },
                onRecordClick = { navigator.push(AppRoutes.DEBT_DETAIL) },
            )

            AppRoutes.DEBT_ADD -> DebtAddRoute(
                modifier = Modifier.fillMaxSize(),
                onClose = navigator::pop,
                onSave = navigator::pop,
            )

            AppRoutes.DEBT_DETAIL -> DebtDetailRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
            )

            AppRoutes.SHARED_INPUT -> SharedInputRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = navigator::pop,
                onCalculate = { navigator.push(AppRoutes.SHARED_SUMMARY) },
            )

            AppRoutes.SHARED_SUMMARY -> SharedSummaryScreenContent(
                modifier = Modifier.fillMaxSize(),
                people = listOf(
                    "Alex" to "$30.00",
                    "Jordan" to "$30.00",
                    "Sam" to "$30.00",
                    "Taylor" to "$30.00",
                ),
                onBack = navigator::pop,
                onSave = { navigator.replace(AppRoutes.SHARED_HISTORY) },
            )

            AppRoutes.SHARED_HISTORY -> SharedHistoryScreenContent(
                modifier = Modifier.fillMaxSize(),
                items = previewSharedHistory,
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
                        val eventTitle = AppRoutes.eventDetailTitle(route)
                        if (eventTitle != null) {
                            EventDetailScreenContent(
                                modifier = Modifier.fillMaxSize(),
                                title = eventTitle,
                                dateRange = "May 12 — May 26",
                                spentLabel = "$1,240",
                                budgetLabel = "of $2,000",
                                progress = 0.62f,
                                isClosed = false,
                                transactions = previewEventDetailTransactions,
                                onBack = navigator::pop,
                            )
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
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var selectedCode by rememberSaveable { mutableStateOf("USD") }
    CurrencySettingScreenContent(
        modifier = modifier,
        selectedCode = selectedCode,
        showPicker = showPicker,
        onOpenPicker = { showPicker = true },
        onClosePicker = { showPicker = false },
        onCurrencySelected = {
            selectedCode = it
            showPicker = false
        },
        onBack = onBack,
    )
}

@Composable
private fun ClearDataRoute(
    onBack: () -> Unit,
    onCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConfirmStep by rememberSaveable { mutableStateOf(false) }
    ClearDataScreenContent(
        modifier = modifier,
        showConfirmStep = showConfirmStep,
        onRequestClear = { showConfirmStep = true },
        onCancelConfirm = { showConfirmStep = false },
        onBack = onBack,
        onConfirmClear = {
            showConfirmStep = false
            onCleared()
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
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    EventCreateScreenContent(
        modifier = modifier,
        name = name,
        onNameChange = {
            name = it
            showErrors = false
        },
        dateRange = "May 12 — May 26",
        amountText = amountText,
        onAmountChange = { amountText = it },
        showErrors = showErrors,
        onBack = onBack,
        onSave = {
            if (name.isBlank() || amountText.isBlank()) {
                showErrors = true
            } else {
                onSaved()
            }
        },
    )
}

@Composable
private fun DebtTrackerRoute(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRecordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var debtTab by rememberSaveable { mutableStateOf(0) }
    DebtTrackerScreenContent(
        modifier = modifier,
        selectedTab = debtTab,
        lentRecords = previewDebtLent,
        oweRecords = previewDebtOwe,
        onTabSelected = { debtTab = it },
        onAddClick = onAdd,
        onRecordClick = onRecordClick,
        onBack = onBack,
    )
}

@Composable
private fun DebtAddRoute(
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var personName by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    val dueDate = stringResource(R.string.debt_due_preview)

    DebtAddScreenContent(
        modifier = modifier,
        personName = personName,
        onPersonNameChange = { personName = it },
        amountText = amountText,
        onAmountChange = { amountText = it },
        dueDate = dueDate,
        onClose = onClose,
        onSave = {
            if (personName.isNotBlank() && amountText.isNotBlank()) onSave()
        },
        saveEnabled = personName.isNotBlank() && amountText.isNotBlank(),
    )
}

@Composable
private fun DebtDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSettled by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    DebtDetailScreenContent(
        modifier = modifier,
        personName = "John",
        isLent = true,
        amount = "$50.00",
        dueLabel = stringResource(R.string.debt_due_preview),
        isSettled = isSettled,
        showDeleteConfirm = showDeleteConfirm,
        onBack = onBack,
        onMarkSettled = { isSettled = true },
        onRequestDelete = { showDeleteConfirm = true },
        onCancelDelete = { showDeleteConfirm = false },
        onConfirmDelete = onBack,
    )
}

@Composable
private fun SharedInputRoute(
    onBack: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountText by rememberSaveable { mutableStateOf("120.00") }
    var peopleCount by rememberSaveable { mutableIntStateOf(4) }
    var showZeroValidation by rememberSaveable { mutableStateOf(false) }

    SharedCostsScreenContent(
        modifier = modifier,
        amountText = amountText,
        onAmountChange = {
            amountText = it
            showZeroValidation = false
        },
        peopleCount = peopleCount,
        showZeroValidation = showZeroValidation,
        onIncrementPeople = { peopleCount = (peopleCount + 1).coerceAtMost(20) },
        onDecrementPeople = { peopleCount = (peopleCount - 1).coerceAtLeast(1) },
        onCalculate = {
            val amount = amountText.toDoubleOrNull() ?: 0.0
            if (amount <= 0.0) {
                showZeroValidation = true
            } else {
                onCalculate()
            }
        },
        onBack = onBack,
    )
}

@Composable
fun PinEntryRouteHost(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
    demoPin: String = PinDemo.DEMO_PIN,
    demoSecurityAnswer: String = PinDemo.DEMO_SECURITY_ANSWER,
) {
    var mode by rememberSaveable { mutableStateOf(PinEntryMode.Entry) }
    var filledDots by rememberSaveable { mutableIntStateOf(0) }
    var currentPin by remember { mutableStateOf("") }
    var recoveryAnswer by rememberSaveable { mutableStateOf("") }
    var wrongAttempts by rememberSaveable { mutableIntStateOf(0) }
    var lockoutSeconds by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(lockoutSeconds) {
        if (lockoutSeconds > 0) {
            delay(1_000)
            lockoutSeconds -= 1
            if (lockoutSeconds == 0) {
                mode = PinEntryMode.Entry
                wrongAttempts = 0
            }
        }
    }

    PinEntryScreenContent(
        modifier = modifier,
        mode = mode,
        filledDots = filledDots,
        lockoutSeconds = lockoutSeconds,
        recoveryAnswer = recoveryAnswer,
        onRecoveryAnswerChange = { recoveryAnswer = it },
        onDigit = { digit ->
            if (mode == PinEntryMode.Lockout) return@PinEntryScreenContent
            if (currentPin.length >= 6) return@PinEntryScreenContent
            currentPin += digit.toString()
            filledDots = currentPin.length
            if (currentPin.length == 6) {
                if (currentPin == demoPin) {
                    onUnlocked()
                } else {
                    wrongAttempts += 1
                    currentPin = ""
                    filledDots = 0
                    mode = PinEntryMode.Wrong
                    if (wrongAttempts >= PinDemo.MAX_ATTEMPTS) {
                        mode = PinEntryMode.Lockout
                        lockoutSeconds = PinDemo.LOCKOUT_SECONDS
                    }
                }
            }
        },
        onBackspace = {
            if (currentPin.isNotEmpty()) {
                currentPin = currentPin.dropLast(1)
                filledDots = currentPin.length
            }
            if (mode == PinEntryMode.Wrong) mode = PinEntryMode.Entry
        },
        onForgotPin = { mode = PinEntryMode.Recovery },
        onUnlockRecovery = {
            if (recoveryAnswer.trim().equals(demoSecurityAnswer, ignoreCase = true)) {
                onUnlocked()
            }
        },
        onBiometricUnlock = onUnlocked,
    )
}
