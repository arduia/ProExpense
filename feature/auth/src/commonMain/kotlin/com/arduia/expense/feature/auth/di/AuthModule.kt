package com.arduia.expense.feature.auth.di

import com.arduia.expense.data.PinCredentialStore
import com.arduia.expense.feature.auth.DefaultPinAuthRepository
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.ResetPinUseCase
import com.arduia.expense.feature.auth.SetupPinUseCase
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.feature.auth.VerifyRecoveryAnswerUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val authModule = module {
    single<PinAuthRepository> {
        DefaultPinAuthRepository(credentialStore = get<PinCredentialStore>(), dispatcher = Dispatchers.Default)
    }
    factory { SetupPinUseCase(get()) }
    factory { VerifyPinUseCase(get()) }
    factory { VerifyRecoveryAnswerUseCase(get()) }
    factory { ResetPinUseCase(get()) }
    factory { DisablePinUseCase(get()) }
}
