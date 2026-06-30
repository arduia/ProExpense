package com.arduia.expense.feature.onboarding

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode

data class OnboardingStatus(
    val isComplete: Boolean,
    val displayName: String,
)

class GetOnboardingStatusUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(): OnboardingStatus {
        val isComplete = (profileRepository.isOnboardingComplete() as? Result.Success)?.data ?: false
        val displayName = (profileRepository.getDisplayName() as? Result.Success)?.data.orEmpty()
        return OnboardingStatus(isComplete, displayName)
    }
}

class CompleteOnboardingUseCase(
    private val profileRepository: ProfileRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
) {
    suspend operator fun invoke(displayName: String, currencyCode: String): Result<Unit> {
        if (displayName.isNotBlank()) {
            val result = profileRepository.setDisplayName(displayName)
            if (result is Result.Error) return result
        }
        val completeResult = profileRepository.setOnboardingComplete()
        if (completeResult is Result.Error) return completeResult
        if (currencyCode.isNotBlank()) {
            val result = currencySettingsRepository.setHomeCurrency(CurrencyCode(currencyCode))
            if (result is Result.Error) return result
        }
        return Result.Success(Unit)
    }
}
