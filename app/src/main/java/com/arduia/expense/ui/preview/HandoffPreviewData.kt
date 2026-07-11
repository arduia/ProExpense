package com.arduia.expense.ui.preview

import com.arduia.expense.ui.design.ProRowKind

data class HomeTransactionItem(
    val id: String = "",
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    val isIncome: Boolean = false,
    val tag: String? = null,
    val rowKind: ProRowKind = if (isIncome) ProRowKind.INCOME else ProRowKind.EXPENSE,
    val linkedId: String? = null,
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
    val sparklinePoints: List<Float> = emptyList(),
    val isLoading: Boolean = false,
) {
    val isEmpty: Boolean get() = dayGroups.isEmpty() && showEmptyHint && !isLoading
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

val previewHomeEmpty =
    HomeUiState(
        greetingName = "Maya",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$0",
        showEmptyHint = true,
    )

val previewHomeLoading =
    HomeUiState(
        greetingName = "Maya",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$0",
        showEmptyHint = true,
        isLoading = true,
    )

val previewHomeCasual =
    HomeUiState(
        greetingName = "Maya",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$80.90",
        monthDelta = "-12% from last month",
        showEmptyHint = false,
        sparklinePoints = listOf(1200f, 800f, 1600f, 900f, 2200f, 1400f, 8090f),
        dayGroups =
            listOf(
                HomeDayGroup(
                    dayTitle = "Today · May 25",
                    dayTotal = "$81.40",
                    transactions =
                        listOf(
                            HomeTransactionItem(
                                id = "",
                                categoryId = "food",
                                note = "Lunch with M.",
                                meta = "Food · 12:30 PM",
                                amount = "$12.40",
                            ),
                            HomeTransactionItem(
                                id = "",
                                categoryId = "transport",
                                note = "Transport",
                                meta = "Transport · 09:15 AM",
                                amount = "$3.50",
                            ),
                            HomeTransactionItem(
                                id = "",
                                categoryId = "coffee",
                                note = "Oat latte",
                                meta = "Coffee · 08:40 AM",
                                amount = "$5.00",
                            ),
                            HomeTransactionItem(
                                id = "",
                                categoryId = "entertainment",
                                note = "Movie · Dune",
                                meta = "Entertainment · 08:10 PM",
                                amount = "$18.00",
                                tag = "Bali Trip",
                            ),
                            HomeTransactionItem(
                                id = "",
                                categoryId = "food",
                                note = "Groceries",
                                meta = "Food · 05:30 PM",
                                amount = "$42.00",
                            ),
                        ),
                ),
            ),
    )

/** Expense + Split + Debt-lent + Debt-owed rows side by side — distinct icon/tint per kind. */
val previewHomeMixedKinds =
    HomeUiState(
        greetingName = "Maya",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$61.40",
        showEmptyHint = false,
        sparklinePoints = previewHomeCasual.sparklinePoints,
        dayGroups =
            listOf(
                HomeDayGroup(
                    dayTitle = "Today · May 25",
                    dayTotal = "$61.40",
                    transactions =
                        listOf(
                            HomeTransactionItem(
                                id = "r1",
                                categoryId = "food",
                                note = "Lunch with M.",
                                meta = "Food · 12:30 PM",
                                amount = "$12.40",
                            ),
                            HomeTransactionItem(
                                id = "sc1",
                                categoryId = "shopping",
                                note = "Dinner split",
                                meta = "Split · 07:20 PM",
                                amount = "$49.00",
                                rowKind = ProRowKind.SPLIT,
                                linkedId = "sc1",
                            ),
                            HomeTransactionItem(
                                id = "d1",
                                categoryId = "",
                                note = "John",
                                meta = "Lent · 03:10 PM",
                                amount = "$50.00",
                                rowKind = ProRowKind.DEBT_LENT,
                                linkedId = "d1",
                            ),
                            HomeTransactionItem(
                                id = "d2",
                                categoryId = "",
                                note = "Priya",
                                meta = "Borrowed · 09:00 AM",
                                amount = "$25.00",
                                rowKind = ProRowKind.DEBT_OWED,
                                linkedId = "d2",
                            ),
                        ),
                ),
            ),
    )

val previewHomeBudget =
    HomeUiState(
        greetingName = "Siti",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$420.00",
        monthDelta = "-8% from last month",
        showEmptyHint = false,
        sparklinePoints = previewHomeCasual.sparklinePoints,
        budgetSummary =
            HomeBudgetSummaryState(
                spentLabel = "$420.00",
                budgetLabel = "of $500.00",
                progress = 0.84f,
                statusLabel = "On track",
                isOverBudget = false,
            ),
        dayGroups = previewHomeCasual.dayGroups,
    )

val previewHomeEvent =
    HomeUiState(
        greetingName = "Carlos",
        dateLabel = "WED · MAY 25",
        monthLabel = "MAY",
        monthSpend = "$1,240.00",
        monthDelta = "+5% from last month",
        showEmptyHint = false,
        sparklinePoints = previewHomeCasual.sparklinePoints,
        activeEvent =
            HomeActiveEventState(
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
