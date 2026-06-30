package com.arduia.expense.feature.onboarding.di

import com.arduia.expense.feature.onboarding.CompleteOnboardingUseCase
import com.arduia.expense.feature.onboarding.GetOnboardingStatusUseCase
import org.koin.dsl.module

val onboardingModule = module {
    factory { GetOnboardingStatusUseCase(get()) }
    factory { CompleteOnboardingUseCase(get(), get()) }
}
