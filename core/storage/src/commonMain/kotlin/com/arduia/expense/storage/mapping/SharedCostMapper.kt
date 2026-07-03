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
                )
            )
        }

        i = objectEnd + 1
    }
    return items
}

internal fun parseStrategyJson(json: String?): SplitStrategy {
    if (json == null || json.isEmpty()) {
        return SplitStrategy.EqualSplit
    }

    return if (json.contains("\"type\":\"custom\"")) {
        val sharesMatch = Regex("\"shares\":\\{([^}]*)\\}").find(json)
        val sharesJson = sharesMatch?.groupValues?.get(1) ?: ""

        val shares = mutableMapOf<ParticipantId, Money>()
        val pairPattern = Regex("\"([^\"]+)\":\\{\"cents\":(\\d+),\"code\":\"([^\"]+)\"\\}")

        pairPattern.findAll(sharesJson).forEach { match ->
            val participantId = match.groupValues[1]
            val cents = match.groupValues[2].toLong()
            val code = match.groupValues[3]

            shares[ParticipantId(participantId)] = Money(
                Amount(cents),
                CurrencyCode(code)
            )
        }

        SplitStrategy.CustomSplit(shares)
    } else {
        SplitStrategy.EqualSplit
    }
}

internal fun List<Participant>.toParticipantsJson(): String {
    if (isEmpty()) return "[]"
    return "[" + joinToString(",") { participant ->
        """{"id":"${participant.id.value}","name":"${escapeJsonString(participant.name)}"}"""
    } + "]"
}

internal fun SplitStrategy.toStrategyJson(): String? = when (this) {
    is SplitStrategy.EqualSplit -> null
    is SplitStrategy.CustomSplit -> {
        val sharesJson = shares.entries.joinToString(",") { (participantId, money) ->
            """"${participantId.value}":{"cents":${money.amount.valueInCents},"code":"${money.currency.code}"}"""
        }
        """{"type":"custom","shares":{$sharesJson}}"""
    }
}
