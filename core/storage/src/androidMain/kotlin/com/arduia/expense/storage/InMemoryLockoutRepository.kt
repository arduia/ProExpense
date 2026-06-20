package com.arduia.expense.storage

import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.LockoutState
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryLockoutRepository(
    private val store: EntityDataStore,
) : LockoutRepository {
    private val mutex = Mutex()

    override suspend fun getFailedAttemptCount(): Int = mutex.withLock { store.failedAttemptCount }

    override suspend fun getLockoutUntilEpochMillis(): Long? = mutex.withLock { store.lockoutUntilEpochMillis }

    override suspend fun recordFailedAttempt(
        nowEpochMillis: Long,
        maxAttempts: Int,
        lockoutDurationMs: Long,
    ): LockoutState = mutex.withLock {
        if (isLockedOutLocked(nowEpochMillis)) {
            return lockoutStateLocked(nowEpochMillis)
        }
        store.failedAttemptCount += 1
        if (store.failedAttemptCount >= maxAttempts) {
            store.lockoutUntilEpochMillis = nowEpochMillis + lockoutDurationMs
        }
        store.persistSettings()
        lockoutStateLocked(nowEpochMillis)
    }

    override suspend fun resetLockout() = mutex.withLock {
        store.failedAttemptCount = 0
        store.lockoutUntilEpochMillis = null
        store.persistSettings()
    }

    override suspend fun isLockedOut(nowEpochMillis: Long): Boolean = mutex.withLock {
        isLockedOutLocked(nowEpochMillis)
    }

    private fun isLockedOutLocked(nowEpochMillis: Long): Boolean {
        val until = store.lockoutUntilEpochMillis
        return until != null && nowEpochMillis < until
    }

    private fun lockoutStateLocked(nowEpochMillis: Long): LockoutState {
        val until = store.lockoutUntilEpochMillis
        val locked = until != null && nowEpochMillis < until
        val seconds = if (locked) ((until!! - nowEpochMillis) / 1000).toInt().coerceAtLeast(1) else 0
        return LockoutState(
            isLockedOut = locked,
            secondsRemaining = seconds,
            failedAttempts = store.failedAttemptCount,
        )
    }
}
