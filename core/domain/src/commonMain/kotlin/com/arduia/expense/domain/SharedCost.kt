package com.arduia.expense.domain

data class SharedCost(
    val id: SharedCostId,
    val title: String,
    val total: Money,
    val participants: List<Participant>,
    val splitStrategy: SplitStrategy,
    val recordedAtEpochMillis: Long,
    val isArchived: Boolean = false,
    // Off by default (US-SHC-4) — a split is reference data for the group, not automatically the
    // user's own spending; opting in links it to a FinanceRecord the same way Debt's
    // "record as transaction" toggle does.
    val recordAsTransaction: Boolean = false,
) {
    init {
        require(title.isNotBlank()) { "SharedCost title must not be blank" }
        require(participants.isNotEmpty()) { "SharedCost must have at least one participant" }
        require(participants.map { it.id }.toSet().size == participants.size) {
            "SharedCost participants must have unique ids"
        }
        SplitStrategy.resolve(splitStrategy, participants, total)
    }

    fun shares(): Map<ParticipantId, Money> = SplitStrategy.resolve(splitStrategy, participants, total)
}
