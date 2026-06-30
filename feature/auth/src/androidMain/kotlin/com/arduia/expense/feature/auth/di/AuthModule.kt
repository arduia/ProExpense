package com.arduia.expense.feature.auth.di

import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.PinAuthRepositoryImpl
import com.arduia.expense.feature.auth.ResetPinUseCase
import com.arduia.expense.feature.auth.SetupPinUseCase
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.feature.auth.VerifyRecoveryAnswerUseCase
import com.arduia.expense.storage.ProExpenseStorage
import org.koin.dsl.module

val authModule = module {
    single<PinAuthRepository> {
        PinAuthRepositoryImpl(appMetaStore = get<ProExpenseStorage>().appMetaStore)
    }
    factory { SetupPinUseCase(get()) }
    factory { VerifyPinUseCase(get()) }
    factory { VerifyRecoveryAnswerUseCase(get()) }
    factory { ResetPinUseCase(get()) }
}
