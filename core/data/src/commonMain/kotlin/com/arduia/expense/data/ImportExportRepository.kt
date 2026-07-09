package com.arduia.expense.data

import com.arduia.expense.domain.FinanceRecord

enum class ExportFormat {
    CSV,
    JSON,
}

data class ImportSummary(
    val importedCount: Int,
    val skippedCount: Int,
)

interface ImportExportRepository {
    suspend fun exportAll(format: ExportFormat): Result<String>

    /**
     * Per-record-type CSV export, keyed by filename ("expenses.csv", "events.csv", "debts.csv",
     * "shared_costs.csv") — packaged into a single zip by the platform layer (US-IE-1). Only
     * expenses.csv round-trips through [importFrom]/[previewImport]; the other files are
     * reference-only exports of data this repository doesn't otherwise expose as FinanceRecords.
     */
    suspend fun exportGrouped(): Result<Map<String, String>>

    suspend fun importFrom(
        content: String,
        format: ExportFormat,
    ): Result<ImportSummary>

    suspend fun previewImport(
        content: String,
        format: ExportFormat,
    ): Result<List<FinanceRecord>>
}
