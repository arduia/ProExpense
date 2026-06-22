package com.arduia.expense.storage.repository

import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightLockoutRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightLockoutRepository(database, UnconfinedTestDispatcher())

    private val maxAttempts = 5
    private val lockoutMs = 30_000L

    // Rule: a fresh install has no failures and is not locked.
    @Test
    fun initialState_isUnlocked() = runTest {
        assertEquals(0, repository.getFailedAttemptCount())
        assertNull(repository.getLockoutUntilEpochMillis())
        assertFalse(repository.isLockedOut(nowEpochMillis = 0))
    }

    // Rule: a failed attempt below the threshold increments the count without locking.
    @Test
    fun recordFailedAttempt_belowThreshold_doesNotLock() = runTest {
        val state = repository.recordFailedAttempt(nowEpochMillis = 1_000, maxAttempts, lockoutMs)

        assertFalse(state.isLockedOut)
        assertEquals(1, state.failedAttempts)
        assertEquals(0, state.secondsRemaining)
        assertNull(repository.getLockoutUntilEpochMillis())
    }

    // Rule: hitting the threshold locks out and reports the remaining seconds.
    @Test
    fun recordFailedAttempt_atThreshold_locksOut() = runTest {
        lateinit var last: com.arduia.expense.data.LockoutState
        repeat(maxAttempts) {
            last = repository.recordFailedAttempt(nowEpochMillis = 1_000, maxAttempts, lockoutMs)
        }

        assertTrue(last.isLockedOut)
        assertEquals(maxAttempts, last.failedAttempts)
        assertEquals(30, last.secondsRemaining)
        assertEquals(31_000L, repository.getLockoutUntilEpochMillis())
    }

    // Rule: isLockedOut is true within the window and false once now passes the deadline.
    @Test
    fun isLockedOut_respectsDeadline() = runTest {
        repeat(maxAttempts) {
            repository.recordFailedAttempt(nowEpochMillis = 1_000, maxAttempts, lockoutMs)
        }

        assertTrue(repository.isLockedOut(nowEpochMillis = 20_000))   // before 31_000 deadline
        assertFalse(repository.isLockedOut(nowEpochMillis = 31_000))  // at deadline
        assertFalse(repository.isLockedOut(nowEpochMillis = 40_000))  // after deadline
    }

    // Rule: reset clears both the count and the lock.
    @Test
    fun resetLockout_clearsState() = runTest {
        repeat(maxAttempts) {
            repository.recordFailedAttempt(nowEpochMillis = 1_000, maxAttempts, lockoutMs)
        }

        repository.resetLockout()

        assertEquals(0, repository.getFailedAttemptCount())
        assertNull(repository.getLockoutUntilEpochMillis())
        assertFalse(repository.isLockedOut(nowEpochMillis = 1_000))
    }
}
