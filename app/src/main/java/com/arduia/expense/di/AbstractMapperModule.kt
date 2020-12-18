package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.expense.ExpenseLogVo
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AbstractMapperModule {

    @Binds
    abstract fun bindExpenseEntToLogVoMapper(mapper: ExpenseEntToLogVoMapper):
            Mapper<ExpenseEnt, ExpenseLogVo>

}