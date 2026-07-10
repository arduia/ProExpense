package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.storage.db.Shared_cost

internal fun Shared_cost.toDomain(): SharedCost {
    val participants = parseParticipantsJson(participants_json)
    val splitStrategy = parseStrategyJson(custom_shares_json)
    return SharedCost(
        id = SharedCostId(id),
        title = title,
        total = Money(Amount(total_cents), CurrencyCode(currency_code)),
        participants = participants,
        splitStrategy = splitStrategy,
        recordedAtEpochMillis = recorded_at,
    )
}

internal fun parseParticipantsJson(json: String): List<Participant> {
    if (json.isEmpty() || json == "[]") return emptyList()

    val items = mutableListOf<Participant>()
    var i = 1
    while (i < json.length - 1) {
        val objectStart = json.indexOf('{', i)
        if (objectStart == -1) break

        val objectEnd = json.indexOf('}', objectStart) + 1
        val objectStr = json.substring(objectStart, objectEnd)

        val id = extractJsonString(objectStr, "id")
        val name = extractJsonString(objectStr, "name")

        if (id != null && name != null) {
            items.add(
                Participant(
                    id = ParticipantId(id),
                    name = name,
                ),
            )
        }

        i = objectEnd + 1
    }
    return items
}

internal fun parseStrategyJson(json: String?): SplitStrategy {
    if (json.isNullOrEmpty() || !json.contains("\"type\":\"custom\"")) {
        return SplitStrategy.EqualSplit
    }
    return SplitStrategy.CustomSplit(parseSharesJson(json))
}

private fun parseSharesJson(json: String): Map<ParticipantId, Money> {
    val sharesKeyIndex = json.indexOf("\"shares\"")
    val sharesObjectStart = if (sharesKeyIndex == -1) -1 else json.indexOf('{', sharesKeyIndex)
    if (sharesObjectStart == -1) {
        return emptyMap()
    }
    val sharesObjectEnd = findMatchingBrace(json, sharesObjectStart)
    return parseShareEntries(json.substring(sharesObjectStart + 1, sharesObjectEnd))
}

private fun parseShareEntries(sharesBody: String): Map<ParticipantId, Money> {
    val shares = mutableMapOf<ParticipantId, Money>()
    var i = 0
    while (i < sharesBody.length) {
        val entry = parseShareEntry(sharesBody, i) ?: break
        shares[entry.id] = entry.money
        i = entry.nextIndex
    }
    return shares
}

private class ShareEntry(
    val id: ParticipantId,
    val money: Money,
    val nextIndex: Int,
)

/**
 * One `"<id>":{"cents":N,"code":"..."}` entry starting at or after [from] — the value object is
 * brace-matched (not `indexOf('}')`), or a shares map with 2+ entries (every real split — min is
 * 2 people) truncates at the first entry's own closing brace instead of the outer map's.
 */
private fun parseShareEntry(
    sharesBody: String,
    from: Int,
): ShareEntry? {
    val keyStart = sharesBody.indexOf('"', from)
    val keyEnd = if (keyStart == -1) -1 else sharesBody.indexOf('"', keyStart + 1)
    val valueStart = if (keyEnd == -1) -1 else sharesBody.indexOf('{', keyEnd)
    if (valueStart == -1) {
        return null
    }
    val valueEnd = findMatchingBrace(sharesBody, valueStart)
    val valueJson = sharesBody.substring(valueStart, valueEnd + 1)
    val cents = extractJsonNumber(valueJson, "cents")
    val code = extractJsonString(valueJson, "code")
    return if (cents == null || code == null) {
        null
    } else {
        val participantId = unescapeJsonString(sharesBody.substring(keyStart + 1, keyEnd))
        ShareEntry(ParticipantId(participantId), Money(Amount(cents), CurrencyCode(code)), valueEnd + 1)
    }
}

/** Depth-aware scan for the `}` that closes the `{` at [openIndex] — a plain `indexOf('}')` stops
 *  at the first nested object's own closing brace instead of the outer object's. */
private fun findMatchingBrace(
    json: String,
    openIndex: Int,
): Int {
    var depth = 0
    for (idx in openIndex until json.length) {
        when (json[idx]) {
            '{' -> depth++
            '}' -> {
                depth--
                if (depth == 0) return idx
            }
        }
    }
    return json.length - 1
}

internal fun List<Participant>.toParticipantsJson(): String {
    if (isEmpty()) return "[]"
    return "[" +
        joinToString(",") { participant ->
            """{"id":"${participant.id.value}","name":"${escapeJsonString(participant.name)}"}"""
        } + "]"
}

internal fun SplitStrategy.toStrategyJson(): String? =
    when (this) {
        is SplitStrategy.EqualSplit -> null
        is SplitStrategy.CustomSplit -> {
            val sharesJson =
                shares.entries.joinToString(",") { (participantId, money) ->
                    """"${participantId.value}":{"cents":${money.amount.valueInCents},"code":"${money.currency.code}"}"""
                }
            """{"type":"custom","shares":{$sharesJson}}"""
        }
    }
