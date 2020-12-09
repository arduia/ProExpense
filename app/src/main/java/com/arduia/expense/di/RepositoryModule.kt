package com.arduia.expense.di

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.room.InvalidationTracker
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.*
import com.arduia.expense.data.local.*
import com.arduia.expense.data.network.ExpenseNetworkDao
import com.arduia.expense.ui.expense.DatabaseInvalidateMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAccRepo(accDao: ExpenseDao, netDao: ExpenseNetworkDao): ExpenseRepository =
        ExpenseRepositoryImpl(accDao, netDao)

    @Provides
    @Singleton
    fun providePreferenceStorageDao(
        application: Application
    ): PreferenceStorageDao = PreferenceFlowStorageDaoImpl(application.applicationContext)


    @Provides
    @Singleton
    fun provideExpenseSourceInvalidateMediator(db: InvalidationTracker): DatabaseInvalidateMediator{
        val invalidator = DatabaseInvalidateMediator()
        db.addObserver(invalidator)
        return invalidator
    }

    @Provides
    @Singleton
    fun provideSettingRepo(
        dao: PreferenceStorageDao
    ): SettingsRepository = SettingsRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideBackupRepo(
        @ApplicationContext context: Context,
        backupDao: BackupDao,
        excelBackup: ExcelBackup
    ): BackupRepository = BackupRepositoryImpl(context, backupDao, excelBackup)

    @Singleton
    @Provides
    fun provideCurrencyRepo(
        dao: CurrencyDao
    ): CurrencyRepository =
        CurrencyRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideCurrencyDao(assetManager: AssetManager): CurrencyDao =
        CurrencyDaoImpl(assetManager)

    @Provides
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager = context.assets

    @Provides
    fun provideCacheDao(): CacheDao = CacheDaoImpl
}

