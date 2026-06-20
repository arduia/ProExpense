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
                    """{"id":"${r.id}","cents":${r.homeCurrencyAmount.valueInCents},"category":"${r.categoryId}","recordedAt":${r.recordedAtEpochMillis}}"""
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

    override suspend fun importFrom(content: String, format: ExportFormat): Result<ImportSummary> = try {
        val parsed = when (format) {
            ExportFormat.CSV -> ImportParser.parseCsv(content)
            ExportFormat.JSON -> ImportParser.parseJson(content)
        }
        var imported = 0
        var skipped = 0
        parsed.forEach { record ->
            val existing = store.allRecords().any { it.id == record.id }
            if (existing) {
                skipped += 1
            } else {
                store.insertRecord(record)
                imported += 1
            }
        }
        Result.Success(ImportSummary(importedCount = imported, skippedCount = skipped))
    } catch (e: Exception) {
        Result.Error("Import failed")
    }

    override suspend fun previewImport(content: String, format: ExportFormat): Result<List<FinanceRecord>> = try {
        val parsed = when (format) {
            ExportFormat.CSV -> ImportParser.parseCsv(content)
            ExportFormat.JSON -> ImportParser.parseJson(content)
        }
        Result.Success(parsed)
    } catch (e: Exception) {
        Result.Error("Preview failed")
    }
}
