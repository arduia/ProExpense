package com.arduia.expense.feature.auth.di

import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.PinAuthRepositoryImpl
import com.arduia.expense.storage.ProExpenseStorage
import org.koin.dsl.module

val authModule = module {
    single<PinAuthRepository> {
        PinAuthRepositoryImpl(appMetaStore = get<ProExpenseStorage>().appMetaStore)
    }
}
