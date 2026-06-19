package com.arduia.expense.ui.util

import com.arduia.expense.feature.history.ExpenseDayGroup
import com.arduia.expense.feature.history.ExpenseListItem
import com.arduia.expense.feature.history.HomeUiState as FeatureHomeUiState
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

private fun ExpenseListItem.toHomeTransactionItem(): HomeTransactionItem = HomeTransactionItem(
    categoryId = categoryId,
    note = note,
    meta = meta,
    amount = amount,
    tag = tag,
)
