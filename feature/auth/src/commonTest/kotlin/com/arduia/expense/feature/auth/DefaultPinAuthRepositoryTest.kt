package com.arduia.expense.feature.auth

import com.arduia.expense.data.PinCredentialStore
import com.arduia.expense.data.PinCredentials
import com.arduia.expense.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakePinCredentialStore(
    private var credentials: PinCredentials = PinCredentials(
        pinHash = null,
        securityQuestionId = null,
        securityAnswerHash = null,
        biometricEnrolled = false,
        biometricWrappedKey = null,
        failedAttemptCount = 0,
        lockoutUntil = null,
    ),
) : PinCredentialStore {
    override suspend fun read(): PinCredentials = credentials

    override suspend fun update(transform: (PinCredentials) -> PinCredentials): PinCredentials {
        credentials = transform(credentials)
        return credentials
    }
}

class DefaultPinAuthRepositoryTest {

    private fun repository(store: FakePinCredentialStore = FakePinCredentialStore()) =
        DefaultPinAuthRepository(store, Dispatchers.Default)

    @Test
    fun setPin_thenVerify_roundTripsWithRealPbkdf2() = runTest {
        val repo = repository()

        repo.setPin("123456")

        assertEquals(Result.Success(true), repo.verifyPin("123456"))
        assertEquals(Result.Success(false), repo.verifyPin("000000"))
    }

    @Test
    fun setPin_persistsExpectedHashFormat() = runTest {
        val store = FakePinCredentialStore()
        repository(store).setPin("123456")

        val hash = store.read().pinHash
        checkNotNull(hash)
        // v2:<iterations>:<saltHex>:<hashHex> — format + iteration count must stay byte-identical,
        // an existing user's stored hash must keep verifying after this move to commonMain.
        assertTrue(hash.startsWith("v2:120000:"))
        assertEquals(4, hash.split(":").size)
    }

    @Test
    fun isPinConfigured_falseUntilSet() = runTest {
        val store = FakePinCredentialStore()
        val repo = repository(store)

        assertEquals(Result.Success(false), repo.isPinConfigured())
        repo.setPin("123456")
        assertEquals(Result.Success(true), repo.isPinConfigured())
    }

    @Test
    fun verifyPin_noPinConfigured_returnsFalseNotError() = runTest {
        val repo = repository()
        assertEquals(Result.Success(false), repo.verifyPin("123456"))
    }

    @Test
    fun clearPin_clearsPinAndBiometricAndLockoutState() = runTest {
        val store = FakePinCredentialStore()
        val repo = repository(store)
        repo.setPin("123456")
        repo.enrollBiometric()
        repo.incrementFailedAttempts()

        repo.clearPin()

        val credentials = store.read()
        assertNull(credentials.pinHash)
        assertFalse(credentials.biometricEnrolled)
        assertNull(credentials.biometricWrappedKey)
        assertEquals(0, credentials.failedAttemptCount)
        assertNull(credentials.lockoutUntil)
    }

    @Test
    fun securityQuestion_setThenVerify_roundTrips() = runTest {
        val repo = repository()
        repo.setSecurityQuestion("pet_name", "Rex")

        assertEquals(Result.Success("pet_name"), repo.getSecurityQuestionId())
        assertEquals(Result.Success(true), repo.verifySecurityAnswer("Rex"))
        assertEquals(Result.Success(false), repo.verifySecurityAnswer("Wrong"))
    }

    @Test
    fun incrementFailedAttempts_locksOutAtFifthAttempt_thenEscalates() = runTest {
        val repo = repository()

        repeat(4) { repo.incrementFailedAttempts() }
        assertEquals(Result.Success(null), repo.getLockoutUntilMs())

        repo.incrementFailedAttempts() // 5th
        val fifth = (repo.getLockoutUntilMs() as Result.Success).data
        checkNotNull(fifth)

        repo.incrementFailedAttempts() // 6th — a longer lockout window than the 5th
        val sixth = (repo.getLockoutUntilMs() as Result.Success).data
        checkNotNull(sixth)
        assertTrue(sixth > fifth)

        repo.incrementFailedAttempts() // 7th — longer still
        val seventh = (repo.getLockoutUntilMs() as Result.Success).data
        checkNotNull(seventh)
        assertTrue(seventh > sixth)
    }

    @Test
    fun resetFailedAttempts_clearsCountAndLockout() = runTest {
        val repo = repository()
        repeat(5) { repo.incrementFailedAttempts() }

        repo.resetFailedAttempts()

        assertEquals(Result.Success(0L), repo.getFailedAttemptCount())
        assertEquals(Result.Success(null), repo.getLockoutUntilMs())
    }

    @Test
    fun verifyPin_success_resetsFailedAttempts() = runTest {
        val repo = repository()
        repo.setPin("123456")
        repo.incrementFailedAttempts()
        repo.incrementFailedAttempts()

        VerifyPinUseCase(repo)("123456")

        assertEquals(Result.Success(0L), repo.getFailedAttemptCount())
    }

    @Test
    fun biometric_enrollThenClear() = runTest {
        val repo = repository()
        assertEquals(Result.Success(false), repo.isBiometricEnrolled())

        repo.enrollBiometric()
        assertEquals(Result.Success(true), repo.isBiometricEnrolled())

        repo.clearBiometric()
        assertEquals(Result.Success(false), repo.isBiometricEnrolled())
    }
}
