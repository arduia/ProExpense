package com.arduia.expense.di

import com.arduia.backup.BackupSheet
import com.arduia.backup.ExcelBackup
import com.arduia.backup.FileNameGenerator
import com.arduia.backup.generator.BackupNameGenerator
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.backup.ExpenseBackupSheet
import com.arduia.expense.data.backup.ExpenseBackupSource
import com.arduia.expense.data.local.ExpenseEnt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object BackupModule {

    @Provides
    @Singleton
    fun provideExpenseSheet(source: ExpenseBackupSource): BackupSheet<ExpenseEnt>  =
        ExpenseBackupSheet(source)

    @Provides
    @Singleton
    fun provideExpenseSource(repo: AccRepository) =
        ExpenseBackupSource(repo)

    @Provides
    @Singleton
    fun provideExcelBackup(expenseSheet: BackupSheet<ExpenseEnt>) =
        ExcelBackup.Builder()
            .addBackupSheet(expenseSheet)
            .build()

    @Provides
    @Singleton
    @BackupNameGen
    fun provideBackupNameGen(): FileNameGenerator = BackupNameGenerator()
}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class BackupNameGen
