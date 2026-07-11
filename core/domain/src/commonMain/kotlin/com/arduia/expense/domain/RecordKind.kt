package com.arduia.expense.domain

/**
 * What kind of entry a [FinanceRecord] represents for display purposes — distinct from
 * [RecordType] (expense/income direction). Presentation layers (Home Recents, Journal) map this
 * to their own row-kind/badge types; this is the single place the underlying rule lives so it
 * can't drift between the two.
 */
enum class RecordKind { EXPENSE, INCOME, SPLIT, DEBT_LENT, DEBT_OWED }

/**
 * A debt's own linked record reuses the [com.arduia.expense.domain.DebtId] as its [RecordId] (see
 * the storage layer's `toFinanceRecord()`), distinguishing "this record IS a debt's transaction"
 * from an unrelated expense a user manually tagged to a debt via the @ picker — same
 * [RecordLink.ToDebt] shape, different, unrelated id. [RecordLink.ToSharedCost] has no such
 * ambiguity: splits are never manually taggable, so it always means "this record IS the split".
 */
fun FinanceRecord.kind(): RecordKind {
    val isOwnDebtRecord = (link as? RecordLink.ToDebt)?.debtId?.value == id.value
    return when {
        link is RecordLink.ToSharedCost -> RecordKind.SPLIT
        link is RecordLink.ToDebt && isOwnDebtRecord ->
            if (type == RecordType.EXPENSE) RecordKind.DEBT_LENT else RecordKind.DEBT_OWED
        type == RecordType.INCOME -> RecordKind.INCOME
        else -> RecordKind.EXPENSE
    }
}

/**
 * The split/debt id to navigate to for a [RecordKind.SPLIT]/[RecordKind.DEBT_LENT]/
 * [RecordKind.DEBT_OWED] record, `null` otherwise.
 */
fun FinanceRecord.linkedRowId(): String? =
    when {
        link is RecordLink.ToSharedCost -> link.sharedCostId.value
        link is RecordLink.ToDebt && link.debtId.value == id.value -> link.debtId.value
        else -> null
    }

/**
 * Single flip point to bring Split/Debt rows back into Home Recents and Journal — product wants
 * only plain Expense/Income visible there for now. Flip to `true` to restore them.
 */
const val SHOW_SPLIT_AND_DEBT_ROWS = false

fun RecordKind.isVisibleInFeed(): Boolean =
    SHOW_SPLIT_AND_DEBT_ROWS ||
        this == RecordKind.EXPENSE ||
        this == RecordKind.INCOME
