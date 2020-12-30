package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapperFactory
import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapperFactoryImpl
import com.arduia.expense.ui.mapping.AboutUpdateUiToDataMapper
import com.arduia.expense.ui.mapping.BackupVoMapper
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapper.*;
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapperFactory
import com.arduia.expense.ui.vto.BackupVto
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
    abstract fun bindAboutUpdateUiToDataMapper(impl: AboutUpdateUiToDataMapper):
            Mapper<AboutUpdateDataModel, AboutUpdateUiModel>

    @Binds
    abstract fun bindBackupVoMapper(impl: BackupVoMapper): Mapper<BackupEnt, BackupVto>

}