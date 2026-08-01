package com.arduia.expense.shell.home

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
