package com.arduia.expense.shell.home

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordKind
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SPLIT_ROW_SUBTITLE_TYPE
import com.arduia.expense.domain.debtRowSubtitleType
import com.arduia.expense.domain.debtRowTitle
import com.arduia.expense.domain.kind
import com.arduia.expense.domain.linkedRowId
import com.arduia.expense.domain.splitRowTitle
import com.arduia.expense.domain.tagLabel
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProRowKind
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.resolveCategoryLabel

// One record (or toggle-off debt) -> one Home row. Split out of HomeStateMapper so each file keeps
// a single job: this one is the row shape, that one is the screen shape.

private fun FinanceRecord.rowKind(): ProRowKind =
    when (kind()) {
        RecordKind.EXPENSE -> ProRowKind.EXPENSE
        RecordKind.INCOME -> ProRowKind.INCOME
        RecordKind.SPLIT -> ProRowKind.SPLIT
        RecordKind.DEBT_LENT -> ProRowKind.DEBT_LENT
        RecordKind.DEBT_OWED -> ProRowKind.DEBT_OWED
    }

private fun FinanceRecord.rowNoteAndMeta(
    rowKind: ProRowKind,
    linkedId: String?,
    linkNames: HomeLinkNames,
): Pair<String, String> {
    val timeLabel = PlatformDateFormatter.timeLabel(recordedAtEpochMillis)
    return when (rowKind) {
        ProRowKind.SPLIT ->
            splitRowTitle(linkedId?.let { linkNames.sharedCostNames[it] }) to "$SPLIT_ROW_SUBTITLE_TYPE · $timeLabel"
        ProRowKind.DEBT_LENT, ProRowKind.DEBT_OWED -> {
            val isLent = rowKind == ProRowKind.DEBT_LENT
            val personName = linkedId?.let { linkNames.debtNames[it] }.orEmpty()
            debtRowTitle(personName, isLent) to "${debtRowSubtitleType(isLent)} · $timeLabel"
        }
        ProRowKind.EXPENSE, ProRowKind.INCOME -> {
            val categoryLabel = resolveCategoryLabel(categoryId.value, linkNames.categoryNames)
            note?.trim().orEmpty().ifEmpty { categoryLabel } to timeLabel
        }
    }
}

internal fun FinanceRecord.toHomeTransactionItem(linkNames: HomeLinkNames): HomeTransactionItem {
    val rowKind = rowKind()
    // The row's own badge/note already convey "this is a split/debt" — an "@ tag" chip repeating
    // the same title underneath would be redundant.
    val suppressTag = rowKind == ProRowKind.SPLIT || rowKind == ProRowKind.DEBT_LENT || rowKind == ProRowKind.DEBT_OWED
    val linkedId = linkedRowId()
    val (rowNote, rowMeta) = rowNoteAndMeta(rowKind, linkedId, linkNames)
    return HomeTransactionItem(
        id = id.value,
        categoryId = categoryId.value,
        note = rowNote,
        meta = rowMeta,
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        isIncome = type == RecordType.INCOME,
        tag =
            if (suppressTag) {
                null
            } else {
                link.tagLabel(linkNames.eventNames, linkNames.debtNames, linkNames.sharedCostNames)
            },
        rowKind = rowKind,
        linkedId = linkedId,
    )
}

internal fun Debt.toDebtHomeTransactionItem(): HomeTransactionItem {
    val isLent = direction == DebtDirection.OWED_TO_ME
    return HomeTransactionItem(
        id = id.value,
        categoryId = "",
        note = debtRowTitle(personName, isLent),
        meta = "${debtRowSubtitleType(isLent)} · ${PlatformDateFormatter.timeLabel(recordedAtEpochMillis)}",
        amount = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(money.currency.code)),
        rowKind = if (isLent) ProRowKind.DEBT_LENT else ProRowKind.DEBT_OWED,
        linkedId = id.value,
    )
}
