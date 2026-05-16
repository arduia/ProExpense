package com.arduia.expense.di

import android.content.res.AssetManager
import com.arduia.expense.data.*
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDaoImpl
import com.arduia.expense.data.local.PreferenceFlowStorageDaoImpl
import com.arduia.expense.data.local.PreferenceStorageDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractRepoModule {

    @Binds
    abstract fun bindServerRepoModule(impl: ProExpenseServerRepositoryImpl):
            ProExpenseServerRepository

    @Binds
    @Singleton
    abstract fun provideAccRepo(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun providePreferenceStorageDao(
        impl: PreferenceFlowStorageDaoImpl
    ): PreferenceStorageDao

    @Binds
    @Singleton
    abstract fun provideSettingRepo(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun provideSettingRepoFactory(impl: SettingRepositoryFactoryImpl): SettingsRepository.Factory

    @Binds
    @Singleton
    abstract fun provideBackupRepo(
        impl: BackupRepositoryImpl
    ): BackupRepository

    @Binds
    @Singleton
    abstract fun provideCurrencyRepo(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    @Singleton
    abstract fun provideCurrencyDao(impl: CurrencyDaoImpl): CurrencyDao
}