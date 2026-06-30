package com.arduia.expense.feature.onboarding

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private class FakeProfileRepository(
    var displayName: String = "",
    var onboardingComplete: Boolean = false,
    var setDisplayNameError: String? = null,
    var setOnboardingCompleteError: String? = null,
) : ProfileRepository {
    override suspend fun setDisplayName(name: String): Result<Unit> {
        setDisplayNameError?.let { return Result.Error(it) }
        displayName = name
        return Result.Success(Unit)
    }

    override suspend fun getDisplayName(): Result<String> = Result.Success(displayName)

    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(onboardingComplete)

    override suspend fun setOnboardingComplete(): Result<Unit> {
        setOnboardingCompleteError?.let { return Result.Error(it) }
        onboardingComplete = true
        return Result.Success(Unit)
    }
}

private class FakeCurrencySettingsRepository(
    var current: CurrencyCode? = null,
    var setHomeCurrencyError: String? = null,
) : CurrencySettingsRepository {
    override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(current)

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
        setHomeCurrencyError?.let { return Result.Error(it) }
        current = currency
        return Result.Success(Unit)
    }
}

class GetOnboardingStatusUseCaseTest {

    @Test
    fun invoke_returnsCompleteStatusAndDisplayName() = runTest {
        val repo = FakeProfileRepository(displayName = "Ada", onboardingComplete = true)
        val useCase = GetOnboardingStatusUseCase(repo)

        val status = useCase()

        assertEquals(OnboardingStatus(isComplete = true, displayName = "Ada"), status)
    }

    @Test
    fun invoke_defaultsToIncompleteOnError() = runTest {
        val repo = object : ProfileRepository by FakeProfileRepository() {
            override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Error("db error")
        }
        val useCase = GetOnboardingStatusUseCase(repo)

        val status = useCase()

        assertEquals(false, status.isComplete)
    }

    @Test
    fun invoke_defaultsToEmptyNameOnError() = runTest {
        val repo = object : ProfileRepository by FakeProfileRepository() {
            override suspend fun getDisplayName(): Result<String> = Result.Error("db error")
        }
        val useCase = GetOnboardingStatusUseCase(repo)

        val status = useCase()

        assertEquals("", status.displayName)
    }
}

class CompleteOnboardingUseCaseTest {

    @Test
    fun invoke_setsNameMarksCompleteAndSetsCurrency() = runTest {
        val profileRepo = FakeProfileRepository()
        val currencyRepo = FakeCurrencySettingsRepository()
        val useCase = CompleteOnboardingUseCase(profileRepo, currencyRepo)

        val result = useCase(displayName = "Ada", currencyCode = "JPY")

        assertIs<Result.Success<Unit>>(result)
        assertEquals("Ada", profileRepo.displayName)
        assertEquals(true, profileRepo.onboardingComplete)
        assertEquals("JPY", currencyRepo.current?.code)
    }

    @Test
    fun invoke_marksCompleteWithoutTouchingNameOrCurrencyWhenBlank() = runTest {
        val profileRepo = FakeProfileRepository()
        val currencyRepo = FakeCurrencySettingsRepository()
        val useCase = CompleteOnboardingUseCase(profileRepo, currencyRepo)

        val result = useCase(displayName = "", currencyCode = "")

        assertIs<Result.Success<Unit>>(result)
        assertEquals("", profileRepo.displayName)
        assertEquals(true, profileRepo.onboardingComplete)
        assertEquals(null, currencyRepo.current)
    }

    @Test
    fun invoke_shortCircuitsOnSetDisplayNameError_skippingOnboardingComplete() = runTest {
        val profileRepo = FakeProfileRepository(setDisplayNameError = "storage failure")
        val currencyRepo = FakeCurrencySettingsRepository()
        val useCase = CompleteOnboardingUseCase(profileRepo, currencyRepo)

        val result = useCase(displayName = "Ada", currencyCode = "JPY")

        assertIs<Result.Error>(result)
        assertEquals(false, profileRepo.onboardingComplete)
        assertEquals(null, currencyRepo.current)
    }

    @Test
    fun invoke_shortCircuitsOnSetOnboardingCompleteError_skippingCurrency() = runTest {
        val profileRepo = FakeProfileRepository(setOnboardingCompleteError = "storage failure")
        val currencyRepo = FakeCurrencySettingsRepository()
        val useCase = CompleteOnboardingUseCase(profileRepo, currencyRepo)

        val result = useCase(displayName = "Ada", currencyCode = "JPY")

        assertIs<Result.Error>(result)
        assertEquals(null, currencyRepo.current)
    }
}
