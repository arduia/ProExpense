package com.arduia.expense.ui.preview

import com.arduia.expense.ui.design.EventBudgetCardState

data class HomeTransactionItem(
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    val tag: String? = null,
)

data class HomeDayGroup(
    val dayTitle: String,
    val dayTotal: String,
    val transactions: List<HomeTransactionItem>,
)

data class HomeUiState(
    val greetingName: String = "",
    val monthSpend: String = "$0",
    val monthDelta: String? = null,
    val dayGroups: List<HomeDayGroup> = emptyList(),
    val showEmptyHint: Boolean = true,
    val budgetSummary: HomeBudgetSummaryState? = null,
    val activeEvent: HomeActiveEventState? = null,
)

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
    monthSpend = "$0",
    showEmptyHint = true,
)

val previewHomeCasual = HomeUiState(
    greetingName = "Maya",
    monthSpend = "$80.90",
    monthDelta = "-12% from last month",
    showEmptyHint = false,
    dayGroups = listOf(
        HomeDayGroup(
            dayTitle = "Today",
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

data class JournalTransactionItem(
    val id: String,
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    val tag: String? = null,
)

data class JournalDayGroup(
    val dayTitle: String,
    val dayTotal: String,
    val transactions: List<JournalTransactionItem>,
)

val previewJournalFilters = listOf("All", "Food", "Transport", "Bills", "More")

val previewJournalList = listOf(
    JournalDayGroup(
        dayTitle = "Today, May 25",
        dayTotal = "$81.40",
        transactions = previewHomeCasual.dayGroups.first().transactions.mapIndexed { index, item ->
            JournalTransactionItem(
                id = "t$index",
                categoryId = item.categoryId,
                note = item.note,
                meta = item.meta,
                amount = item.amount,
                tag = item.tag,
            )
        },
    ),
    JournalDayGroup(
        dayTitle = "Yesterday, May 24",
        dayTotal = "$34.20",
        transactions = listOf(
            JournalTransactionItem("b1", "bills", "Electric bill", "Bills · 06:00 PM", "$28.00"),
            JournalTransactionItem("t1", "transport", "Bus fare", "Transport · 08:00 AM", "$6.20"),
        ),
    ),
)

val previewReportsCategories = listOf(
    Triple("food", "Food", "$42.00" to 0.52f),
    Triple("transport", "Transport", "$18.50" to 0.23f),
    Triple("entertainment", "Entertainment", "$12.40" to 0.15f),
    Triple("bills", "Bills", "$8.00" to 0.10f),
)

val previewCategoryItems = listOf(
    "food" to "Food",
    "transport" to "Transport",
    "bills" to "Bills",
    "entertainment" to "Entertainment",
    "coffee" to "Coffee",
    "shopping" to "Shopping",
)

data class DebtRecordItem(
    val id: String,
    val personName: String,
    val amount: String,
    val dueLabel: String,
    val isSettled: Boolean = false,
)

val previewDebtLent = listOf(
    DebtRecordItem("1", "John", "$50.00", "Due May 30"),
    DebtRecordItem("2", "Sarah", "$30.00", "Due Jun 5"),
)

val previewDebtOwe = listOf(
    DebtRecordItem("3", "David", "$25.00", "Due May 28"),
)

data class SharedHistoryItem(
    val title: String,
    val dateLabel: String,
    val total: String,
    val peopleCount: Int,
)

val previewSharedHistory = listOf(
    SharedHistoryItem("Dinner at Izakaya", "May 20", "$120.00", 4),
    SharedHistoryItem("Weekend groceries", "May 12", "$86.40", 3),
)

val previewEventDetailTransactions = listOf(
    HomeTransactionItem("entertainment", "Movie · Dune", "Entertainment · 08:10 PM", "$18.00"),
    HomeTransactionItem("food", "Beach lunch", "Food · 01:00 PM", "$32.00"),
    HomeTransactionItem("transport", "Taxi to airport", "Transport · 06:30 AM", "$24.00"),
)

val previewEventListEmpty: List<EventBudgetCardState> = emptyList()

val previewEventListActive = listOf(
    EventBudgetCardState(
        id = "event_bali",
        title = "Bali Trip",
        dateRange = "May 12 — May 26",
        spentLabel = "$1,240",
        budgetLabel = "of $2,000",
        progress = 0.62f,
    ),
    EventBudgetCardState(
        id = "event_wedding",
        title = "John's Wedding",
        dateRange = "Jun 14 — Jun 15",
        spentLabel = "$800",
        budgetLabel = "of $1,500",
        progress = 0.53f,
    ),
    EventBudgetCardState(
        id = "event_birthday",
        title = "Birthday party",
        dateRange = "Apr 20 — Apr 20",
        spentLabel = "$545",
        budgetLabel = "of $500",
        progress = 1f,
        isOverBudget = true,
        overAmountLabel = "over",
    ),
)
