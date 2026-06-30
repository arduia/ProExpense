package com.arduia.expense.domain

import com.arduia.expense.shared.platformDigestHex

/**
 * Strategy for turning a canonical payload into a hex digest. New schemes (HMAC, signatures, a
 * server-side algorithm) plug in by implementing this and registering them with
 * [RecordIntegrityVerifier] — no change to call sites, mappers, or the schema.
 */
interface IntegrityDigester {
    val algorithm: String

    fun digest(payload: String): String
}

class Md5Digester : IntegrityDigester {
    override val algorithm: String = IntegrityAlgorithms.MD5
    override fun digest(payload: String): String = platformDigestHex(algorithm, payload)
}

class Sha256Digester : IntegrityDigester {
    override val algorithm: String = IntegrityAlgorithms.SHA_256
    override fun digest(payload: String): String = platformDigestHex(algorithm, payload)
}

/**
 * Stamps and verifies record checksums using a registry of [IntegrityDigester]s.
 *
 * - [stamp] uses the configured default algorithm for new/updated records.
 * - [verify] re-derives the digest using the digester that matches the *record's stored* algorithm,
 *   so older records keep verifying after the default is upgraded.
 *
 * Extensibility: add a digester to [digesters] to support a new algorithm for both writing and
 * verifying; the default can be switched independently.
 */
class RecordIntegrityVerifier(
    digesters: List<IntegrityDigester> = listOf(Sha256Digester(), Md5Digester()),
    private val defaultAlgorithm: String = IntegrityAlgorithms.SHA_256,
) {
    private val byAlgorithm: Map<String, IntegrityDigester> = digesters.associateBy { it.algorithm }

    init {
        require(byAlgorithm.containsKey(defaultAlgorithm)) {
            "No digester registered for default algorithm '$defaultAlgorithm'"
        }
    }

    fun checksumFor(record: FinanceRecord, algorithm: String = defaultAlgorithm): RecordChecksum {
        val digester = byAlgorithm[algorithm]
            ?: error("No digester registered for algorithm '$algorithm'")
        return RecordChecksum(algorithm, digester.digest(record.canonicalPayload()))
    }

    /** Returns the record with a freshly computed checksum (default algorithm). */
    fun stamp(record: FinanceRecord, algorithm: String = defaultAlgorithm): FinanceRecord =
        record.copy(integrity = checksumFor(record, algorithm))

    /**
     * Verifies a record against its own stored checksum. Returns `false` if the record has no
     * checksum, if its algorithm is unknown, or if the content no longer matches.
     */
    fun verify(record: FinanceRecord): Boolean {
        val checksum = record.integrity ?: return false
        val digester = byAlgorithm[checksum.algorithm] ?: return false
        return digester.digest(record.canonicalPayload()) == checksum.value
    }
}
