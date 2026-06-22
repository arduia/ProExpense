package com.arduia.expense.ui.preview

data class HomeTransactionItem(
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    val tag: String? = null,
    val id: String = "",
)

data class HomeDayGroup(
    val dayTitle: String,
    val dayTotal: String,
    val transactions: List<HomeTransactionItem>,
)

data class HomeUiState(
    val greetingName: String = "",
    val dateLabel: String = "",
    val monthLabel: String = "",
    val monthSpend: String = "$0",
    val monthDelta: String? = null,
    val dayGroups: List<HomeDayGroup> = emptyList(),
    val showEmptyHint: Boolean = true,
    val budgetSummary: HomeBudgetSummaryState? = null,
    val activeEvent: HomeActiveEventState? = null,
) {
    val isEmpty: Boolean get() = dayGroups.isEmpty() && showEmptyHint
    val greetingPrefixRes: String get() = if (isEmpty) "welcome" else "hi"
}

data class HomeBudgetSummaryState(
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val statusLabel: String,
    val isOverBudget: Boolean,
)

data class HomeActiveEventState(
    val eventId: String,
    val title: String,
    val dateRange: String,
    val spentLabel: String,
    val budgetLabel: String,
    val progress: Float,
    val isOverBudget: Boolean,
)

val previewHomeEmpty = HomeUiState(
    greetingName = "Maya",
    dateLabel = "WED · MAY 25",
    monthLabel = "MAY",
    monthSpend = "$0",
    showEmptyHint = true,
)

val previewHomeCasual = HomeUiState(
    greetingName = "Maya",
    dateLabel = "WED · MAY 25",
    monthLabel = "MAY",
    monthSpend = "$80.90",
    monthDelta = "-12% from last month",
    showEmptyHint = false,
    dayGroups = listOf(
        HomeDayGroup(
            dayTitle = "Today · May 25",
            dayTotal = "$81.40",
            transactions = listOf(
                HomeTransactionItem("food", "Lunch with M.", "Food · 12:30 PM", "$12.40"),
                HomeTransactionItem("transport", "Transport", "Transport · 09:15 AM", "$3.50"),
                HomeTransactionItem("coffee", "Oat latte", "Coffee · 08:40 AM", "$5.00"),
                HomeTransactionItem("entertainment", "Movie · Dune", "Entertainment · 08:10 PM", "$18.00", "Bali Trip"),
                HomeTransactionItem("food", "Groceries", "Food · 05:30 PM", "$42.00"),
            ),
        ),
    ),
)

val previewHomeBudget = HomeUiState(
    greetingName = "Siti",
    dateLabel = "WED · MAY 25",
    monthLabel = "MAY",
    monthSpend = "$420.00",
    monthDelta = "-8% from last month",
    showEmptyHint = false,
    budgetSummary = HomeBudgetSummaryState(
        spentLabel = "$420.00",
        budgetLabel = "of $500.00",
        progress = 0.84f,
        statusLabel = "On track",
        isOverBudget = false,
    ),
    dayGroups = previewHomeCasual.dayGroups,
)

val previewHomeEvent = HomeUiState(
    greetingName = "Carlos",
    dateLabel = "WED · MAY 25",
    monthLabel = "MAY",
    monthSpend = "$1,240.00",
    monthDelta = "+5% from last month",
    showEmptyHint = false,
    activeEvent = HomeActiveEventState(
        eventId = "event_bali",
        title = "Bali Trip",
        dateRange = "May 12 — May 26",
        spentLabel = "$1,240",
        budgetLabel = "of $2,000",
        progress = 0.62f,
        isOverBudget = false,
    ),
    dayGroups = previewHomeCasual.dayGroups,
)
