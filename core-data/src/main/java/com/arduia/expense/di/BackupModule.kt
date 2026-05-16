package com.arduia.expense.di

import android.app.Activity
import com.arduia.backup.BackupSheet
import com.arduia.backup.ExcelBackup
import com.arduia.backup.FileNameGenerator
import com.arduia.backup.generator.BackupNameGenerator
import com.arduia.expense.backup.schema.BackupSchema
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.backup.ExpenseBackupSheet
import com.arduia.expense.data.backup.ExpenseBackupSource
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.BackupMessageReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BackupModule {


    @Provides
    @Singleton
    fun provideExcelBackup(
        expenseSheet: BackupSheet<ExpenseEnt>,
        schemaSheet: BackupSheet<BackupSchema>
    ) =
        ExcelBackup.Builder()
            .addBackupSheet(expenseSheet)
            .addBackupSheet(schemaSheet)
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
