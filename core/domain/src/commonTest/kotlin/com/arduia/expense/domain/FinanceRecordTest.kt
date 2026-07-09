package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith

class FinanceRecordTest {
    private val usd = CurrencyCode("USD")

    private fun record(note: String?) =
        FinanceRecord(
            id = RecordId("r1"),
            money = Money(Amount(100), usd),
            homeCurrencyMoney = Money(Amount(100), usd),
            categoryId = CategoryId("c1"),
            type = RecordType.EXPENSE,
            note = note,
            recordedAtEpochMillis = 0,
        )

    @Test
    fun `constructs with no link by default`() {
        val record = record(note = null)
        require(record.link == RecordLink.None)
    }

    @Test
    fun `accepts note at max length boundary`() {
        record(note = "a".repeat(FinanceRecord.MAX_NOTE_LENGTH))
    }

    @Test
    fun `rejects note exceeding max length`() {
        assertFailsWith<IllegalArgumentException> {
            record(note = "a".repeat(FinanceRecord.MAX_NOTE_LENGTH + 1))
        }
    }
}
