package com.arduia.expense.di

import com.arduia.expense.data.ProExpenseServerRepository
import com.arduia.expense.data.ProExpenseServerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class AbstractRepoModule {

    @Binds
    abstract fun bindServerRepoModule(impl: ProExpenseServerRepositoryImpl):
            ProExpenseServerRepository
}