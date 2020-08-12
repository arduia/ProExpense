package com.arduia.expense.di

import android.app.Application
import androidx.room.Room
import com.arduia.expense.data.local.ProExpenseDatabase
import com.arduia.expense.data.local.BackupDao
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
    fun provideAccDatabase(application: Application): ProExpenseDatabase {
        return  Room.databaseBuilder(application, ProExpenseDatabase::class.java, "accounting.db")
            .build()
    }


    @Provides
    @Singleton
    fun provideAccDao(accDb: ProExpenseDatabase): ExpenseDao = accDb.expenseDao

    @Provides
    @Singleton
    fun provideBackupDao(db: ProExpenseDatabase): BackupDao = db.backupDao
}
