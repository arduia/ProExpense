package com.arduia.expense.storage.repository

import android.content.SharedPreferences
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
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

/**
 * Backs onto a shared mutable map so two [FakeSharedPreferences] built from the same [backing]
 * simulate re-reading the same on-disk file across a fresh process — the scenario that matters
 * for the onboarding/display-name persistence regression tests below.
 */
private class FakeSharedPreferences(
    private val backing: MutableMap<String, Any?> = mutableMapOf(),
) : SharedPreferences by UnsupportedSharedPreferences {
    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        backing[key] as? Boolean ?: defValue

    override fun getString(key: String?, defValue: String?): String? =
        backing[key] as? String ?: defValue

    override fun edit(): SharedPreferences.Editor = FakeEditor(backing)

    private class FakeEditor(
        private val backing: MutableMap<String, Any?>,
    ) : SharedPreferences.Editor by UnsupportedEditor {
        private val pending = mutableMapOf<String, Any?>()

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            pending[key] = value
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            pending[key] = value
            return this
        }

        override fun commit(): Boolean {
            backing.putAll(pending)
            return true
        }

        override fun apply() {
            backing.putAll(pending)
        }
    }
}

private object UnsupportedSharedPreferences : SharedPreferences {
    override fun getAll(): MutableMap<String, *> = throw UnsupportedOperationException()
    override fun getString(key: String?, defValue: String?) = throw UnsupportedOperationException()
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) = throw UnsupportedOperationException()
    override fun getInt(key: String?, defValue: Int) = throw UnsupportedOperationException()
    override fun getLong(key: String?, defValue: Long) = throw UnsupportedOperationException()
    override fun getFloat(key: String?, defValue: Float) = throw UnsupportedOperationException()
    override fun getBoolean(key: String?, defValue: Boolean) = throw UnsupportedOperationException()
    override fun contains(key: String?) = throw UnsupportedOperationException()
    override fun edit(): SharedPreferences.Editor = throw UnsupportedOperationException()
    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = throw UnsupportedOperationException()
    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = throw UnsupportedOperationException()
}

private object UnsupportedEditor : SharedPreferences.Editor {
    override fun putString(key: String?, value: String?) = throw UnsupportedOperationException()
    override fun putStringSet(key: String?, values: MutableSet<String>?) = throw UnsupportedOperationException()
    override fun putInt(key: String?, value: Int) = throw UnsupportedOperationException()
    override fun putLong(key: String?, value: Long) = throw UnsupportedOperationException()
    override fun putFloat(key: String?, value: Float) = throw UnsupportedOperationException()
    override fun putBoolean(key: String?, value: Boolean) = throw UnsupportedOperationException()
    override fun remove(key: String?) = throw UnsupportedOperationException()
    override fun clear() = throw UnsupportedOperationException()
    override fun commit() = throw UnsupportedOperationException()
    override fun apply() = throw UnsupportedOperationException()
}

class AppMetaRepositoriesTest {

    private fun store() = AppMetaLocalStore(inMemoryDatabase().appMetaQueries, Dispatchers.Unconfined)

    @Test
    fun profile_onboardingComplete_survivesSimulatedProcessRestart() = runTest {
        // Same on-disk backing map, but a brand new AppMetaLocalStore each time — this mirrors a
        // cold app restart where Koin rebuilds the DI graph but SharedPreferences persists on disk.
        val diskBacking = mutableMapOf<String, Any?>()
        val firstLaunchPrefs = FakeSharedPreferences(diskBacking)
        val firstLaunchRepo = AppMetaProfileRepository(store(), firstLaunchPrefs)

        assertEquals(Result.Success(false), firstLaunchRepo.isOnboardingComplete())
        firstLaunchRepo.setOnboardingComplete()

        val secondLaunchPrefs = FakeSharedPreferences(diskBacking)
        val secondLaunchRepo = AppMetaProfileRepository(store(), secondLaunchPrefs)

        assertEquals(Result.Success(true), secondLaunchRepo.isOnboardingComplete())
    }

    @Test
    fun profile_onboardingComplete_fallsBackToDbWhenPrefsMissing() = runTest {
        // Simulates an existing install that wrote the flag to the DB before this fix shipped.
        val dbStore = store()
        AppMetaProfileRepository(dbStore, FakeSharedPreferences()).setOnboardingComplete()

        val freshPrefs = FakeSharedPreferences()
        val repoWithFreshPrefs = AppMetaProfileRepository(dbStore, freshPrefs)

        assertEquals(Result.Success(true), repoWithFreshPrefs.isOnboardingComplete())
    }

