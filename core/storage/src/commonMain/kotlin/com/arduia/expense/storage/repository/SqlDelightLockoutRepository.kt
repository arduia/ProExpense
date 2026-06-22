package com.arduia.expense.storage.repository

import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.LockoutState
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * C8 lockout state backed by the single app_meta row. The C8 decision is that biometric is fully
 * blocked while locked out, so the only state this layer owns is the failure count and the
 * lockout deadline; the "biometric blocked" guard is enforced above, against [isLockedOut].
 */
internal class SqlDelightLockoutRepository(
    private val database: ProExpenseDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LockoutRepository {

    private val queries get() = database.appMetaQueries

    override suspend fun getFailedAttemptCount(): Int = withContext(dispatcher) {
        queries.ensureMeta()
        queries.selectMeta().executeAsOne().failed_attempt_count.toInt()
    }

    override suspend fun getLockoutUntilEpochMillis(): Long? = withContext(dispatcher) {
        queries.ensureMeta()
        queries.selectMeta().executeAsOne().lockout_until
    }

    override suspend fun recordFailedAttempt(
        nowEpochMillis: Long,
        maxAttempts: Int,
        lockoutDurationMs: Long,
    ): LockoutState = withContext(dispatcher) {
        queries.ensureMeta()
        val newCount = queries.selectMeta().executeAsOne().failed_attempt_count.toInt() + 1
        val reachedThreshold = newCount >= maxAttempts
        val lockoutUntil = if (reachedThreshold) nowEpochMillis + lockoutDurationMs else null
        queries.updateLockout(newCount.toLong(), lockoutUntil)
        LockoutState(
            isLockedOut = reachedThreshold,
            secondsRemaining = if (reachedThreshold) ceilToSeconds(lockoutDurationMs) else 0,
            failedAttempts = newCount,
        )
    }

    override suspend fun resetLockout() {
        withContext(dispatcher) {
            queries.ensureMeta()
            queries.updateLockout(0L, null)
        }
    }

    override suspend fun isLockedOut(nowEpochMillis: Long): Boolean = withContext(dispatcher) {
        queries.ensureMeta()
        val lockoutUntil = queries.selectMeta().executeAsOne().lockout_until
        lockoutUntil != null && nowEpochMillis < lockoutUntil
    }

    private fun ceilToSeconds(millis: Long): Int = ((millis + 999) / 1000).toInt()
}
