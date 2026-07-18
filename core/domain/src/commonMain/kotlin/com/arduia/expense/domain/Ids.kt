package com.arduia.expense.domain

import kotlin.jvm.JvmInline

@JvmInline
value class RecordId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "RecordId must not be blank" }
    }
}

@JvmInline
value class CategoryId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "CategoryId must not be blank" }
    }
}

@JvmInline
value class ParticipantId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ParticipantId must not be blank" }
    }
}

@JvmInline
value class DebtId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "DebtId must not be blank" }
    }
}

@JvmInline
value class EventId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "EventId must not be blank" }
    }
}

@JvmInline
value class SharedCostId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SharedCostId must not be blank" }
    }
}

/**
 * Integer (not UUID) by design — wallets are a small, locally-numbered set unlike every other
 * entity's string ID, chosen up front for the future multi-wallet linkage column.
 */
@JvmInline
value class WalletId(
    val value: Int,
) {
    init {
        require(value > 0) { "WalletId must be positive" }
    }
}
