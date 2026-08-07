package com.arduia.expense.shell

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.feature.onboarding.CompleteOnboardingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Backbone coverage for the first-launch flow.
 *
 * Traceability: US-ONB-1 Scenario 2 (swiping tracks progress), Scenario 3 (no forced use-case
 * selection — Skip goes to profile setup rather than bypassing it), and US-ONB-4 (profile + home
 * currency are captured before the shell opens).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private fun TestScope.viewModel(onboardingFails: Boolean = false): OnboardingViewModel =
        OnboardingViewModel(
            completeOnboarding =
                CompleteOnboardingUseCase(
                    FakeOnboardingProfile(failOnComplete = onboardingFails),
                    FakeOnboardingCurrencySettings(),
                ),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `next advances through the carousel then hands off to profile setup`() =
        runTest {
            val vm = viewModel()

            repeat(OnboardingPage.ordered.lastIndex) { vm.onNext() }
            assertTrue(vm.uiState.value.isLastPage)
            assertEquals(OnboardingStep.Carousel, vm.uiState.value.step)

            vm.onNext()

            assertEquals(OnboardingStep.ProfileSetup, vm.uiState.value.step)
        }

    @Test
    fun `skip goes to profile setup rather than bypassing setup entirely`() =
        runTest {
            val vm = viewModel()

            vm.onSkip()

            assertEquals(OnboardingStep.ProfileSetup, vm.uiState.value.step)
        }

    @Test
    fun `back steps out of profile setup and then back through pages`() =
        runTest {
            val vm = viewModel()
            vm.onNext()
            vm.onSkip()

            vm.onBack()
            assertEquals(OnboardingStep.Carousel, vm.uiState.value.step)
            assertEquals(1, vm.uiState.value.pageIndex)

            vm.onBack()
            assertEquals(0, vm.uiState.value.pageIndex)

            // Already at the first page — back is a no-op rather than going negative.
            vm.onBack()
            assertEquals(0, vm.uiState.value.pageIndex)
        }

    @Test
    fun `finish reports success and clears the saving flag`() =
        runTest {
            val vm = viewModel()
            vm.onNameChange("Maya")
            vm.onCurrencySelected("EUR")

            val finished = vm.finish()

            assertTrue(finished)
            assertFalse(vm.uiState.value.isSaving)
        }

    @Test
    fun `finish reports failure so the shell does not advance its gate`() =
        runTest {
            val vm = viewModel(onboardingFails = true)

            val finished = vm.finish()

            assertFalse(finished)
            assertFalse(vm.uiState.value.isSaving)
        }

    @Test
    fun `currency search filters on both code and name`() =
        runTest {
            val vm = viewModel()

            vm.onCurrencyQueryChange("eur")

            val options = vm.uiState.value.currencyOptions
            assertTrue(options.isNotEmpty())
            assertTrue(options.any { it.code == "EUR" })
        }
}

private class FakeOnboardingProfile(
    private val failOnComplete: Boolean = false,
) : ProfileRepository {
    override suspend fun setDisplayName(name: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getDisplayName(): Result<String> = Result.Success("")

    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(false)

    override suspend fun setOnboardingComplete(): Result<Unit> = if (failOnComplete) Result.Error("disk full") else Result.Success(Unit)
}

private class FakeOnboardingCurrencySettings : CurrencySettingsRepository {
    override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(CurrencyCode("USD"))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
}
