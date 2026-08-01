package com.arduia.expense.ui.preview

import com.arduia.expense.shell.home.HomeActiveEventState
import com.arduia.expense.shell.home.HomeBudgetSummaryState
import com.arduia.expense.shell.home.HomeDayGroup
import com.arduia.expense.shell.home.HomeTransactionItem
import com.arduia.expense.shell.home.HomeUiState
import com.arduia.expense.ui.design.ProRowKind

// The state types themselves live in `:shell` commonMain (shared with the iOS shell) — only the
// Compose preview fixtures below stay here.

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
                                note = "Split · Dinner",
                                meta = "Shared split · 07:20 PM",
                                amount = "$49.00",
                                rowKind = ProRowKind.SPLIT,
                                linkedId = "sc1",
                            ),
                            HomeTransactionItem(
                                id = "d1",
                                categoryId = "",
                                note = "Lent to John",
                                meta = "Lent · 03:10 PM",
                                amount = "$50.00",
                                rowKind = ProRowKind.DEBT_LENT,
                                linkedId = "d1",
                            ),
                            HomeTransactionItem(
                                id = "d2",
                                categoryId = "",
                                note = "Owe Priya",
                                meta = "Owe · 09:00 AM",
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
