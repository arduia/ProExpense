package com.arduia.expense.ui.util

import com.arduia.expense.feature.history.DebtRecordUiState
import com.arduia.expense.feature.history.EventListItemState
import com.arduia.expense.feature.history.ExpenseDayGroup
import com.arduia.expense.feature.history.ExpenseListItem
import com.arduia.expense.feature.history.HomeUiState as FeatureHomeUiState
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.preview.HomeDayGroup
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.HomeUiState
import com.arduia.expense.ui.preview.JournalDayGroup
import com.arduia.expense.ui.preview.JournalTransactionItem

fun FeatureHomeUiState.toHomeScreenState(greetingName: String = ""): HomeUiState = HomeUiState(
    greetingName = greetingName,
    monthSpend = monthSpend,
    monthDelta = monthDelta,
    dayGroups = dayGroups.map { it.toHomeDayGroup() },
    showEmptyHint = showEmptyHint,
)

fun ExpenseDayGroup.toHomeDayGroup(): HomeDayGroup = HomeDayGroup(
    dayTitle = dayTitle,
    dayTotal = dayTotal,
    transactions = transactions.map { it.toHomeTransactionItem() },
)

fun ExpenseDayGroup.toJournalDayGroup(): JournalDayGroup = JournalDayGroup(
    dayTitle = dayTitle,
    dayTotal = dayTotal,
    transactions = transactions.map { it.toJournalTransactionItem() },
)

fun ExpenseListItem.toJournalTransactionItem(): JournalTransactionItem = JournalTransactionItem(
    id = id,
    categoryId = categoryId,
    note = note,
    meta = meta,
    amount = amount,
    tag = tag,
)

fun ExpenseListItem.toHomeTransactionItem(): HomeTransactionItem = HomeTransactionItem(
    categoryId = categoryId,
    note = note,
    meta = meta,
    amount = amount,
    tag = tag,
)

fun EventListItemState.toEventBudgetCardState(): EventBudgetCardState = EventBudgetCardState(
    title = title,
    dateRange = dateRange,
    spentLabel = spentLabel,
    budgetLabel = budgetLabel,
    progress = progress,
    isOverBudget = isOverBudget,
    overAmountLabel = overAmountLabel,
)

fun DebtRecordUiState.toDebtRecordItem(): com.arduia.expense.ui.preview.DebtRecordItem =
    com.arduia.expense.ui.preview.DebtRecordItem(
        id = id,
        personName = personName,
        amount = amount,
        dueLabel = dueLabel,
        isSettled = isSettled,
    )

fun com.arduia.expense.feature.history.ReportsCategoryState.toReportsCategoryTriple(): Triple<String, String, Pair<String, Float>> =
    Triple(categoryId, label, amountLabel to share)

fun com.arduia.expense.feature.sharedcost.SharedHistoryItemState.toSharedHistoryItem(): com.arduia.expense.ui.preview.SharedHistoryItem =
    com.arduia.expense.ui.preview.SharedHistoryItem(
        title = title,
        dateLabel = dateLabel,
        total = total,
        peopleCount = peopleCount,
    )

fun com.arduia.expense.feature.auth.PinSetupStep.toUiPinSetupStep(): com.arduia.expense.ui.auth.PinSetupStep =
    when (this) {
        com.arduia.expense.feature.auth.PinSetupStep.ENTER -> com.arduia.expense.ui.auth.PinSetupStep.Create
        com.arduia.expense.feature.auth.PinSetupStep.CONFIRM -> com.arduia.expense.ui.auth.PinSetupStep.Confirm
        com.arduia.expense.feature.auth.PinSetupStep.SECURITY_QUESTION -> com.arduia.expense.ui.auth.PinSetupStep.SecurityQuestion
        com.arduia.expense.feature.auth.PinSetupStep.BIOMETRIC_OFFER -> com.arduia.expense.ui.auth.PinSetupStep.BiometricOffer
        com.arduia.expense.feature.auth.PinSetupStep.COMPLETE -> com.arduia.expense.ui.auth.PinSetupStep.BiometricOffer
    }

fun com.arduia.expense.feature.auth.PinEntryMode.toUiPinEntryMode(): com.arduia.expense.ui.auth.PinEntryMode =
    when (this) {
        com.arduia.expense.feature.auth.PinEntryMode.ENTRY -> com.arduia.expense.ui.auth.PinEntryMode.Entry
        com.arduia.expense.feature.auth.PinEntryMode.WRONG -> com.arduia.expense.ui.auth.PinEntryMode.Wrong
        com.arduia.expense.feature.auth.PinEntryMode.LOCKOUT -> com.arduia.expense.ui.auth.PinEntryMode.Lockout
        com.arduia.expense.feature.auth.PinEntryMode.RECOVERY -> com.arduia.expense.ui.auth.PinEntryMode.Recovery
    }
