package com.arduia.expense.feature.logging.ui.preview

import com.arduia.expense.ui.design.TagLinkKind
import com.arduia.expense.ui.design.TagLinkOption

data class ExpenseEntryState(
    val rawAmount: String = "",
    val selectedCategoryId: String = "food",
    val note: String = "",
    val dateLabel: String = "Today, May 25",
    val timeLabel: String = "12:30 PM",
    val recordedAtEpochMillis: Long = System.currentTimeMillis(),
    val linkedTagId: String? = null,
    val linkedTagKind: TagLinkKind? = null,
    val linkedTagLabel: String? = null,
    val showZeroValidation: Boolean = false,
    val showTagSheet: Boolean = false,
    val currencyCode: String = "USD",
)

val previewExpenseAmountTyped = ExpenseEntryState(rawAmount = "12.50")

val previewExpenseAmountZero = ExpenseEntryState(rawAmount = "0")

val previewExpenseAmountZeroValidation = ExpenseEntryState(
    rawAmount = "0",
    showZeroValidation = true,
)

val previewExpenseDetails = ExpenseEntryState(
    rawAmount = "12.50",
    note = "Lunch with M.",
    linkedTagId = "bali",
    linkedTagKind = TagLinkKind.Event,
    linkedTagLabel = "Bali Trip",
)

val previewExpenseDetailsNoteLimit = ExpenseEntryState(
    rawAmount = "86.00",
    note = "Team dinner at the rooftop place downtown — split the tasting menu, two bottles of wine, plus the service charge. Reimbursing Alex for my half since he put it all on his card this time around again tonight ok.",
)

val previewExpenseDraft = ExpenseEntryState(rawAmount = "12.50")

val previewTagEvents = listOf(
    TagLinkOption("bali", "Bali Trip", "May 12 — May 26", TagLinkKind.Event),
    TagLinkOption("wedding", "John's Wedding", "Jun 04 — Jun 06", TagLinkKind.Event),
)

val previewTagDebts = listOf(
    TagLinkOption("lent-john", "Lent · John", "$50", TagLinkKind.Debt),
    TagLinkOption("owe-sarah", "Owe · Sarah", "$30", TagLinkKind.Debt),
)
