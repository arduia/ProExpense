package com.arduia.expense.ui.home

// Sample preview / screenshot data — mirrors design_handoff_v2/screens-manifest.yaml flow-01-home-*.

object HomeSampleData {
    val empty = HomeUiState(
        profileName = "Maya",
        homeCurrencyCode = "USD",
        dateLabel = "Wed · May 25",
        monthLabel = "May",
        monthSpendLabel = "$0",
        spendHelperText = "No spending logged yet this month",
        recentGroups = emptyList(),
    )

    val casual = HomeUiState(
        profileName = "Maya",
        homeCurrencyCode = "USD",
        dateLabel = "Wed · May 25",
        monthLabel = "May",
        monthSpendLabel = "$80.90",
        spendDeltaLabel = "12% from last month",
        recentGroups = listOf(
            HomeDayGroup(
                title = "Today",
                totalLabel = "$42.40",
                items = listOf(
                    HomeTransactionItem(
                        id = "1",
                        categoryId = "food",
                        note = "Lunch with M.",
                        metaLabel = "Food · 12:30 PM",
                        amountLabel = "$12.40",
                    ),
                    HomeTransactionItem(
                        id = "2",
                        categoryId = "transport",
                        note = "Transport",
                        metaLabel = "Transport · 9:15 AM",
                        amountLabel = "$3.50",
                    ),
                    HomeTransactionItem(
                        id = "3",
                        categoryId = "coffee",
                        note = "Oat latte",
                        metaLabel = "Coffee · 8:40 AM",
                        amountLabel = "$5.00",
                    ),
                    HomeTransactionItem(
                        id = "4",
                        categoryId = "entertainment",
                        note = "Movie · Dune",
                        metaLabel = "Entertainment · 8:10 PM",
                        amountLabel = "$18.00",
                        eventTag = "Bali Trip",
                    ),
                    HomeTransactionItem(
                        id = "5",
                        categoryId = "food",
                        note = "Groceries",
                        metaLabel = "Food · 5:30 PM",
                        amountLabel = "$42.00",
                    ),
                ),
            ),
        ),
    )
}
