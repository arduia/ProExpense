package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.expenselogs.ExpenseLogVoMapperFactory
import com.arduia.expense.ui.expenselogs.ExpenseLogVoMapperFactoryImpl
import com.arduia.expense.ui.about.AboutUpdateUiModelMapper
import com.arduia.expense.ui.backup.BackupVoMapper
import com.arduia.expense.ui.expenselogs.ExpenseEntToLogVoMapper.*;
import com.arduia.expense.ui.expenselogs.ExpenseEntToLogVoMapperFactory
import com.arduia.expense.ui.backup.BackupVto
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AbstractMapperModule {

    @Binds
    abstract fun bindExpenseLogVoMapperFactory(factory: ExpenseLogVoMapperFactoryImpl):
            ExpenseLogVoMapperFactory

    @Binds
    abstract fun bindExpenseEntToLogMapperFactory(factory:  ExpenseEntToLogVoMapperFactoryImpl):
            ExpenseEntToLogVoMapperFactory

    @Binds
    abstract fun bindAboutUpdateUiToDataMapper(impl: AboutUpdateUiModelMapper):
            Mapper<AboutUpdateDataModel, AboutUpdateUiModel>

    @Binds
    abstract fun bindBackupVoMapper(impl: BackupVoMapper): Mapper<BackupEnt, BackupVto>

}