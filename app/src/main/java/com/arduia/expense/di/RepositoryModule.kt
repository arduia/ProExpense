package com.arduia.expense.di

import android.app.Application
import android.content.Context
import com.arduia.expense.data.*
import com.arduia.expense.data.local.BackupDao
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

    @Singleton
    @Provides
    fun provideBackupRepo(backupDao: BackupDao): BackupRepository
            = BackupRepositoryImpl(backupDao)

}
