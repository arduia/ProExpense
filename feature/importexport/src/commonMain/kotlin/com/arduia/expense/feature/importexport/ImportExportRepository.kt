package com.arduia.expense.feature.importexport

import com.arduia.expense.data.Result
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

    suspend fun importFrom(content: String, format: ExportFormat): Result<ImportSummary>

    suspend fun previewImport(content: String, format: ExportFormat): Result<List<FinanceRecord>>
}
