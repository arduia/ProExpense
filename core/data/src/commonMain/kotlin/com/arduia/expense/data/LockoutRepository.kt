package com.arduia.expense.data

interface LockoutRepository {
    suspend fun getFailedAttemptCount(): Int

    suspend fun getLockoutUntilEpochMillis(): Long?

    suspend fun recordFailedAttempt(nowEpochMillis: Long, maxAttempts: Int, lockoutDurationMs: Long): LockoutState

    suspend fun resetLockout()

    suspend fun isLockedOut(nowEpochMillis: Long): Boolean
}

data class LockoutState(
    val isLockedOut: Boolean,
    val secondsRemaining: Int,
    val failedAttempts: Int,
)
