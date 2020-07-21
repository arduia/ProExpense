package com.arduia.expense.di

import android.app.Application
import android.content.Context
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.AccRepositoryImpl
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.data.local.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAccRepo(accDao: ExpenseDao): AccRepository
     = AccRepositoryImpl(accDao)

    @Provides
    @Singleton
    fun provideSettingRepo(application: Application, @CoroutineIO scope:CoroutineScope): SettingsRepository
    = SettingsRepositoryImpl(application.applicationContext, scope)

}
