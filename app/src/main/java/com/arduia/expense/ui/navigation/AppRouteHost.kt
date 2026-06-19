package com.arduia.expense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import com.arduia.expense.ui.auth.PinEntryMode
import com.arduia.expense.ui.auth.PinEntryScreenContent
import com.arduia.expense.ui.categories.CategoryListScreenContent
import com.arduia.expense.ui.currency.CurrencySettingScreenContent
import com.arduia.expense.ui.data.ClearDataScreenContent
import com.arduia.expense.ui.data.DataExportScreenContent
import com.arduia.expense.ui.debt.DebtAddScreenContent
import com.arduia.expense.ui.debt.DebtDetailScreenContent
import com.arduia.expense.ui.debt.DebtTrackerScreenContent
import com.arduia.expense.ui.events.EventCreateScreenContent
import com.arduia.expense.ui.events.EventDetailScreenContent
import com.arduia.expense.ui.journal.JournalDetailScreenContent
import com.arduia.expense.ui.preview.previewCategoryItems
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.preview.previewEventDetailTransactions
import com.arduia.expense.ui.preview.previewReportsCategories
import com.arduia.expense.ui.preview.previewSharedHistory
import com.arduia.expense.ui.reports.ReportsScreenContent
import com.arduia.expense.ui.shared.SharedCostsScreenContent
import com.arduia.expense.ui.shared.SharedHistoryScreenContent
import com.arduia.expense.ui.shared.SharedSummaryScreenContent

@Composable
fun AppRouteHost(
    route: String,
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
) {
    when (route) {
        AppRoutes.REPORTS -> ReportsScreenContent(
            modifier = modifier,
            monthLabel = stringResource(R.string.reports_monthly),
            totalSpend = "$80.90",
            categories = previewReportsCategories,
            onBack = navigator::pop,
        )

        AppRoutes.CATEGORIES -> CategoryListScreenContent(
            modifier = modifier,
            categories = previewCategoryItems,
            newCategoryName = "",
            onNewCategoryChange = {},
            duplicateError = false,
            onBack = navigator::pop,
            onAddCategory = {},
        )

        AppRoutes.CURRENCY -> {
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
                onBack = navigator::pop,
            )
        }

        AppRoutes.EXPORT -> DataExportScreenContent(
            modifier = modifier,
            onBack = navigator::pop,
            onExport = navigator::pop,
        )

        AppRoutes.CLEAR -> ClearDataScreenContent(
            modifier = modifier,
            onBack = navigator::pop,
            onConfirmClear = navigator::pop,
        )

        AppRoutes.JOURNAL_DETAIL -> {
            var showActions by rememberSaveable { mutableStateOf(false) }
            JournalDetailScreenContent(
                modifier = modifier,
                categoryId = "entertainment",
                note = "Movie · Dune",
                meta = "Entertainment · May 25 · 08:10 PM",
                amount = "$18.00",
                eventTag = "Bali Trip",
                showActionsSheet = showActions,
                onBack = navigator::pop,
                onMore = { showActions = true },
                onDismissActions = { showActions = false },
                onEdit = { showActions = false },
                onDelete = {
                    showActions = false
                    navigator.pop()
                },
            )
        }

        AppRoutes.EVENT_CREATE -> EventCreateScreenContent(
            modifier = modifier,
            name = "Bali Trip",
            onNameChange = {},
            dateRange = "May 12 — May 26",
            amountText = "2000",
            showErrors = false,
            onBack = navigator::pop,
            onSave = navigator::pop,
        )

        AppRoutes.DEBT_TRACKER -> {
            var debtTab by rememberSaveable { mutableStateOf(0) }
            DebtTrackerScreenContent(
                modifier = modifier,
                selectedTab = debtTab,
                lentRecords = previewDebtLent,
                oweRecords = previewDebtOwe,
                onTabSelected = { debtTab = it },
                onAddClick = { navigator.push(AppRoutes.DEBT_ADD) },
                onRecordClick = { navigator.push(AppRoutes.DEBT_DETAIL) },
                onBack = navigator::pop,
            )
        }

        AppRoutes.DEBT_ADD -> DebtAddScreenContent(
            modifier = modifier,
            personName = "John",
            onPersonNameChange = {},
            amount = "$50.00",
            dueDate = "Due May 30",
            onClose = navigator::pop,
            onSave = navigator::pop,
        )

        AppRoutes.DEBT_DETAIL -> DebtDetailScreenContent(
            modifier = modifier,
            personName = "John",
            isLent = true,
            amount = "$50.00",
            dueLabel = "Due May 30",
            isSettled = false,
            onBack = navigator::pop,
            onMarkSettled = navigator::pop,
            onDelete = navigator::pop,
        )

        AppRoutes.SHARED_INPUT -> SharedCostsScreenContent(
            modifier = modifier,
            amountText = "120.00",
            peopleCount = 4,
            showZeroValidation = false,
            onIncrementPeople = {},
            onDecrementPeople = {},
            onCalculate = { navigator.push(AppRoutes.SHARED_SUMMARY) },
            onBack = navigator::pop,
        )

        AppRoutes.SHARED_SUMMARY -> SharedSummaryScreenContent(
            modifier = modifier,
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
            modifier = modifier,
            items = previewSharedHistory,
            onBack = navigator::pop,
        )

        else -> {
            val eventTitle = AppRoutes.eventDetailTitle(route)
            if (eventTitle != null) {
                EventDetailScreenContent(
                    modifier = modifier,
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

@Composable
fun PinEntryRouteHost(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by rememberSaveable { mutableStateOf(PinEntryMode.Entry) }
    var filledDots by rememberSaveable { mutableStateOf(0) }
    var recoveryAnswer by rememberSaveable { mutableStateOf("") }

        PinEntryScreenContent(
        modifier = modifier,
        mode = mode,
        filledDots = filledDots,
        lockoutSeconds = 30,
        recoveryAnswer = recoveryAnswer,
        onRecoveryAnswerChange = { recoveryAnswer = it },
        onDigit = {
            val next = (filledDots + 1).coerceAtMost(6)
            filledDots = next
            if (next == 6) onUnlocked()
        },
        onBackspace = { filledDots = (filledDots - 1).coerceAtLeast(0) },
        onForgotPin = { mode = PinEntryMode.Recovery },
        onUnlockRecovery = onUnlocked,
        onBiometricUnlock = onUnlocked,
    )
}
