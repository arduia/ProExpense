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

data class MoreImportUiState(
    val fileName: String? = null,
    val previewCount: Int? = null,
    val errorMessage: String? = null,
    val isImporting: Boolean = false,
    val resultMessage: String? = null,
    val needsPassword: Boolean = false,
    val password: String = "",
)

val previewMoreExportFiles =
    listOf(
        MoreExportFileUi("expenses.csv", "All logged expenses + @ tags"),
        MoreExportFileUi("events.csv", "Event budgets & status"),
        MoreExportFileUi("debts.csv", "Lent / owed records"),
        MoreExportFileUi("shared_costs.csv", "Saved bill splits"),
    )

val previewMoreImportEmpty = MoreImportUiState()

val previewMoreImportPicked = MoreImportUiState(fileName = "backup-2026-05.csv", previewCount = 42)

val previewMoreImportError =
    MoreImportUiState(
        fileName = "notes.txt",
        errorMessage = "Couldn't read this file as a Pro Expense backup.",
    )

val previewMoreImportLocked =
    MoreImportUiState(
        fileName = "pro-expense-export-20260702.zip",
        needsPassword = true,
    )

val previewMoreClearOptions =
    listOf(
        MoreClearOptionUi("expenses", "Expenses", "All logged entries"),
        MoreClearOptionUi("events", "Events", "Budgets & linked tags"),
        MoreClearOptionUi("debts", "Debts", "Lent / owed records"),
        MoreClearOptionUi("shared", "Shared costs", "Saved splits"),
        MoreClearOptionUi("everything", "Everything", "Reset the app fully", destructive = true),
    )
