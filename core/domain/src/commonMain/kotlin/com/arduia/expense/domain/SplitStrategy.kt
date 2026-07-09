package com.arduia.expense.domain

sealed interface SplitStrategy {
    data object EqualSplit : SplitStrategy

    data class CustomSplit(
        val shares: Map<ParticipantId, Money>,
    ) : SplitStrategy {
        init {
            require(shares.isNotEmpty()) { "CustomSplit must have at least one share" }
        }
    }

    companion object {
        fun resolve(
            strategy: SplitStrategy,
            participants: List<Participant>,
            total: Money,
        ): Map<ParticipantId, Money> =
            when (strategy) {
                is EqualSplit -> {
                    require(participants.isNotEmpty()) { "Cannot split equally with no participants" }
                    val each = total.amount.valueInCents / participants.size
                    val remainder = total.amount.valueInCents % participants.size
                    participants
                        .mapIndexed { index, participant ->
                            val cents = if (index < remainder) each + 1 else each
                            participant.id to Money(Amount(cents), total.currency)
                        }.toMap()
                }

                is CustomSplit -> {
                    // Custom shares need not sum to the total — the total remains the stored source
                    // of truth (US-SHC-2/US-SHC-4); editing one share never rebalances the others.
                    val participantIds = participants.map { it.id }.toSet()
                    require(strategy.shares.keys == participantIds) {
                        "CustomSplit participant keys must exactly match SharedCost participants"
                    }
                    require(strategy.shares.values.all { it.currency == total.currency }) {
                        "All shares must be in the SharedCost currency: ${total.currency}"
                    }
                    strategy.shares
                }
            }
    }
}
