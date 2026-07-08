package com.arduia.expense.storage.repository

import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.LockoutState

class AppMetaLockoutRepository(
    private val store: AppMetaLocalStore,
) : LockoutRepository {

    override suspend fun getFailedAttemptCount(): Int = store.read().failedAttemptCount.toInt()

    override suspend fun getLockoutUntilEpochMillis(): Long? = store.read().lockoutUntil

    override suspend fun recordFailedAttempt(
        nowEpochMillis: Long,
        maxAttempts: Int,
        lockoutDurationMs: Long,
    ): LockoutState {
        val updated = store.update { snapshot ->
            val newCount = snapshot.failedAttemptCount + 1
            val lockedOut = newCount >= maxAttempts
            snapshot.copy(
                failedAttemptCount = newCount,
                lockoutUntil = if (lockedOut) nowEpochMillis + lockoutDurationMs else snapshot.lockoutUntil,
            )
        }
        return lockoutStateAt(updated.failedAttemptCount, updated.lockoutUntil, nowEpochMillis)
    }

    override suspend fun resetLockout() {
        store.update { it.copy(failedAttemptCount = 0, lockoutUntil = null) }
    }

    override suspend fun isLockedOut(nowEpochMillis: Long): Boolean {
        val until = store.read().lockoutUntil ?: return false
        return until > nowEpochMillis
    }

    private fun lockoutStateAt(failedAttempts: Long, lockoutUntil: Long?, now: Long): LockoutState {
        val remainingMs = if (lockoutUntil != null && lockoutUntil > now) lockoutUntil - now else 0L
        val secondsRemaining = ((remainingMs + MILLIS_PER_SECOND - 1) / MILLIS_PER_SECOND).toInt()
        return LockoutState(
            isLockedOut = remainingMs > 0,
            secondsRemaining = secondsRemaining,
            failedAttempts = failedAttempts.toInt(),
        )
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1000L
    }
}
