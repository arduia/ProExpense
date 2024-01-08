package com.arduia.expense.di

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.backup.FileNameGenerator
import com.arduia.backup.generator.BackupNameGenerator
import com.arduia.expense.backup.schema.BackupSchema
import com.arduia.expense.data.backup.ExpenseBackupSheet
import com.arduia.expense.data.backup.ExpenseBackupSource
import com.arduia.expense.data.backup.SchemaBackupSheet
import com.arduia.expense.data.backup.SchemaBackupSource
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.data.local.ExpenseEnt
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractBackupModule {

    @Binds
    abstract fun bindSchemaSource(impl: SchemaBackupSource): BackupSource<BackupSchema>

    @Binds
    abstract fun bindSchemaBackupSheet(impl: SchemaBackupSheet): BackupSheet<BackupSchema>

    @Binds
    abstract fun bindExpenseSource(impl: ExpenseBackupSource): BackupSource<ExpenseEnt>

    @Binds
    abstract fun bindExpenseBackupSheet(impl: ExpenseBackupSheet): BackupSheet<ExpenseEnt>


}