package com.arduia.expense.feature.importexport

import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.storage.InMemoryDataStore

class InMemoryImportExportRepository(
    private val store: InMemoryDataStore,
) : ImportExportRepository {
    override suspend fun exportAll(format: ExportFormat): Result<String> = try {
        val records = store.allRecords()
        when (format) {
            ExportFormat.JSON -> {
                val body = records.joinToString(
                    separator = ",",
                    prefix = "[",
                    postfix = "]",
                ) { r ->
                    """{"id":"${r.id}","cents":${r.homeCurrencyAmount.valueInCents},"category":"${r.categoryId}"}"""
                }
                Result.Success(body)
            }
            ExportFormat.CSV -> {
                val header = "id,amountCents,categoryId,note,recordedAt"
                val rows = records.map { r ->
                    "${r.id},${r.homeCurrencyAmount.valueInCents},${r.categoryId},${r.note.orEmpty()},${r.recordedAtEpochMillis}"
                }
                Result.Success((listOf(header) + rows).joinToString("\n"))
            }
        }
    } catch (e: Exception) {
        Result.Error("Export failed")
    }

    override suspend fun importFrom(content: String, format: ExportFormat): Result<ImportSummary> =
        Result.Success(ImportSummary(importedCount = 0, skippedCount = 0))

    override suspend fun previewImport(content: String, format: ExportFormat): Result<List<FinanceRecord>> =
        Result.Success(emptyList())
}
