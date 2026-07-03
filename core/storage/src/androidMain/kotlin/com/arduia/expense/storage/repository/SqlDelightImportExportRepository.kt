package com.arduia.expense.storage.repository

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ImportExportRepository
import com.arduia.expense.data.ImportSummary
import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.mapping.extractJsonNumber
import com.arduia.expense.storage.mapping.extractJsonString
import com.arduia.expense.storage.mapping.extractNestedNumber
import com.arduia.expense.storage.mapping.extractNestedString
import com.arduia.expense.storage.mapping.escapeJsonString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightImportExportRepository(
    private val financeRecordRepository: FinanceRecordRepository,
    private val eventRepository: EventRepository,
    private val debtRepository: DebtRepository,
    private val sharedCostRepository: SharedCostRepository,
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

    override suspend fun exportGrouped(): Result<Map<String, String>> = withContext(dispatcher) {
        catchingResult {
            val records = financeRecordRepository.getAll().let {
                when (it) {
                    is Result.Success -> it.data
                    is Result.Error -> throw Exception(it.message, it.cause)
                }
            }
            val events = eventRepository.getAll().let {
                when (it) {
                    is Result.Success -> it.data
                    is Result.Error -> throw Exception(it.message, it.cause)
                }
            }
            val debts = debtRepository.getAll().let {
                when (it) {
                    is Result.Success -> it.data
                    is Result.Error -> throw Exception(it.message, it.cause)
                }
            }
            val sharedCosts = sharedCostRepository.getAll().let {
                when (it) {
                    is Result.Success -> it.data
                    is Result.Error -> throw Exception(it.message, it.cause)
                }
            }
            mapOf(
                "expenses.csv" to toCsv(records),
                "events.csv" to eventsToCsv(events),
                "debts.csv" to debtsToCsv(debts),
                "shared_costs.csv" to sharedCostsToCsv(sharedCosts),
            )
        }
    }

    private fun eventsToCsv(events: List<Event>): String {
        val header = "id,name,start_at,end_at,budget_cents,currency_code,status"
        val rows = events.map { event ->
            listOf(
                event.id.value,
                escapeQuotes(event.name),
                event.startEpochMillis,
                event.endEpochMillis,
                event.budget.amount.valueInCents,
                event.budget.currency.code,
                event.status.name,
            ).joinToString(",") { "\"$it\"" }
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun debtsToCsv(debts: List<Debt>): String {
        val header = "id,person_name,amount_cents,currency_code,direction,due_at,settled"
        val rows = debts.map { debt ->
            listOf(
                debt.id.value,
                escapeQuotes(debt.personName),
                debt.money.amount.valueInCents,
                debt.money.currency.code,
                debt.direction.name,
                debt.dueEpochMillis ?: "",
                debt.isSettled,
            ).joinToString(",") { "\"$it\"" }
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun sharedCostsToCsv(sharedCosts: List<SharedCost>): String {
        val header = "id,title,total_cents,currency_code,participant_count,recorded_at"
        val rows = sharedCosts.map { sharedCost ->
            listOf(
                sharedCost.id.value,
                escapeQuotes(sharedCost.title),
                sharedCost.total.amount.valueInCents,
                sharedCost.total.currency.code,
                sharedCost.participants.size,
                sharedCost.recordedAtEpochMillis,
            ).joinToString(",") { "\"$it\"" }
        }
        return (listOf(header) + rows).joinToString("\n")
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
        val header = "id,money_cents,money_code,category_id,type,note,recorded_at,link_type,link_id,home_money_cents,home_money_code"
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
                record.homeCurrencyMoney.amount.valueInCents,
                record.homeCurrencyMoney.currency.code,
            ).joinToString(",") { "\"$it\"" }
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun toJson(records: List<FinanceRecord>): String {
        val jsonRecords = records.map { record ->
            val linkJson = buildLinkJson(record.link)
            val note = record.note?.let { escapeJsonString(it) } ?: ""

            """{"id":"${record.id.value}","money":{"cents":${record.money.amount.valueInCents},"code":"${record.money.currency.code}"},"homeMoney":{"cents":${record.homeCurrencyMoney.amount.valueInCents},"code":"${record.homeCurrencyMoney.currency.code}"},"categoryId":"${record.categoryId.value}","type":"${record.type.name}","note":"$note","recordedAtEpochMillis":${record.recordedAtEpochMillis},"link":$linkJson}"""
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
        // Every field is written quoted (see toCsv), so a note containing a literal newline is
        // valid CSV — but naively splitting the whole file on "\n" breaks that one quoted record
        // into multiple fragments. splitCsvRecords tracks quote state so only an *unquoted*
        // newline ends a record.
        val lines = splitCsvRecords(content)
        if (lines.size < 2) return emptyList()

        val records = mutableListOf<FinanceRecord>()
        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.isBlank()) continue

            val fields = parseCsvLine(line)
            if (fields.size < 9) continue

            try {
                val link = parseLink(fields[7], fields[8])
                val money = Money(Amount(fields[1].toLong()), CurrencyCode(fields[2]))
                // Older exports (9 columns) predate multi-currency — home money always equals money.
                val homeCurrencyMoney = if (fields.size >= 11 && fields[9].isNotEmpty() && fields[10].isNotEmpty()) {
                    Money(Amount(fields[9].toLong()), CurrencyCode(fields[10]))
                } else {
                    money
                }
                records.add(
                    FinanceRecord(
                        id = RecordId(fields[0]),
                        money = money,
                        homeCurrencyMoney = homeCurrencyMoney,
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
            val moneyCents = extractNestedNumber(json, "money", "cents") ?: return null
            val moneyCode = extractNestedString(json, "money", "code") ?: return null
            val categoryId = extractJsonString(json, "categoryId") ?: return null
            val type = extractJsonString(json, "type") ?: return null
            val note = extractJsonString(json, "note")
            val recordedAt = extractJsonNumber(json, "recordedAtEpochMillis") ?: return null

            // Older exports predate multi-currency and never wrote a homeMoney object — home money
            // then equals money, matching the CSV importer's same fallback.
            val homeMoneyCents = extractNestedNumber(json, "homeMoney", "cents") ?: moneyCents
            val homeMoneyCode = extractNestedString(json, "homeMoney", "code") ?: moneyCode

            val linkType = extractNestedString(json, "link", "type") ?: "NONE"
            val linkId = extractNestedString(json, "link", "id")
            val link = when (linkType) {
                "EVENT" -> RecordLink.ToEvent(EventId(linkId ?: ""))
                "DEBT" -> RecordLink.ToDebt(DebtId(linkId ?: ""))
                "SHARED_COST" -> RecordLink.ToSharedCost(SharedCostId(linkId ?: ""))
                else -> RecordLink.None
            }

            FinanceRecord(
                id = RecordId(id),
                money = Money(Amount(moneyCents), CurrencyCode(moneyCode)),
                homeCurrencyMoney = Money(Amount(homeMoneyCents), CurrencyCode(homeMoneyCode)),
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

    /**
     * Splits raw CSV content into per-record strings, treating a newline inside a quoted field
     * (e.g. a multi-line note) as literal content rather than a record boundary. Mirrors
     * [parseCsvLine]'s own quote-toggle logic so the two stay consistent.
     */
    private fun splitCsvRecords(content: String): List<String> {
        val records = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < content.length) {
            val c = content[i]
            when {
                inQuotes && c == '"' -> {
                    if (i + 1 < content.length && content[i + 1] == '"') {
                        current.append('"').append('"')
                        i++
                    } else {
                        inQuotes = false
                        current.append(c)
                    }
                }
                !inQuotes && c == '"' -> {
                    inQuotes = true
                    current.append(c)
                }
                !inQuotes && (c == '\n' || c == '\r') -> {
                    if (c == '\r' && i + 1 < content.length && content[i + 1] == '\n') i++
                    records.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) records.add(current.toString())
        return records
    }

    private fun parseCsvLine(line: String): List<String> {
        val fields = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                // A quote's meaning depends on whether we're already inside a quoted field: two
                // adjacent quotes right after an opening quote is an *empty* field (close
                // immediately), not an escaped literal quote — that only applies once we're
                // already inside content. Conflating the two used to corrupt every row containing
                // an empty field (e.g. no note, no tag — the common case), desyncing every column
                // after it.
                inQuotes && c == '"' -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = false
                    }
                }
                !inQuotes && c == '"' -> inQuotes = true
                !inQuotes && c == ',' -> {
                    fields.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(c)
            }
            i++
        }
        // Always emit the trailing field, even when empty — dropping it silently shifted every
        // subsequent row's column count whenever the last field (e.g. home_money_code) was blank.
        fields.add(current.toString().trim())
        return fields
    }

    private fun escapeQuotes(str: String): String {
        return str.replace("\"", "\"\"")
    }
}
