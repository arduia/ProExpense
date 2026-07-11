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
 * Split/Debt rows need their own title so they don't fall back to the linked [FinanceRecord]'s
 * bookkeeping category label (e.g. "Shopping"/"Gift" — never meant to be user-facing here).
 */
fun debtRowTitle(
    personName: String,
    isLent: Boolean,
): String = if (isLent) "Lent to $personName" else "Owe $personName"

/** The type word for a debt row's subtitle — pair with a " · <time>" suffix at the call site. */
fun debtRowSubtitleType(isLent: Boolean): String = if (isLent) "Lent" else "Owe"

fun splitRowTitle(splitTitle: String?): String {
    val trimmed = splitTitle?.trim().orEmpty()
    return if (trimmed.isEmpty()) SPLIT_ROW_SUBTITLE_TYPE else "Split · $trimmed"
}

/** The type word for a split row's subtitle — pair with a " · <time>" suffix at the call site. */
const val SPLIT_ROW_SUBTITLE_TYPE = "Shared split"

/**
 * Single flip point to bring Split rows (and toggle-off debts — see
 * [com.arduia.expense.domain.Debt.recordAsTransaction]) into Home Recents — product wants those
 * hidden there for now. Journal always shows every kind. A toggle-on debt's own linked
 * [FinanceRecord] is a different case: it already counts toward spend/income totals, so it's
 * always visible regardless of this flag — see [isVisibleInHomeRecents]. Flip to `true` to
 * restore Split and toggle-off debts on Home Recents too.
 */
const val SHOW_SPLIT_AND_DEBT_ROWS = false

fun RecordKind.isVisibleInHomeRecents(): Boolean =
    when (this) {
        RecordKind.EXPENSE, RecordKind.INCOME, RecordKind.DEBT_LENT, RecordKind.DEBT_OWED -> true
        RecordKind.SPLIT -> SHOW_SPLIT_AND_DEBT_ROWS
    }
