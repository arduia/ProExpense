package com.arduia.expense.feature.importexport

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType

object ImportParser {
    fun parseCsv(content: String): List<FinanceRecord> {
        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()
        val header = lines.first().split(",").map { it.trim() }
        val idIndex = header.indexOf("id")
        val centsIndex = header.indexOf("amountCents")
        val categoryIndex = header.indexOf("categoryId")
        val noteIndex = header.indexOf("note")
        val recordedIndex = header.indexOf("recordedAt")
        if (idIndex < 0 || centsIndex < 0 || categoryIndex < 0 || recordedIndex < 0) return emptyList()

        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",", limit = 5)
            if (parts.size < 4) return@mapNotNull null
            val cents = parts[centsIndex].trim().toLongOrNull() ?: return@mapNotNull null
            val recordedAt = parts[recordedIndex].trim().toLongOrNull() ?: return@mapNotNull null
            FinanceRecord(
                id = parts[idIndex].trim(),
                amount = Amount(cents),
                currency = CurrencyCode("USD"),
                homeCurrencyAmount = Amount(cents),
                categoryId = parts[categoryIndex].trim(),
                type = RecordType.EXPENSE,
                note = parts.getOrNull(noteIndex)?.trim()?.takeIf { it.isNotEmpty() },
                recordedAtEpochMillis = recordedAt,
            )
        }
    }

    fun parseJson(content: String): List<FinanceRecord> {
        val trimmed = content.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val body = trimmed.removePrefix("[").removeSuffix("]")
        if (body.isBlank()) return emptyList()
        return body.split("},").map { chunk ->
            val normalized = chunk.trim().removePrefix("{").removeSuffix("}").trim()
            val fields = normalized.split(",").associate { part ->
                val keyValue = part.split(":", limit = 2)
                val key = keyValue[0].trim().removeSurrounding("\"")
                val value = keyValue.getOrNull(1)?.trim()?.removeSurrounding("\"") ?: ""
                key to value
            }
            val id = fields["id"] ?: return@map null
            val cents = fields["cents"]?.toLongOrNull() ?: return@map null
            val category = fields["category"] ?: fields["categoryId"] ?: "food"
            FinanceRecord(
                id = id,
                amount = Amount(cents),
                currency = CurrencyCode("USD"),
                homeCurrencyAmount = Amount(cents),
                categoryId = category,
                type = RecordType.EXPENSE,
                note = null,
                recordedAtEpochMillis = fields["recordedAt"]?.toLongOrNull() ?: 0L,
            )
        }.filterNotNull()
    }
}
