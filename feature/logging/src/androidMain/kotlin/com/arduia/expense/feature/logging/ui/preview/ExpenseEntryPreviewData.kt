package com.arduia.expense.feature.logging.ui.preview

import com.arduia.expense.domain.RecordType
import com.arduia.expense.ui.design.TagLinkKind
import com.arduia.expense.ui.design.TagLinkOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun defaultDateLabel(epochMillis: Long): String =
    SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

private fun defaultTimeLabel(epochMillis: Long): String =
    SimpleDateFormat("h:mm a", Locale.US).format(Calendar.getInstance().apply { timeInMillis = epochMillis }.time)

// Fixed rather than System.currentTimeMillis() — previews render at build time and back
// Roborazzi screenshot baselines, so a live clock would make every capture non-deterministic.
private const val PREVIEW_RECORDED_AT_EPOCH_MILLIS = 1_748_176_200_000L // 2025-05-25 12:30 UTC

data class ExpenseEntryState(
    val rawAmount: String = "",
    val selectedCategoryId: String = "food",
    val note: String = "",
    val recordedAtEpochMillis: Long = PREVIEW_RECORDED_AT_EPOCH_MILLIS,
    val dateLabel: String = defaultDateLabel(recordedAtEpochMillis),
    val timeLabel: String = defaultTimeLabel(recordedAtEpochMillis),
    val linkedTagId: String? = null,
    val linkedTagKind: TagLinkKind? = null,
    val linkedTagLabel: String? = null,
    val showZeroValidation: Boolean = false,
    val showTagSheet: Boolean = false,
    val currencyCode: String = "USD",
    val homeCurrencyCode: String = "USD",
    val exchangeRateRaw: String = "1",
    val showCurrencySheet: Boolean = false,
    val type: RecordType = RecordType.EXPENSE,
)

/** True when the entry's currency differs from the account's home currency (US-LOG multi-currency). */
fun ExpenseEntryState.isForeignCurrency(): Boolean = currencyCode != homeCurrencyCode

/** Same-currency entries never need a rate; foreign-currency entries need a positive manual rate. */
fun ExpenseEntryState.hasValidExchangeRate(): Boolean =
    !isForeignCurrency() || (exchangeRateRaw.trim().toDoubleOrNull()?.let { it > 0.0 } == true)

val previewExpenseAmountTyped = ExpenseEntryState(rawAmount = "12.50")

val previewIncomeAmountTyped =
    ExpenseEntryState(rawAmount = "500.00", selectedCategoryId = "salary", type = RecordType.INCOME)

val previewExpenseAmountZero = ExpenseEntryState(rawAmount = "0")

val previewExpenseAmountZeroValidation =
    ExpenseEntryState(
        rawAmount = "0",
        showZeroValidation = true,
    )

val previewExpenseDetails =
    ExpenseEntryState(
        rawAmount = "12.50",
        note = "Lunch with M.",
        linkedTagId = "bali",
        linkedTagKind = TagLinkKind.Event,
        linkedTagLabel = "Bali Trip",
    )

val previewExpenseDetailsNoteLimit =
    ExpenseEntryState(
        rawAmount = "86.00",
        note =
            "Team dinner at the rooftop place downtown — split the tasting menu, two bottles of " +
                "wine, plus the service charge. Reimbursing Alex for my half since he put it all on his card.",
    )

val previewExpenseDraft = ExpenseEntryState(rawAmount = "12.50")

// Guards against the category chip area growing unbounded and pushing NumericKeypad's Save/Next
// buttons off-screen — pairs with a long customCategories list at the call site, not a field here.
val previewExpenseAmountManyCategories = ExpenseEntryState(rawAmount = "12.50")

val previewExpenseAmountForeignCurrency =
    ExpenseEntryState(
        rawAmount = "45.00",
        currencyCode = "EUR",
        homeCurrencyCode = "USD",
    )

val previewExpenseDetailsForeignCurrency =
    ExpenseEntryState(
        rawAmount = "45.00",
        currencyCode = "EUR",
        homeCurrencyCode = "USD",
        exchangeRateRaw = "1.08",
        note = "Dinner in Lisbon",
    )

val previewExpenseDetailsForeignCurrencyNoRate =
    ExpenseEntryState(
        rawAmount = "45.00",
        currencyCode = "EUR",
        homeCurrencyCode = "USD",
        exchangeRateRaw = "",
    )

val previewTagEvents =
    listOf(
        TagLinkOption("bali", "Bali Trip", "May 12 — May 26", TagLinkKind.Event),
        TagLinkOption("wedding", "John's Wedding", "Jun 04 — Jun 06", TagLinkKind.Event),
    )

val previewTagDebts =
    listOf(
        TagLinkOption("lent-john", "Lent · John", "$50", TagLinkKind.Debt),
        TagLinkOption("owe-sarah", "Owe · Sarah", "$30", TagLinkKind.Debt),
    )
