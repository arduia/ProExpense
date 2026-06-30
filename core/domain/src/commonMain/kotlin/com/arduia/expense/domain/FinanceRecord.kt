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
    /**
     * Tamper/corruption-detection checksum over the record's content. Stamped by the data layer on
     * write and re-derived on read; `null` for records that predate integrity stamping.
     */
    val integrity: RecordChecksum? = null,
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
