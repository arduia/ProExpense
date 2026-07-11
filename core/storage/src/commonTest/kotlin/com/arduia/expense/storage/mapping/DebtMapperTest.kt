package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.DebtDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.arduia.expense.storage.db.Debt_record as DebtRow

class DebtMapperTest {
    private fun row(
        recordedAt: Long = 1_000L,
        recordAsTransaction: Long = 0L,
    ) = DebtRow(
        id = "d1",
        person_name = "Alex",
        amount_cents = 5000,
        currency_code = "USD",
        direction = 0L,
        due_epoch_millis = null,
        is_settled = 0L,
        note = null,
        recorded_at = recordedAt,
        record_as_transaction = recordAsTransaction,
    )

    @Test
    fun toDomain_mapsRecordedAtAndRecordAsTransactionColumns() {
        val debt = row(recordedAt = 42_000L, recordAsTransaction = 1L).toDomain()

        assertEquals(42_000L, debt.recordedAtEpochMillis)
        assertTrue(debt.recordAsTransaction)
    }

    @Test
    fun toDomain_recordAsTransactionFalseWhenColumnIsZero() {
        val debt = row(recordAsTransaction = 0L).toDomain()

        assertFalse(debt.recordAsTransaction)
    }

    @Test
    fun debtDirectionCode_roundTrips() {
        assertEquals(DebtDirection.OWED_TO_ME, 0L.toDebtDirectionFromCode())
        assertEquals(DebtDirection.I_OWE, 1L.toDebtDirectionFromCode())
        assertEquals(0L, DebtDirection.OWED_TO_ME.toCode())
        assertEquals(1L, DebtDirection.I_OWE.toCode())
    }
}
