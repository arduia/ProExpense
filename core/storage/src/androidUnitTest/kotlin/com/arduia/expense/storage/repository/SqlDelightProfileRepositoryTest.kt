package com.arduia.expense.storage.repository

import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlDelightProfileRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightProfileRepository(database, UnconfinedTestDispatcher())

    // Rule: a fresh install has an empty name, a default USD currency, and onboarding incomplete.
    @Test
    fun freshProfile_hasDefaults() = runTest {
        val profile = repository.getProfile().getOrFail()
        assertEquals("", profile.name)
        assertEquals(CurrencyCode("USD"), profile.homeCurrency)
        assertFalse(profile.onboardingCompleted)
    }

    // Rule: saved name + currency read back; onboarding flag is untouched by saveProfile.
    @Test
    fun saveProfile_persistsNameAndCurrency() = runTest {
        repository.saveProfile("Maya", CurrencyCode("EUR")).getOrFail()

        val profile = repository.getProfile().getOrFail()
        assertEquals("Maya", profile.name)
        assertEquals(CurrencyCode("EUR"), profile.homeCurrency)
        assertFalse(profile.onboardingCompleted)
    }

    // Rule: completing onboarding flips the flag (Splash routing depends on this).
    @Test
    fun setOnboardingCompleted_flipsFlag() = runTest {
        repository.setOnboardingCompleted(true).getOrFail()

        assertTrue(repository.getProfile().getOrFail().onboardingCompleted)
    }
}
