package com.arduia.expense.feature.importexport

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.storage.EntityDataStore

class SqlDelightImportExportRepository(
    private val store: EntityDataStore,
) : ImportExportRepository {
    override suspend fun exportAll(format: ExportFormat): Result<String> = try {
        val records = store.allRecords()
        when (format) {
            ExportFormat.JSON -> Result.Success(encodeRecordsJson(records))
            ExportFormat.CSV -> Result.Success(encodeRecordsCsvPlain(records))
        }
    } catch (e: Exception) {
        Result.Error("Export failed")
    }

    override suspend fun exportEncryptedArchive(password: String): Result<ByteArray> = try {
        if (password.isBlank()) return Result.Error("Password required")
        val payload = ExportArchive.buildArchive(
            password = password,
            records = store.allRecords(),
            events = store.allEvents(),
            debts = store.allDebts(),
            sharedCosts = store.allSharedCosts(),
        )
        val files = mapOf(
            ExportArchive.MANIFEST_FILE to ExportArchive.manifestToJson(payload.manifest),
            ExportArchive.RECORDS_FILE to payload.recordsCsv,
            ExportArchive.EVENTS_FILE to payload.eventsCsv,
            ExportArchive.DEBTS_FILE to payload.debtsCsv,
            ExportArchive.SHARED_COSTS_FILE to payload.sharedCostsCsv,
        )
        Result.Success(EncryptedZipArchive.pack(password, files))
    } catch (e: Exception) {
        Result.Error("Export failed")
    }

    override suspend fun importFrom(content: String, format: ExportFormat): Result<ImportSummary> = try {
        val parsed = when (format) {
            ExportFormat.CSV -> ImportParser.parseCsv(content)
            ExportFormat.JSON -> ImportParser.parseJson(content)
        }
        importRecords(parsed)
    } catch (e: Exception) {
        Result.Error("Import failed")
    }

    override suspend fun importEncryptedArchive(archiveBytes: ByteArray, password: String): Result<ImportSummary> = try {
        if (password.isBlank()) return Result.Error("Password required")
        val files = EncryptedZipArchive.unpack(password, archiveBytes)
        val manifestJson = files[ExportArchive.MANIFEST_FILE]
            ?: return Result.Error("Incorrect password or corrupted file")
        val manifest = ExportArchive.parseManifestJson(manifestJson)
            ?: return Result.Error("Incorrect password or corrupted file")
        val payload = ExportArchive.ArchivePayload(
            manifest = manifest,
            recordsCsv = files[ExportArchive.RECORDS_FILE].orEmpty(),
            eventsCsv = files[ExportArchive.EVENTS_FILE].orEmpty(),
            debtsCsv = files[ExportArchive.DEBTS_FILE].orEmpty(),
            sharedCostsCsv = files[ExportArchive.SHARED_COSTS_FILE].orEmpty(),
        )
        when (ExportArchive.verifyArchive(password, payload)) {
            ExportVerificationResult.SchemaTooNew ->
                return Result.Error("This file was exported from a newer version of Pro Expense. Please update the app to import it.")
            ExportVerificationResult.InvalidPasswordOrCorrupt ->
                return Result.Error("Incorrect password or corrupted file")
            ExportVerificationResult.Valid -> Unit
        }
        val records = parseRecordsCsv(payload.recordsCsv)
        val events = parseEventsCsv(payload.eventsCsv)
        val debts = parseDebtsCsv(payload.debtsCsv)
        val sharedCosts = parseSharedCostsCsv(payload.sharedCostsCsv)
        var imported = 0
        events.forEach { event ->
            val exists = store.allEvents().any { it.id == event.id }
            if (!exists) {
                store.insertEvent(event)
                imported += 1
            }
        }
        debts.forEach { debt ->
            val exists = store.allDebts().any { it.id == debt.id }
            if (!exists) {
                store.insertDebt(debt)
                imported += 1
            }
        }
        sharedCosts.forEach { cost ->
            val exists = store.allSharedCosts().any { it.id == cost.id }
            if (!exists) {
                store.insertSharedCost(cost)
                imported += 1
            }
        }
        val recordSummary = importRecords(records)
        when (recordSummary) {
            is Result.Success -> Result.Success(
                ImportSummary(
                    importedCount = imported + recordSummary.data.importedCount,
                    skippedCount = recordSummary.data.skippedCount,
                ),
            )
            is Result.Error -> recordSummary
        }
    } catch (e: Exception) {
        Result.Error("Incorrect password or corrupted file")
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

    private suspend fun importRecords(parsed: List<FinanceRecord>): Result<ImportSummary> {
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
        return Result.Success(ImportSummary(importedCount = imported, skippedCount = skipped))
    }

    private fun encodeRecordsJson(records: List<FinanceRecord>): String {
        val body = records.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]",
        ) { r ->
            """{"id":"${r.id}","cents":${r.homeCurrencyAmount.valueInCents},"category":"${r.categoryId}","recordedAt":${r.recordedAtEpochMillis}}"""
        }
        return body
    }

    private fun encodeRecordsCsvPlain(records: List<FinanceRecord>): String {
        val header = "id,amountCents,categoryId,note,recordedAt"
        val rows = records.map { r ->
            "${r.id},${r.homeCurrencyAmount.valueInCents},${r.categoryId},${r.note.orEmpty()},${r.recordedAtEpochMillis}"
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun parseRecordsCsv(csv: String): List<FinanceRecord> {
        if (csv.isBlank()) return emptyList()
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 10) return@mapNotNull null
            FinanceRecord(
                id = parts[0],
                amount = Amount(parts[1].toLongOrNull() ?: return@mapNotNull null),
                currency = CurrencyCode(parts[2]),
                homeCurrencyAmount = Amount(parts[3].toLongOrNull() ?: return@mapNotNull null),
                categoryId = parts[4],
                type = RecordType.valueOf(parts[5]),
                note = parts[6].ifBlank { null },
                recordedAtEpochMillis = parts[7].toLongOrNull() ?: return@mapNotNull null,
                tagType = parts[8].ifBlank { null }?.let { ExpenseTagType.valueOf(it) },
                tagId = parts[9].ifBlank { null },
            )
        }
    }

    private fun parseEventsCsv(csv: String): List<Event> {
        if (csv.isBlank()) return emptyList()
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 6) return@mapNotNull null
            Event(
                id = parts[0],
                name = parts[1],
                startEpochMillis = parts[2].toLongOrNull() ?: return@mapNotNull null,
                endEpochMillis = parts[3].toLongOrNull() ?: return@mapNotNull null,
                budgetAmount = Amount(parts[4].toLongOrNull() ?: return@mapNotNull null),
                status = EventStatus.valueOf(parts[5]),
            )
        }
    }

    private fun parseDebtsCsv(csv: String): List<Debt> {
        if (csv.isBlank()) return emptyList()
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 6) return@mapNotNull null
            Debt(
                id = parts[0],
                personName = parts[1],
                amount = Amount(parts[2].toLongOrNull() ?: return@mapNotNull null),
                direction = DebtDirection.valueOf(parts[3]),
                dueEpochMillis = parts[4].toLongOrNull() ?: return@mapNotNull null,
                isSettled = parts[5].toBooleanStrictOrNull() ?: false,
            )
        }
    }

    private fun parseSharedCostsCsv(csv: String): List<SharedCost> {
        if (csv.isBlank()) return emptyList()
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 7) return@mapNotNull null
            val participants = parts[5].split("|").mapNotNull { chunk ->
                val bits = chunk.split(";")
                if (bits.size < 2) return@mapNotNull null
                Participant(id = bits[0], name = bits[1])
            }
            val customShares = parts[6].ifBlank { null }?.split("|")?.mapNotNull { it.toLongOrNull() }
            SharedCost(
                id = parts[0],
                title = parts[1],
                totalAmount = Amount(parts[2].toLongOrNull() ?: return@mapNotNull null),
                currency = CurrencyCode(parts[3]),
                participants = participants,
                recordedAtEpochMillis = parts[4].toLongOrNull() ?: return@mapNotNull null,
                customShareCents = customShares,
            )
        }
    }
}
