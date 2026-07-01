package com.arduia.expense.feature.reports.ui.preview

data class ReportsCategoryUi(
    val categoryId: String,
    val label: String,
    val percentLabel: String,
    val amountLabel: String,
    val fraction: Float,
)

data class ReportsUiState(
    val periodLabel: String,
    val totalLabel: String,
    val dailyAvgLabel: String,
    val daysLabel: String,
    val categories: List<ReportsCategoryUi>,
    val uncategorized: Boolean = false,
    val empty: Boolean = false,
)

val previewReports = ReportsUiState(
    periodLabel = "May 2026",
    totalLabel = "$1,247",
    dailyAvgLabel = "$50",
    daysLabel = "25 days in",
    categories = listOf(
        ReportsCategoryUi("food", "Food", "38%", "$478", 0.38f),
        ReportsCategoryUi("transport", "Transport", "22%", "$274", 0.22f),
        ReportsCategoryUi("shopping", "Shopping", "18%", "$224", 0.18f),
        ReportsCategoryUi("bills", "Bills", "14%", "$170", 0.14f),
        ReportsCategoryUi("entertainment", "Entertainment", "8%", "$101", 0.08f),
    ),
)

val previewReportsWithOtherRollup = ReportsUiState(
    periodLabel = "May 2026",
    totalLabel = "$1,247",
    dailyAvgLabel = "$50",
    daysLabel = "25 days in",
    categories = listOf(
        ReportsCategoryUi("food", "Food", "30%", "$374", 0.30f),
        ReportsCategoryUi("transport", "Transport", "18%", "$224", 0.18f),
        ReportsCategoryUi("shopping", "Shopping", "16%", "$200", 0.16f),
        ReportsCategoryUi("bills", "Bills", "12%", "$150", 0.12f),
        ReportsCategoryUi("entertainment", "Entertainment", "10%", "$125", 0.10f),
        ReportsCategoryUi("other", "Other", "14%", "$174", 0.14f),
    ),
)

val previewReportsUncategorized = ReportsUiState(
    periodLabel = "May 2026",
    totalLabel = "$1,247",
    dailyAvgLabel = "$50",
    daysLabel = "25 days in",
    categories = emptyList(),
    uncategorized = true,
)

val previewReportsEmpty = ReportsUiState(
    periodLabel = "",
    totalLabel = "",
    dailyAvgLabel = "",
    daysLabel = "",
    categories = emptyList(),
    empty = true,
)

/** A specific past month with no spending, distinct from [previewReportsEmpty]'s global case. */
val previewReportsPeriodEmpty = ReportsUiState(
    periodLabel = "March 2026",
    totalLabel = "",
    dailyAvgLabel = "",
    daysLabel = "",
    categories = emptyList(),
    empty = true,
)
