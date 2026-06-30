package com.arduia.expense.storage

import com.arduia.expense.data.Result

/**
 * Manages the lifecycle of the HMAC-SHA256 signing key for data integrity verification.
 *
 * This is a storage-layer concern, separate from database-level encryption (SQLCipher).
 * The HMAC key flags any field change (accidental or deliberate), independent of file-level encryption.
 * The key is device-bound and cannot validate data that has left the device.
 */
interface IntegrityKeyManager {
    /**
     * Computes and returns the hex-encoded HMAC-SHA256 signature of the given canonical string.
     */
    suspend fun sign(canonical: String): Result<String>

    /**
     * Verifies that the given hex-encoded HMAC-SHA256 signature matches the computed signature
     * of the canonical string. Returns success if the signature is valid, error otherwise.
     */
    suspend fun verify(canonical: String, hash: String): Result<Unit>
}
