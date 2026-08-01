package com.arduia.expense.ui.design

/**
 * What kind of row this is — drives badge icon/tint and tap destination, independent of
 * [ProTransactionRowModel.categoryId] (a Split/Debt row still carries a category for
 * filter/report bucketing, but never renders its badge from it).
 */
enum class ProRowKind { EXPENSE, INCOME, SPLIT, DEBT_LENT, DEBT_OWED }

/**
 * Platform-neutral row payload: Compose renders it via `TransactionRow`, SwiftUI reads the same
 * fields through the iOS framework, so the mapping from a record to a row stays in `commonMain`
 * instead of being written twice.
 */
data class ProTransactionRowModel(
    val id: String,
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    /** True for an income record — the row renders the amount in the success/green tone. */
    val isIncome: Boolean = false,
    val tag: String? = null,
    /** Secondary line for the linked tag (e.g. its date range) shown on Journal Detail. */
    val tagSubtitle: String? = null,
    /** The record's actual note, pre-fallback — [note] substitutes a placeholder when blank. */
    val rawNote: String? = null,
    /** "Today · 12:44 AM" style label for Journal Detail — [meta] is category + time, not a date. */
    val detailDateTimeLabel: String? = null,
    /** Set when [tag] links to an event, so Journal Detail can navigate to that event's detail. */
    val linkedEventId: String? = null,
    val rowKind: ProRowKind = if (isIncome) ProRowKind.INCOME else ProRowKind.EXPENSE,
    /** Split/debt id to navigate to when [rowKind] isn't a plain [ProRowKind.EXPENSE]/[ProRowKind.INCOME]. */
    val linkedId: String? = null,
    /** Renders a [ProRowKind.DEBT_OWED] amount in success/green instead of the default color —
     *  screen-specific (Home Recents wants this, Journal doesn't), so it's set by the caller
     *  rather than derived from [rowKind] alone. */
    val emphasizeOwedAsIncome: Boolean = false,
)
