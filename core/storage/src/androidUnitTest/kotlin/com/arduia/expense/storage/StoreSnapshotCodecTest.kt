package com.arduia.expense.storage

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StoreSnapshotCodecTest {
    @Test
    fun encodeDecode_roundTripsSecurityKeys() {
        val key = ByteArray(32) { it.toByte() }
        val salt = ByteArray(16) { (it + 1).toByte() }
        val snapshot = StoreSnapshot(
            records = listOf(
                FinanceRecord(
                    id = "r1",
                    amount = Amount(100),
                    currency = CurrencyCode("USD"),
                    homeCurrencyAmount = Amount(100),
                    categoryId = "food",
                    type = RecordType.EXPENSE,
                    recordedAtEpochMillis = 1L,
                ),
            ),
            events = listOf(
                Event(
                    id = "e1",
                    name = "Trip",
                    startEpochMillis = 1L,
                    endEpochMillis = 2L,
                    budgetAmount = Amount(1000),
                    status = EventStatus.ACTIVE,
                ),
            ),
            debts = emptyList(),
            sharedCosts = emptyList(),
            monthlyBudgetCents = 5000L,
            homeCurrencyCode = "USD",
            failedAttemptCount = 0,
            lockoutUntilEpochMillis = null,
            securityAnswerNormalized = "answer",
            activeKey = key,
            pinSalt = salt,
            pinProtected = true,
        )

        val decoded = StoreSnapshotCodec.decode(StoreSnapshotCodec.encode(snapshot))
        assertContentEquals(key, decoded.activeKey)
        assertContentEquals(salt, decoded.pinSalt)
        assertEquals(true, decoded.pinProtected)
        assertEquals(1, decoded.records.size)
        assertEquals("r1", decoded.records[0].id)
    }
}
