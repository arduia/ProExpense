package com.arduia.expense.storage

/**
 * Owns the lifecycle of the raw SQLCipher passphrase.
 *
 * Phase 0 scope (per the data-layer design build sequence): first-launch random-key bootstrap, which
 * unblocks every repository. PIN-derived `PRAGMA rekey` rotation and biometric-gated key wrapping
 * (design §5.2) build on top of this once the storage layer is stable and are intentionally deferred
 * to a later phase.
 */
interface DatabaseKeyManager {
    /**
     * Returns the persistent database passphrase, generating a cryptographically random key and
     * persisting it (platform-securely) on first call. Subsequent calls return the same key.
     */
    fun getOrCreateDatabaseKey(): ByteArray
}
