package com.arduia.expense.di

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.expense.backup.schema.BackupSchema
import com.arduia.expense.data.backup.SchemaBackupSheet
import com.arduia.expense.data.backup.SchemaBackupSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class AbstractBackupModule {

    @Binds
    abstract fun bindSchemaSource(impl: SchemaBackupSource): BackupSource<BackupSchema>

    @Binds
    abstract fun bindSchemaBackupSheet(impl: SchemaBackupSheet): BackupSheet<BackupSchema>
}