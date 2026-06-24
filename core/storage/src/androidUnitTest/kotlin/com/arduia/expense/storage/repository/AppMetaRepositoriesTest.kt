package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppMetaRepositoriesTest {

    private fun store() = AppMetaLocalStore(inMemoryDatabase().appMetaQueries, Dispatchers.Unconfined)

    @Test
    fun budget_setThenGet_roundTrips() = runTest {
        val store = store()
        val repo = AppMetaBudgetRepository(store)

        val initial = repo.getMonthlyBudget()
        assertTrue(initial is Result.Success)
        assertNull(initial.data)

        repo.setMonthlyBudget(Money(Amount(150_00), CurrencyCode("USD")))

        val fetched = repo.getMonthlyBudget()
        assertTrue(fetched is Result.Success)
        assertEquals(150_00, fetched.data!!.amount.valueInCents)
    }

    @Test
    fun budget_setNull_clearsBudget() = runTest {
        val store = store()
        val repo = AppMetaBudgetRepository(store)
        repo.setMonthlyBudget(Money(Amount(100), CurrencyCode("USD")))

        repo.setMonthlyBudget(null)

        val fetched = repo.getMonthlyBudget()
        assertTrue(fetched is Result.Success)
        assertNull(fetched.data)
    }

    @Test
    fun lockout_locksOnceMaxAttemptsReached() = runTest {
        val store = store()
        val repo = AppMetaLockoutRepository(store)
        val now = 10_000L
        val durationMs = 30_000L

        val first = repo.recordFailedAttempt(now, maxAttempts = 3, lockoutDurationMs = durationMs)
        assertFalse(first.isLockedOut)
        assertEquals(1, first.failedAttempts)

        repo.recordFailedAttempt(now, maxAttempts = 3, lockoutDurationMs = durationMs)
        val third = repo.recordFailedAttempt(now, maxAttempts = 3, lockoutDurationMs = durationMs)

        assertTrue(third.isLockedOut)
        assertEquals(3, third.failedAttempts)
        assertEquals(30, third.secondsRemaining)
        assertTrue(repo.isLockedOut(now))
        assertEquals(now + durationMs, repo.getLockoutUntilEpochMillis())
    }

    @Test
    fun lockout_resetClearsCountAndExpiry() = runTest {
        val store = store()
        val repo = AppMetaLockoutRepository(store)
        repo.recordFailedAttempt(0, maxAttempts = 1, lockoutDurationMs = 5_000)

        repo.resetLockout()

        assertEquals(0, repo.getFailedAttemptCount())
        assertNull(repo.getLockoutUntilEpochMillis())
        assertFalse(repo.isLockedOut(0))
    }

    @Test
    fun lockout_notLockedAfterExpiryWindow() = runTest {
        val store = store()
        val repo = AppMetaLockoutRepository(store)
        repo.recordFailedAttempt(0, maxAttempts = 1, lockoutDurationMs = 5_000)

        assertFalse(repo.isLockedOut(nowEpochMillis = 6_000))
    }

    @Test
    fun securityState_noPinByDefault() = runTest {
        val reader = AppMetaSecurityStateReader(store())
        assertFalse(reader.hasPinConfigured())
    }
}
