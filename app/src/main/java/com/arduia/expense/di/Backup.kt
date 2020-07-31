package com.arduia.expense.di

import com.arduia.backup.BackupSheet
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.backup.ExpenseBackupSheet
import com.arduia.expense.data.backup.ExpenseBackupSource
import com.arduia.expense.data.local.ExpenseEnt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object Backup {

    @Provides
    @Singleton
    fun provideExpenseSheet(source: ExpenseBackupSource): BackupSheet<ExpenseEnt>  =
        ExpenseBackupSheet(source)

    @Provides
    @Singleton
    fun provideExpenseSource(repo: AccRepository) =
        ExpenseBackupSource(repo)
}
