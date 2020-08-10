package com.arduia.expense.di

import android.app.Application
import android.content.Context
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.*
import com.arduia.expense.data.local.BackupDao
import com.arduia.expense.data.local.ExpenseDao
import com.arduia.expense.data.network.ExpenseNetworkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAccRepo(accDao: ExpenseDao, netDao: ExpenseNetworkDao): AccRepository
     = AccRepositoryImpl(accDao, netDao)

    @Provides
    @Singleton
    fun provideSettingRepo(application: Application, @CoroutineIO scope:CoroutineScope): SettingsRepository
    = SettingsRepositoryImpl(application.applicationContext, scope)

    @Singleton
    @Provides
    fun provideBackupRepo(@ApplicationContext context: Context, backupDao: BackupDao, excelBackup: ExcelBackup): BackupRepository
            = BackupRepositoryImpl(context, backupDao, excelBackup)

}
