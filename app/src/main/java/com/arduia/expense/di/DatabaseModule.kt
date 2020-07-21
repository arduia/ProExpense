package com.arduia.expense.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.data.local.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule{

    @Provides
    @Singleton
    fun provideAccDatabase(application: Application): AccountingDatabase =
        Room.databaseBuilder(application, AccountingDatabase::class.java, "accounting.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideAccDao(accDb: AccountingDatabase): ExpenseDao = accDb.expenseDao


}
