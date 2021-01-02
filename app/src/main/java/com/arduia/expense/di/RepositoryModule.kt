package com.arduia.expense.di

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import androidx.room.InvalidationTracker
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.*
import com.arduia.expense.data.local.*
import com.arduia.expense.data.network.ExpenseNetworkDao
import dagger.Binds
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
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager = context.assets

    @Provides
    fun provideCacheDao(): CacheDao = CacheDaoImpl

}

