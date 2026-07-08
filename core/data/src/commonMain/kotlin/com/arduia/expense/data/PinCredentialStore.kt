package com.arduia.expense.data

/**
 * The subset of the app's local security state that PIN/biometric/lockout logic needs. Kept as its
 * own contract (rather than `feature:auth` reaching into `core:storage`'s internal `AppMetaStore`)
 * so `feature:auth`'s business logic depends only on `core:data`, per the repository-pattern rule.
 */
data class PinCredentials(
    val pinHash: String?,
    val securityQuestionId: String?,
    val securityAnswerHash: String?,
    val biometricEnrolled: Boolean,
    val biometricWrappedKey: ByteArray?,
    val failedAttemptCount: Long,
    val lockoutUntil: Long?,
)

interface PinCredentialStore {
    suspend fun read(): PinCredentials

    /** Atomically read-modify-write; returns the persisted value. */
    suspend fun update(transform: (PinCredentials) -> PinCredentials): PinCredentials
}
