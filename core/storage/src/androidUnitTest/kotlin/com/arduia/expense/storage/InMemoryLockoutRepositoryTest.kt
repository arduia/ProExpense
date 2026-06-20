/**
 * Domain rules (design plan C8 / PIN Entry):
 * - Failed PIN attempts increment until maxAttempts (5).
 * - At max attempts, lockoutUntil = now + lockoutDuration (30s).
 * - While locked out, further attempts do not increment counter.
 * - resetLockout clears attempts and lockout window.
 */
package com.arduia.expense.storage

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InMemoryLockoutRepositoryTest {
    private val store = InMemoryDataStore()
    private val repository = InMemoryLockoutRepository(store)

    @Test
    fun recordFailedAttempt_locksAfterMaxAttempts() = runTest {
        val now = 1_000L
        repeat(4) {
            val state = repository.recordFailedAttempt(now, maxAttempts = 5, lockoutDurationMs = 30_000L)
            assertFalse(state.isLockedOut)
        }
        val locked = repository.recordFailedAttempt(now, maxAttempts = 5, lockoutDurationMs = 30_000L)
        assertTrue(locked.isLockedOut)
        assertEquals(30, locked.secondsRemaining)
        assertTrue(repository.isLockedOut(now + 1_000L))
    }

    @Test
    fun recordFailedAttempt_whileLockedOut_doesNotIncrementAttempts() = runTest {
        val now = 5_000L
        repeat(5) {
            repository.recordFailedAttempt(now, maxAttempts = 5, lockoutDurationMs = 30_000L)
        }
        val before = repository.getFailedAttemptCount()
        repository.recordFailedAttempt(now + 1_000L, maxAttempts = 5, lockoutDurationMs = 30_000L)
        assertEquals(before, repository.getFailedAttemptCount())
    }

    @Test
    fun resetLockout_clearsAttemptsAndLockout() = runTest {
        val now = 1_000L
        repeat(5) {
            repository.recordFailedAttempt(now, maxAttempts = 5, lockoutDurationMs = 30_000L)
        }
        repository.resetLockout()
        assertEquals(0, repository.getFailedAttemptCount())
        assertFalse(repository.isLockedOut(now))
        assertEquals(null, repository.getLockoutUntilEpochMillis())
    }

    @Test
    fun persistSettings_calledAfterLockoutChanges() = runTest {
        repository.recordFailedAttempt(1_000L, maxAttempts = 5, lockoutDurationMs = 30_000L)
        assertEquals(1, store.failedAttemptCount)
    }
}