    @Test
    fun profile_displayName_survivesSimulatedProcessRestart() = runTest {
        val diskBacking = mutableMapOf<String, Any?>()
        val firstLaunchRepo = AppMetaProfileRepository(store(), FakeSharedPreferences(diskBacking))

        firstLaunchRepo.setDisplayName("Ada")

        val secondLaunchRepo = AppMetaProfileRepository(store(), FakeSharedPreferences(diskBacking))

        assertEquals(Result.Success("Ada"), secondLaunchRepo.getDisplayName())
    }

    @Test
    fun profile_displayName_fallsBackToDbWhenPrefsMissing() = runTest {
        // Simulates an existing install that wrote the name to the DB before this fix shipped.
        val dbStore = store()
        AppMetaProfileRepository(dbStore, FakeSharedPreferences()).setDisplayName("Ada")

        val repoWithFreshPrefs = AppMetaProfileRepository(dbStore, FakeSharedPreferences())

        assertEquals(Result.Success("Ada"), repoWithFreshPrefs.getDisplayName())
    }

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

    @Test
    fun currency_defaultsToUsd_thenPersistsHomeCurrency() = runTest {
        val store = store()
        val repo = AppMetaCurrencySettingsRepository(store)

        val initial = repo.getHomeCurrency()
        assertTrue(initial is Result.Success)
        assertEquals("USD", initial.data!!.code)

        repo.setHomeCurrency(CurrencyCode("EUR"))

        val fetched = repo.getHomeCurrency()
        assertTrue(fetched is Result.Success)
        assertEquals("EUR", fetched.data!!.code)
    }

    @Test
    fun currency_changeDoesNotResetBudgetOrLockout() = runTest {
        val store = store()
        AppMetaBudgetRepository(store).setMonthlyBudget(Money(Amount(100), CurrencyCode("USD")))
        AppMetaLockoutRepository(store).recordFailedAttempt(0, maxAttempts = 5, lockoutDurationMs = 1_000)

        AppMetaCurrencySettingsRepository(store).setHomeCurrency(CurrencyCode("GBP"))

        val budget = AppMetaBudgetRepository(store).getMonthlyBudget()
        assertTrue(budget is Result.Success)
        assertEquals(100, budget.data!!.amount.valueInCents)
        assertEquals(1, AppMetaLockoutRepository(store).getFailedAttemptCount())
    }

    @Test
    fun defaultCategory_nullByDefault_thenPersists() = runTest {
        val store = store()
        val repo = AppMetaDefaultCategoryRepository(store)

        val initial = repo.getDefaultCategoryId()
        assertTrue(initial is Result.Success)
        assertNull(initial.data)

        repo.setDefaultCategoryId("coffee")

        val fetched = repo.getDefaultCategoryId()
        assertTrue(fetched is Result.Success)
        assertEquals("coffee", fetched.data)
    }

    @Test
    fun defaultCategory_setNull_clearsIt() = runTest {
        val store = store()
        val repo = AppMetaDefaultCategoryRepository(store)
        repo.setDefaultCategoryId("coffee")

        repo.setDefaultCategoryId(null)

        val fetched = repo.getDefaultCategoryId()
        assertTrue(fetched is Result.Success)
        assertNull(fetched.data)
    }

    @Test
    fun defaultCategory_changeDoesNotResetBudgetOrCurrency() = runTest {
        val store = store()
        AppMetaBudgetRepository(store).setMonthlyBudget(Money(Amount(100), CurrencyCode("USD")))
        AppMetaCurrencySettingsRepository(store).setHomeCurrency(CurrencyCode("GBP"))

        AppMetaDefaultCategoryRepository(store).setDefaultCategoryId("pet")

        val budget = AppMetaBudgetRepository(store).getMonthlyBudget()
        assertTrue(budget is Result.Success)
        assertEquals(100, budget.data!!.amount.valueInCents)
        assertEquals("GBP", AppMetaCurrencySettingsRepository(store).getHomeCurrency().let { (it as Result.Success).data!!.code })
    }

    @Test
    fun theme_defaultsToDark_thenPersists() = runTest {
        val store = store()
        val repo = AppMetaThemeRepository(store)

        val initial = repo.getThemeMode()
        assertTrue(initial is Result.Success)
        assertEquals(ThemeMode.DARK, initial.data)

        repo.setThemeMode(ThemeMode.SYSTEM)

        val fetched = repo.getThemeMode()
        assertTrue(fetched is Result.Success)
        assertEquals(ThemeMode.SYSTEM, fetched.data)
    }
}
