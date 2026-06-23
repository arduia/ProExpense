package com.arduia.expense.domain

data class FinanceRecord(
    val id: RecordId,
    val money: Money,
    val homeCurrencyMoney: Money,
    val categoryId: CategoryId,
    val type: RecordType,
    val note: String?,
    val recordedAtEpochMillis: Long,
    val link: RecordLink = RecordLink.None,
) {
    init {
        note?.let {
            require(it.length <= MAX_NOTE_LENGTH) { "Note must be at most $MAX_NOTE_LENGTH characters" }
        }
    }

    companion object {
        const val MAX_NOTE_LENGTH = 500
    }
}
