package com.arduia.expense.feature.importexport

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost

object ExportArchive {
    const val SCHEMA_VERSION = 1
    const val MANIFEST_FILE = "manifest.json"
    const val RECORDS_FILE = "records.csv"
    const val EVENTS_FILE = "events.csv"
    const val DEBTS_FILE = "debts.csv"
    const val SHARED_COSTS_FILE = "shared_costs.csv"
    const val FILE_EXTENSION = "peexport"

    data class Manifest(
        val schemaVersion: Int,
        val recordCount: Int,
        val eventCount: Int,
        val debtCount: Int,
        val sharedCostCount: Int,
        val signature: String,
    )

    data class ArchivePayload(
        val manifest: Manifest,
        val recordsCsv: String,
        val eventsCsv: String,
        val debtsCsv: String,
        val sharedCostsCsv: String,
    )

    fun buildArchive(
        password: String,
        records: List<FinanceRecord>,
        events: List<Event>,
        debts: List<Debt>,
        sharedCosts: List<SharedCost>,
    ): ArchivePayload {
        val recordsCsv = encodeRecordsCsv(records)
        val eventsCsv = encodeEventsCsv(events)
        val debtsCsv = encodeDebtsCsv(debts)
        val sharedCostsCsv = encodeSharedCostsCsv(sharedCosts)
        val unsignedPayload = buildUnsignedPayload(
            recordCount = records.size,
            eventCount = events.size,
            debtCount = debts.size,
            sharedCostCount = sharedCosts.size,
        )
        val signaturePayload = unsignedPayload + recordsCsv + eventsCsv + debtsCsv + sharedCostsCsv
        val signature = HmacSha256.sign(password, signaturePayload)
        val manifest = Manifest(
            schemaVersion = SCHEMA_VERSION,
            recordCount = records.size,
            eventCount = events.size,
            debtCount = debts.size,
            sharedCostCount = sharedCosts.size,
            signature = signature,
        )
        return ArchivePayload(
            manifest = manifest,
            recordsCsv = recordsCsv,
            eventsCsv = eventsCsv,
            debtsCsv = debtsCsv,
            sharedCostsCsv = sharedCostsCsv,
        )
    }

    fun verifyArchive(password: String, payload: ArchivePayload): ExportVerificationResult {
        if (payload.manifest.schemaVersion > SCHEMA_VERSION) {
            return ExportVerificationResult.SchemaTooNew
        }
        val unsignedPayload = buildUnsignedPayload(
            recordCount = payload.manifest.recordCount,
            eventCount = payload.manifest.eventCount,
            debtCount = payload.manifest.debtCount,
            sharedCostCount = payload.manifest.sharedCostCount,
        )
        val signaturePayload = unsignedPayload +
            payload.recordsCsv +
            payload.eventsCsv +
            payload.debtsCsv +
            payload.sharedCostsCsv
        if (!HmacSha256.verify(password, signaturePayload, payload.manifest.signature)) {
            return ExportVerificationResult.InvalidPasswordOrCorrupt
        }
        return ExportVerificationResult.Valid
    }

    fun manifestToJson(manifest: Manifest): String =
        """{"schemaVersion":${manifest.schemaVersion},"recordCount":${manifest.recordCount},"eventCount":${manifest.eventCount},"debtCount":${manifest.debtCount},"sharedCostCount":${manifest.sharedCostCount},"signature":"${manifest.signature}"}"""

    fun parseManifestJson(json: String): Manifest? {
        val schema = extractInt(json, "schemaVersion") ?: return null
        val records = extractInt(json, "recordCount") ?: return null
        val events = extractInt(json, "eventCount") ?: return null
        val debts = extractInt(json, "debtCount") ?: return null
        val shared = extractInt(json, "sharedCostCount") ?: return null
        val signature = extractString(json, "signature") ?: return null
        return Manifest(schema, records, events, debts, shared, signature)
    }

    private fun buildUnsignedPayload(
        recordCount: Int,
        eventCount: Int,
        debtCount: Int,
        sharedCostCount: Int,
    ): String = "schema=$SCHEMA_VERSION;records=$recordCount;events=$eventCount;debts=$debtCount;shared=$sharedCostCount"

    private fun encodeRecordsCsv(records: List<FinanceRecord>): String {
        val header = "id,amountCents,currencyCode,homeAmountCents,categoryId,type,note,recordedAt,tagType,tagId"
        val rows = records.map { r ->
            listOf(
                r.id,
                r.amount.valueInCents,
                r.currency.code,
                r.homeCurrencyAmount.valueInCents,
                r.categoryId,
                r.type.name,
                r.note.orEmpty(),
                r.recordedAtEpochMillis,
                r.tagType?.name.orEmpty(),
                r.tagId.orEmpty(),
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun encodeEventsCsv(events: List<Event>): String {
        val header = "id,name,startEpochMillis,endEpochMillis,budgetCents,status"
        val rows = events.map { e ->
            listOf(
                e.id,
                e.name,
                e.startEpochMillis,
                e.endEpochMillis,
                e.budgetAmount.valueInCents,
                e.status.name,
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun encodeDebtsCsv(debts: List<Debt>): String {
        val header = "id,personName,amountCents,direction,dueEpochMillis,isSettled"
        val rows = debts.map { d ->
            listOf(
                d.id,
                d.personName,
                d.amount.valueInCents,
                d.direction.name,
                d.dueEpochMillis,
                d.isSettled,
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun encodeSharedCostsCsv(costs: List<SharedCost>): String {
        val header = "id,title,totalCents,currencyCode,recordedAt,participantsJson,customSharesJson"
        val rows = costs.map { c ->
            listOf(
                c.id,
                c.title,
                c.totalAmount.valueInCents,
                c.currency.code,
                c.recordedAtEpochMillis,
                encodeParticipants(c.participants),
                c.customShareCents?.joinToString("|").orEmpty(),
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun encodeParticipants(participants: List<com.arduia.expense.domain.Participant>): String =
        participants.joinToString("|") { "${it.id};${it.name}" }

    private fun extractInt(json: String, key: String): Int? {
        val pattern = """"$key"\s*:\s*(\d+)""".toRegex()
        return pattern.find(json)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractString(json: String, key: String): String? {
        val pattern = """"$key"\s*:\s*"([^"]*)"""".toRegex()
        return pattern.find(json)?.groupValues?.getOrNull(1)
    }
}

enum class ExportVerificationResult {
    Valid,
    InvalidPasswordOrCorrupt,
    SchemaTooNew,
}
