package com.arduia.expense.storage.repository

import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ImportExportRepository
import com.arduia.expense.data.ImportSummary
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.storage.catchingResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightImportExportRepository(
    private val financeRecordRepository: FinanceRecordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ImportExportRepository {

    override suspend fun exportAll(format: ExportFormat): Result<String> = withContext(dispatcher) {
        val allResult = financeRecordRepository.getAll()
        catchingResult {
            val records = when (allResult) {
                is Result.Success -> allResult.data
                is Result.Error -> throw Exception(allResult.message, allResult.cause)
            }
            when (format) {
                ExportFormat.CSV -> toCsv(records)
                ExportFormat.JSON -> toJson(records)
            }
        }
    }

    override suspend fun importFrom(
        content: String,
        format: ExportFormat,
    ): Result<ImportSummary> = withContext(dispatcher) {
        catchingResult {
            val records = when (format) {
                ExportFormat.CSV -> parseCsv(content)
                ExportFormat.JSON -> parseJson(content)
            }

            var importedCount = 0
            var skippedCount = 0

            for (record in records) {
                val result = financeRecordRepository.upsert(record)
                if (result is Result.Success) {
                    importedCount++
                } else {
                    skippedCount++
                }
            }

            ImportSummary(importedCount, skippedCount)
        }
    }

    override suspend fun previewImport(
        content: String,
        format: ExportFormat,
    ): Result<List<FinanceRecord>> = withContext(dispatcher) {
        catchingResult {
            when (format) {
                ExportFormat.CSV -> parseCsv(content)
                ExportFormat.JSON -> parseJson(content)
            }
        }
    }

    private fun toCsv(records: List<FinanceRecord>): String {
        val header = "id,money_cents,money_code,category_id,type,note,recorded_at,link_type,link_id"
        val rows = records.map { record ->
            val (linkType, linkId) = extractLinkParts(record.link)

            listOf(
                record.id.value,
                record.money.amount.valueInCents,
                record.money.currency.code,
                record.categoryId.value,
                record.type.name,
                record.note?.let { escapeQuotes(it) } ?: "",
                record.recordedAtEpochMillis,
                linkType,
                linkId,
            ).joinToString(",") { "\"$it\"" }
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun toJson(records: List<FinanceRecord>): String {
        val jsonRecords = records.map { record ->
            val linkJson = buildLinkJson(record.link)
            val note = record.note?.let { escapeJsonString(it) } ?: ""

            """{"id":"${record.id.value}","money":{"cents":${record.money.amount.valueInCents},"code":"${record.money.currency.code}"},"categoryId":"${record.categoryId.value}","type":"${record.type.name}","note":"$note","recordedAtEpochMillis":${record.recordedAtEpochMillis},"link":$linkJson}"""
        }
        return "[" + jsonRecords.joinToString(",") + "]"
    }

    private fun extractLinkParts(link: RecordLink): Pair<String, String> {
        // Use reflection to avoid smart cast issues with public API properties
        return when {
            link is RecordLink.ToEvent -> "EVENT" to link.eventId.value
            link is RecordLink.ToDebt -> "DEBT" to link.debtId.value
            link is RecordLink.ToSharedCost -> "SHARED_COST" to link.sharedCostId.value
            else -> "" to ""
        }
    }

    private fun buildLinkJson(link: RecordLink): String {
        return when {
            link is RecordLink.ToEvent -> """{"type":"EVENT","id":"${link.eventId.value}"}"""
            link is RecordLink.ToDebt -> """{"type":"DEBT","id":"${link.debtId.value}"}"""
            link is RecordLink.ToSharedCost -> """{"type":"SHARED_COST","id":"${link.sharedCostId.value}"}"""
            else -> """{"type":"NONE"}"""
        }
    }

    private fun parseCsv(content: String): List<FinanceRecord> {
        val lines = content.split("\n")
        if (lines.size < 2) return emptyList()

        val records = mutableListOf<FinanceRecord>()
        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            val fields = parseCsvLine(line)
            if (fields.size < 9) continue

            try {
                val link = parseLink(fields[7], fields[8])
                records.add(
                    FinanceRecord(
                        id = RecordId(fields[0]),
                        money = Money(Amount(fields[1].toLong()), CurrencyCode(fields[2])),
                        homeCurrencyMoney = Money(Amount(fields[1].toLong()), CurrencyCode(fields[2])),
                        categoryId = CategoryId(fields[3]),
                        type = RecordType.valueOf(fields[4]),
                        note = if (fields[5].isEmpty()) null else fields[5],
                        recordedAtEpochMillis = fields[6].toLong(),
                        link = link,
                    )
                )
            } catch (e: Exception) {
                // Skip malformed records
            }
        }
        return records
    }

    private fun parseJson(content: String): List<FinanceRecord> {
        val records = mutableListOf<FinanceRecord>()

        var i = content.indexOf('[')
        if (i == -1) return emptyList()
        i++

        while (i < content.length) {
            val start = content.indexOf('{', i)
            if (start == -1) break

            var braceCount = 0
            var end = start
            while (end < content.length) {
                when (content[end]) {
                    '{' -> braceCount++
                    '}' -> {
                        braceCount--
                        if (braceCount == 0) break
                    }
                }
                end++
            }

            if (end >= content.length) break

            try {
                val jsonObj = content.substring(start, end + 1)
                val record = parseJsonRecord(jsonObj)
                if (record != null) {
                    records.add(record)
                }
            } catch (e: Exception) {
                // Skip malformed records
            }

            i = end + 1
        }

        return records
    }

    private fun parseJsonRecord(json: String): FinanceRecord? {
        return try {
            val id = extractJsonString(json, "id") ?: return null
            val moneyCents = extractJsonNumber(json, "money.*cents") ?: return null
            val moneyCode = extractJsonString(json, "money.*code") ?: return null
            val categoryId = extractJsonString(json, "categoryId") ?: return null
            val type = extractJsonString(json, "type") ?: return null
            val note = extractJsonString(json, "note")
            val recordedAt = extractJsonNumber(json, "recordedAtEpochMillis") ?: return null

            val linkType = extractJsonString(json, "link.*type") ?: "NONE"
            val linkId = extractJsonString(json, "link.*id")
            val link = when (linkType) {
                "EVENT" -> RecordLink.ToEvent(EventId(linkId ?: ""))
                "DEBT" -> RecordLink.ToDebt(DebtId(linkId ?: ""))
                "SHARED_COST" -> RecordLink.ToSharedCost(SharedCostId(linkId ?: ""))
                else -> RecordLink.None
            }

            FinanceRecord(
                id = RecordId(id),
                money = Money(Amount(moneyCents), CurrencyCode(moneyCode)),
                homeCurrencyMoney = Money(Amount(moneyCents), CurrencyCode(moneyCode)),
                categoryId = CategoryId(categoryId),
                type = RecordType.valueOf(type),
                note = if (note.isNullOrEmpty()) null else note,
                recordedAtEpochMillis = recordedAt,
                link = link,
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseLink(linkType: String, linkId: String): RecordLink {
        return when {
            linkType == "EVENT" && linkId.isNotEmpty() -> RecordLink.ToEvent(EventId(linkId))
            linkType == "DEBT" && linkId.isNotEmpty() -> RecordLink.ToDebt(DebtId(linkId))
            linkType == "SHARED_COST" && linkId.isNotEmpty() -> RecordLink.ToSharedCost(SharedCostId(linkId))
            else -> RecordLink.None
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val fields = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    inQuotes = !inQuotes
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    }
                }
                c == ',' && !inQuotes -> {
                    fields.add(current.toString().trim().trim('"'))
                    current = StringBuilder()
                }
                else -> current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) {
            fields.add(current.toString().trim().trim('"'))
        }
        return fields
    }

    private fun extractJsonString(json: String, key: String): String? {
        val pattern = if (key.contains(".*")) {
            key.replace(".*", "[^:]*")
        } else {
            "\"$key\""
        }
        val regex = Regex("$pattern\\s*:\\s*\"([^\"]*)\"")
        return regex.find(json)?.groupValues?.get(1)
    }

    private fun extractJsonNumber(json: String, key: String): Long? {
        val pattern = if (key.contains(".*")) {
            key.replace(".*", "[^:]*")
        } else {
            "\"$key\""
        }
        val regex = Regex("$pattern\\s*:\\s*(-?\\d+)")
        return regex.find(json)?.groupValues?.get(1)?.toLongOrNull()
    }

    private fun escapeQuotes(str: String): String {
        return str.replace("\"", "\"\"")
    }

    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}
