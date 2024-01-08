package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.home.*
import com.arduia.expense.ui.common.mapper.CurrencyUiModelMapper
import com.arduia.expense.ui.entry.ExpenseUpdateDataUiModel
import com.arduia.expense.ui.entry.ExpenseUpdateDataUiModelMapper
import com.arduia.expense.ui.expenselogs.ExpenseLogUiModel
import com.arduia.expense.ui.expenselogs.ExpenseLogUiModelMapper
import com.arduia.expense.ui.expenselogs.ExpenseUiModelMapperFactoryImpl
import com.arduia.expense.ui.onboarding.CurrencyUiModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import java.text.NumberFormat

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractUiModelMapperModule {

    @Binds
    abstract fun provideUpdateDataMapper(
        impl: ExpenseUpdateDataUiModelMapper
    ): Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>

    @Binds
    abstract fun provideExpenseLogMapper(
        impl: ExpenseLogUiModelMapper
    ): Mapper<ExpenseEnt, ExpenseLogUiModel.Log>


    @Binds
    abstract fun provideCurrencyMapper(impl: CurrencyUiModelMapper): Mapper<CurrencyDto, CurrencyUiModel>

    @Binds
    abstract fun provideExpenseDetailMapper(
        imp: ExpenseDetailUiModelMapperFactoryImpl
    ): ExpenseDetailUiModelMapperFactory

    @Binds
    abstract fun provideExpenseUiModelMapperFactory(
        impl: ExpenseUiModelMapper.ExpenseUiModelMapperFactoryImpl
    ): ExpenseUiModelMapperFactory

}