package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.WalletId
import com.arduia.expense.storage.db.Finance_record
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FinanceRecordMapperTest {
    @Test
    fun toDomain_foreignCurrencyRecord_usesHomeCurrencyColumnForHomeMoney() {
        val row =
            Finance_record(
                id = "rec-1",
                amount_cents = 12_345,
                currency_code = "EUR",
                home_amount_cents = 11_000,
                category_id = "food",
                type = 0L, // EXPENSE
                note = "lunch",
                recorded_at = 1_700_000_000_000,
                updated_at = 1_700_000_000_001,
                tag_type = null,
                tag_id = null,
                integrity_algo = "SHA-256",
                integrity_hash = "abc123",
                home_currency_code = "USD",
                wallet_id = null,
                dirty = 1,
                last_synced_at = null,
            )

        val record = row.toDomain()

        assertEquals("rec-1", record.id.value)
        assertEquals(12_345, record.money.amount.valueInCents)
        assertEquals("EUR", record.money.currency.code)
        assertEquals(11_000, record.homeCurrencyMoney.amount.valueInCents)
        assertEquals("USD", record.homeCurrencyMoney.currency.code)
        assertEquals("food", record.categoryId.value)
        assertEquals(RecordType.EXPENSE, record.type)
        assertEquals("lunch", record.note)
        assertEquals(1_700_000_000_000, record.recordedAtEpochMillis)
        assertEquals(RecordLink.None, record.link)
        assertEquals("SHA-256", record.integrity!!.algorithm)
        assertEquals("abc123", record.integrity!!.value)
    }

    @Test
    fun toDomain_sameCurrencyRecord_nullHomeColumnsFallBackToRowCurrency() {
        val row =
            Finance_record(
                id = "rec-2",
                amount_cents = 5_000,
                currency_code = "USD",
                home_amount_cents = null,
                category_id = "food",
                type = 0L,
                note = null,
                recorded_at = 1_700_000_000_000,
                updated_at = 1_700_000_000_001,
                tag_type = null,
                tag_id = null,
                integrity_algo = "SHA-256",
                integrity_hash = "abc123",
                home_currency_code = null,
                wallet_id = null,
                dirty = 1,
                last_synced_at = null,
            )

        val record = row.toDomain()

        assertEquals(5_000, record.homeCurrencyMoney.amount.valueInCents)
        assertEquals("USD", record.homeCurrencyMoney.currency.code)
    }

    @Test
    fun toDomain_walletIdColumn_mapsToWalletId() {
        val row =
            Finance_record(
                id = "rec-3",
                amount_cents = 5_000,
                currency_code = "USD",
                home_amount_cents = null,
                category_id = "food",
                type = 0L,
                note = null,
                recorded_at = 1_700_000_000_000,
                updated_at = 1_700_000_000_001,
                tag_type = null,
                tag_id = null,
                integrity_algo = "SHA-256",
                integrity_hash = "abc123",
                home_currency_code = null,
                wallet_id = 3L,
                dirty = 1,
                last_synced_at = null,
            )

        assertEquals(WalletId(3), row.toDomain().walletId)
    }

    @Test
    fun toChecksum_nullColumns_yieldNullIntegrity() {
        assertNull(toChecksum(null, null))
        assertNull(toChecksum("SHA-256", null))
        assertNull(toChecksum(null, "abc"))
    }

    @Test
    fun recordLink_roundTripsThroughTagColumns() {
        val links =
            listOf(
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
        val error =
            assertFailsWith<IllegalArgumentException> {
                toRecordLink("EVENT", null)
            }
        assertTrue(error.message!!.contains("tag_id"))
    }
}
