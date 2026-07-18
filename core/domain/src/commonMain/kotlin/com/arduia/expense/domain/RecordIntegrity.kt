package com.arduia.expense.domain

/**
 * Content checksum for a [FinanceRecord]. The [algorithm] is stored alongside the [value] so records
 * remain verifiable across algorithm upgrades (e.g. MD5 → SHA-256) and so a future sync service can
 * pick the matching verifier.
 */
data class RecordChecksum(
    val algorithm: String,
    val value: String,
) {
    init {
        require(algorithm.isNotBlank()) { "Checksum algorithm must not be blank" }
        require(value.isNotBlank()) { "Checksum value must not be blank" }
    }
}

/** Well-known integrity algorithm identifiers (match the platform digest provider names). */
object IntegrityAlgorithms {
    const val MD5 = "MD5"
    const val SHA_256 = "SHA-256"
}

/**
 * Deterministic, versioned serialization of a record's content used as the integrity digest input.
 * The leading version prefix (currently `v2`, bumped when a field is added/removed) lets the
 * canonical form evolve without silently invalidating older checksums; the free-text note is
 * length-prefixed so it cannot collide with the field delimiter.
 *
 * Stable enough to double as the payload a future sync/backup service hashes or signs.
 */
fun FinanceRecord.canonicalPayload(): String {
    val sep = '\u001F'
    return buildString {
        append("v2")
        append(sep)
        append(id.value)
        append(sep)
        append(money.amount.valueInCents)
        append(sep)
        append(money.currency.code)
        append(sep)
        append(homeCurrencyMoney.amount.valueInCents)
        append(sep)
        append(homeCurrencyMoney.currency.code)
        append(sep)
        append(categoryId.value)
        append(sep)
        append(type.name)
        append(sep)
        append(recordedAtEpochMillis)
        append(sep)
        append(link.canonical())
        append(sep)
        append(note?.length ?: -1)
        append(sep)
        append(note ?: "")
        append(sep)
        append(walletId?.value ?: -1)
    }
}
