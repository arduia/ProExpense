package com.arduia.expense.feature.eventbudget.ui.preview

import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.EventBudgetTone
import com.arduia.expense.ui.design.PlatformDateFormatter

data class EventLinkedExpenseUi(
    val id: String,
    val title: String,
    val categoryId: String,
    val categoryLabel: String,
    val amountLabel: String,
)

data class EventDetailUiState(
    val id: String,
    val title: String,
    val subtitle: String,
    val statusEyebrow: String? = null,
    val statusInlineChip: String? = null,
    val summary: EventBudgetSummaryState,
    val linkedCount: Int = 0,
    val linkedExpenses: List<EventLinkedExpenseUi> = emptyList(),
    val showAddTagged: Boolean = false,
    val readOnly: Boolean = false,
    val isClosed: Boolean = false,
)

data class EventCreateFormState(
    val name: String = "",
    val budgetRaw: String = "",
    val startEpochMillis: Long = System.currentTimeMillis(),
    val endEpochMillis: Long = System.currentTimeMillis(),
    // Defaults derived from the epoch fields above, not hardcoded — a bare EventCreateFormState()
    // for a brand-new event must show today's date, not a fixed placeholder month.
    val startLabel: String = PlatformDateFormatter.shortDateLabel(startEpochMillis),
    val endLabel: String = PlatformDateFormatter.shortDateLabel(endEpochMillis),
    val isDuplicateName: Boolean = false,
    val showBudgetError: Boolean = false,
) {
    val budgetIsEmpty: Boolean get() = budgetRaw.isEmpty()
    val budgetDisplay: String get() = "$" + AmountInput.formatDisplay(budgetRaw)
    val canSave: Boolean
        get() = name.isNotBlank() && !isDuplicateName && AmountInput.canProceed(budgetRaw)
}

const val EVENT_NAME_MAX = 30

val previewEventList = listOf(
    EventBudgetCardState(
        id = "bali",
        title = "Bali Trip",
        dateRange = "May 12 — May 26",
        spentLabel = "$1,240",
        budgetLabel = "of $2,000",
        progress = 0.62f,
    ),
    EventBudgetCardState(
        id = "wedding",
        title = "John's Wedding",
        dateRange = "Jun 04 — Jun 06",
        spentLabel = "$460",
        budgetLabel = "of $800",
        progress = 0.575f,
    ),
    EventBudgetCardState(
        id = "birthday",
        title = "Birthday party",
        dateRange = "Apr 28",
        spentLabel = "$45",
        budgetLabel = "",
        progress = 1f,
        isOverBudget = true,
        overAmountLabel = "over",
    ),
)

val previewEventCreateValid = EventCreateFormState(
    name = "Bali Trip 2026",
    budgetRaw = "2000",
    startLabel = "May 12",
    endLabel = "May 26",
)

val previewEventCreateErrors = EventCreateFormState(
    name = "Bali Trip",
    budgetRaw = "",
    startLabel = "May 12",
    endLabel = "May 26",
    isDuplicateName = true,
    showBudgetError = true,
)

private val previewBaliLinkedExpenses = listOf(
    EventLinkedExpenseUi("1", "Hotel night", "bills", "Bills", "$180"),
    EventLinkedExpenseUi("2", "Lunch · beach", "food", "Food", "$22"),
    EventLinkedExpenseUi("3", "Scooter rental", "transport", "Transport", "$15"),
    EventLinkedExpenseUi("4", "Souvenirs", "shopping", "Shopping", "$48"),
    EventLinkedExpenseUi("5", "Dinner · seafood", "food", "Food", "$64"),
)

val previewEventDetail = EventDetailUiState(
    id = "bali",
    title = "Bali Trip",
    subtitle = "May 12 — May 26 · 14 days left",
    statusEyebrow = "ACTIVE",
    summary = EventBudgetSummaryState(
        eyebrow = "REMAINING",
        remainingLabel = "$1,240",
        spentLabel = "$760",
        budgetLabel = "$2,000",
        spentCaption = "Spent",
        budgetCaption = "Budget",
        progress = 0.38f,
        tone = EventBudgetTone.OnTrack,
        footerStartLabel = "38% spent",
        footerEndLabel = "$88/day pace",
    ),
    linkedCount = 5,
    linkedExpenses = previewBaliLinkedExpenses,
    showAddTagged = true,
)

val previewEventDetailWarn = EventDetailUiState(
    id = "birthday",
    title = "Birthday party",
    subtitle = "Apr 28 · active",
    summary = EventBudgetSummaryState(
        eyebrow = "OVER BUDGET",
        remainingLabel = "−$22",
        spentLabel = "$422",
        budgetLabel = "$400",
        spentCaption = "Spent",
        budgetCaption = "Budget",
        progress = 1f,
        tone = EventBudgetTone.OverBudget,
        overNoticeLabel = "Over budget by $22 (105%)",
    ),
)

val previewEventDetailNoLinked = EventDetailUiState(
    id = "birthday",
    title = "Birthday party",
    subtitle = "Apr 28 · active",
    statusEyebrow = "ACTIVE",
    summary = EventBudgetSummaryState(
        eyebrow = "REMAINING",
        remainingLabel = "$400",
        spentLabel = "$0",
        budgetLabel = "$400",
        spentCaption = "Spent",
        budgetCaption = "Budget",
        progress = 0f,
        tone = EventBudgetTone.OnTrack,
        footerStartLabel = "0% spent",
        footerEndLabel = "$0/day pace",
    ),
    linkedCount = 0,
    linkedExpenses = emptyList(),
    showAddTagged = true,
)

val previewEventDetailClosed = EventDetailUiState(
    id = "wedding",
    title = "John's Wedding",
    subtitle = "Jun 04 — Jun 06 · archived",
    statusInlineChip = "CLOSED",
    summary = EventBudgetSummaryState(
        eyebrow = "FINAL · REMAINING",
        remainingLabel = "$340",
        progress = 0.575f,
        isFinal = true,
    ),
    readOnly = true,
    isClosed = true,
)
