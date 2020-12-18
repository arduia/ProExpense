package com.arduia.expense.di

import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapperFactory
import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapperFactoryImpl
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapper.*;
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapperFactory
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
}