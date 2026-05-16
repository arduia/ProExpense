package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.expenselogs.ExpenseUiModelMapperFactory
import com.arduia.expense.ui.expenselogs.ExpenseUiModelMapperFactoryImpl
import com.arduia.expense.ui.about.AboutUpdateUiModelMapper
import com.arduia.expense.ui.backup.BackupUiModelMapper
import com.arduia.expense.ui.expenselogs.ExpenseEntToLogVoMapper.*;
import com.arduia.expense.ui.expenselogs.ExpenseEntToLogVoMapperFactory
import com.arduia.expense.ui.backup.BackupUiModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractMapperModule {

    @Binds
    abstract fun bindExpenseLogVoMapperFactory(impl: ExpenseUiModelMapperFactoryImpl):
            ExpenseUiModelMapperFactory

    @Binds
    abstract fun bindExpenseEntToLogMapperFactory(impl:  ExpenseEntToLogVoMapperFactoryImpl):
            ExpenseEntToLogVoMapperFactory

    @Binds
    abstract fun bindAboutUpdateUiToDataMapper(impl: AboutUpdateUiModelMapper):
            Mapper<AboutUpdateDataModel, AboutUpdateUiModel>

    @Binds
    abstract fun bindBackupVoMapper(impl: BackupUiModelMapper): Mapper<BackupEnt, BackupUiModel>

}