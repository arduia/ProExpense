package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.storage.db.Finance_record
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FinanceRecordMapperTest {

    @Test
    fun toDomain_mapsAllColumns_andUsesRowCurrencyForBothMoneys() {
        val row = Finance_record(
            id = "rec-1",
            amount_cents = 12_345,
            currency_code = "EUR",
            home_amount_cents = 11_000,
            category_id = "food",
            type = "EXPENSE",
            note = "lunch",
            recorded_at = 1_700_000_000_000,
            tag_type = null,
            tag_id = null,
            integrity_algo = "SHA-256",
            integrity_hash = "abc123",
        )

        val record = row.toDomain()

        assertEquals("rec-1", record.id.value)
        assertEquals(12_345, record.money.amount.valueInCents)
        assertEquals("EUR", record.money.currency.code)
        // Schema stores a single currency column, so home money reuses it (design §4.3 limitation).
        assertEquals(11_000, record.homeCurrencyMoney.amount.valueInCents)
        assertEquals("EUR", record.homeCurrencyMoney.currency.code)
        assertEquals("food", record.categoryId.value)
        assertEquals(RecordType.EXPENSE, record.type)
        assertEquals("lunch", record.note)
        assertEquals(1_700_000_000_000, record.recordedAtEpochMillis)
        assertEquals(RecordLink.None, record.link)
        assertEquals("SHA-256", record.integrity!!.algorithm)
        assertEquals("abc123", record.integrity!!.value)
    }

    @Test
    fun toChecksum_nullColumns_yieldNullIntegrity() {
        assertNull(toChecksum(null, null))
        assertNull(toChecksum("SHA-256", null))
        assertNull(toChecksum(null, "abc"))
    }

    @Test
    fun recordLink_roundTripsThroughTagColumns() {
        val links = listOf(
            RecordLink.None,
            RecordLink.ToEvent(EventId("evt-1")),
            RecordLink.ToDebt(DebtId("debt-1")),
            RecordLink.ToSharedCost(SharedCostId("sc-1")),
        )

        links.forEach { link ->
            val restored = toRecordLink(link.tagType(), link.tagId())
            assertEquals(link, restored)
        }
    }

    @Test
    fun toRecordLink_none_hasNullTagColumns() {
        assertNull(RecordLink.None.tagType())
        assertNull(RecordLink.None.tagId())
    }

    @Test
    fun toRecordLink_unknownTagType_fails() {
        assertFailsWith<IllegalStateException> {
            toRecordLink("MYSTERY", "x")
        }
    }

    @Test
    fun toRecordLink_missingTagId_fails() {
        val error = assertFailsWith<IllegalArgumentException> {
            toRecordLink("EVENT", null)
        }
        assertTrue(error.message!!.contains("tag_id"))
    }
}
