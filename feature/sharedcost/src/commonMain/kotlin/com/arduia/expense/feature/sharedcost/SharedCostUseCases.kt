package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SplitStrategy

enum class SplitMode { EQUAL, CUSTOM }

/** Raw, platform-agnostic form values for creating or editing a shared cost split. */
data class SaveSharedCostInput(
    val title: String,
    val rawTotal: String,
    val mode: SplitMode,
    val participantNames: List<String>,
    val customShareRaws: List<String>,
)

private fun buildSplitStrategy(
    input: SaveSharedCostInput,
    participants: List<Participant>,
    total: Money,
): SplitStrategy = when (input.mode) {
    SplitMode.EQUAL -> SplitStrategy.EqualSplit
    SplitMode.CUSTOM -> {
        val rawCents = participants.indices.map { index ->
            val raw = input.customShareRaws.getOrElse(index) { "0" }
            (Amount.parseOrNull(raw) ?: Amount(0L)).valueInCents
        }.toMutableList()
        val diff = total.amount.valueInCents - rawCents.sum()
        if (rawCents.isNotEmpty()) {
            rawCents[rawCents.lastIndex] = (rawCents.last() + diff).coerceAtLeast(0)
        }
        val shares = participants.mapIndexed { index, participant ->
            participant.id to Money(Amount(rawCents[index]), total.currency)
        }.toMap()
        SplitStrategy.CustomSplit(shares)
    }
}

private fun buildParticipants(names: List<String>, nowEpochMillis: Long): List<Participant> =
    names.mapIndexed { index, name ->
        Participant(ParticipantId(name.trim().lowercase() + "-" + index + "-" + nowEpochMillis), name)
    }

/** Validates and creates a new shared-cost split (design plan §SharedCostViewModel). */
class CreateSharedCostUseCase(
    private val sharedCostRepository: SharedCostRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(input: SaveSharedCostInput, currencyCode: String = "USD"): Boolean {
        val totalAmount = Amount.parseOrNull(input.rawTotal) ?: return false
        val total = Money(totalAmount, CurrencyCode(currencyCode))
        val now = nowEpochMillis()
        val participants = buildParticipants(input.participantNames, now)
        val splitStrategy = buildSplitStrategy(input, participants, total)
        sharedCostRepository.create(
            SharedCostInput(
                title = input.title,
                total = total,
                participants = participants,
                splitStrategy = splitStrategy,
                recordedAtEpochMillis = now,
            ),
        )
        return true
    }
}

/** Validates and applies edits to an existing shared-cost split. */
class UpdateSharedCostUseCase(
    private val sharedCostRepository: SharedCostRepository,
    private val nowEpochMillis: () -> Long,
) {
    suspend operator fun invoke(existing: SharedCost, input: SaveSharedCostInput, currencyCode: String = "USD"): Boolean {
        val totalAmount = Amount.parseOrNull(input.rawTotal) ?: return false
        val total = Money(totalAmount, CurrencyCode(currencyCode))
        val participants = buildParticipants(input.participantNames, nowEpochMillis())
        val splitStrategy = buildSplitStrategy(input, participants, total)
        sharedCostRepository.update(
            existing.copy(
                title = input.title,
                total = total,
                participants = participants,
                splitStrategy = splitStrategy,
            ),
        )
        return true
    }
}
