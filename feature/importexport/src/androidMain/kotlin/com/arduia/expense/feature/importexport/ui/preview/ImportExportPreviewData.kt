package com.arduia.expense.feature.importexport.ui.preview

data class MoreExportFileUi(
    val fileName: String,
    val subtitle: String,
)

data class MoreClearOptionUi(
    val id: String,
    val label: String,
    val subtitle: String,
    val destructive: Boolean = false,
)

val previewMoreExportFiles = listOf(
    MoreExportFileUi("expenses.csv", "All logged expenses + @ tags"),
    MoreExportFileUi("events.csv", "Event budgets & status"),
    MoreExportFileUi("debts.csv", "Lent / owed records"),
    MoreExportFileUi("shared_costs.csv", "Saved bill splits"),
)

val previewMoreClearOptions = listOf(
    MoreClearOptionUi("expenses", "Expenses", "All logged entries"),
    MoreClearOptionUi("events", "Events", "Budgets & linked tags"),
    MoreClearOptionUi("debts", "Debts", "Lent / owed records"),
    MoreClearOptionUi("shared", "Shared costs", "Saved splits"),
    MoreClearOptionUi("everything", "Everything", "Reset the app fully", destructive = true),
)
