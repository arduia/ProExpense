package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RecordIntegrityTest {
    private fun record(
        id: String = "rec-1",
        amountCents: Long = 5_000,
        note: String? = "lunch",
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(amountCents), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(amountCents), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        type = RecordType.EXPENSE,
        note = note,
        recordedAtEpochMillis = 1_000,
    )

    @Test
    fun sha256Digester_matchesKnownVector() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            Sha256Digester().digest("abc"),
        )
    }

    @Test
    fun md5Digester_matchesKnownVector() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", Md5Digester().digest("abc"))
    }

    @Test
    fun canonicalPayload_changesWhenContentChanges() {
        assertNotEquals(record(amountCents = 5_000).canonicalPayload(), record(amountCents = 5_001).canonicalPayload())
        assertEquals(record().canonicalPayload(), record().canonicalPayload())
    }

    @Test
    fun canonicalPayload_changesWhenWalletIdChanges() {
        val withWallet = record().copy(walletId = WalletId(1))
        assertNotEquals(record().canonicalPayload(), withWallet.canonicalPayload())
        assertNotEquals(withWallet.canonicalPayload(), record().copy(walletId = WalletId(2)).canonicalPayload())
    }

    @Test
    fun stampThenVerify_isIntact_andDefaultsToSha256() {
        val verifier = RecordIntegrityVerifier()
        val stamped = verifier.stamp(record())

        assertEquals(IntegrityAlgorithms.SHA_256, stamped.integrity!!.algorithm)
        assertTrue(verifier.verify(stamped))
    }

    @Test
    fun verify_failsWhenContentMutatedAfterStamping() {
        val verifier = RecordIntegrityVerifier()
        val stamped = verifier.stamp(record(amountCents = 5_000))

        val tampered = stamped.copy(money = Money(Amount(9_999), CurrencyCode("USD")))

        assertFalse(verifier.verify(tampered))
    }

    @Test
    fun verify_falseWhenNoChecksumOrUnknownAlgorithm() {
        val verifier = RecordIntegrityVerifier()
        assertFalse(verifier.verify(record().copy(integrity = null)))
        assertFalse(verifier.verify(record().copy(integrity = RecordChecksum("CRC32", "deadbeef"))))
    }

    @Test
    fun olderAlgorithmStillVerifies_afterDefaultUpgraded() {
        // Record stamped with MD5; verifier defaults to SHA-256 but still has the MD5 digester.
        val verifier = RecordIntegrityVerifier(defaultAlgorithm = IntegrityAlgorithms.SHA_256)
        val md5Stamped = verifier.stamp(record(), algorithm = IntegrityAlgorithms.MD5)

        assertEquals(IntegrityAlgorithms.MD5, md5Stamped.integrity!!.algorithm)
        assertTrue(verifier.verify(md5Stamped))
    }
}
