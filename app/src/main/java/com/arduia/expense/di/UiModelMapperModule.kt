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
import com.arduia.expense.ui.onboarding.CurrencyUiModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.NumberFormat

@Module
@InstallIn(ActivityComponent::class)
object UiModelMapperModule {

    @Provides
    fun provideUpdateDataMapper(
        categoryProvider: ExpenseCategoryProvider,
    ): Mapper<ExpenseEnt, ExpenseUpdateDataUiModel> = ExpenseUpdateDataUiModelMapper(
        categoryProvider
    )

    @Provides
    fun provideExpenseLogMapper(
        categoryProvider: ExpenseCategoryProvider,
        dateFormatter: DateFormatter,
        @CurrencyDecimalFormat decimalFormat: NumberFormat,
    ): Mapper<ExpenseEnt, ExpenseLogUiModel.Log> = ExpenseLogUiModelMapper(
        categoryProvider,
        dateFormatter,
        decimalFormat
    ) { "" }


    @Provides
    fun provideCurrencyMapper(): Mapper<CurrencyDto, CurrencyUiModel> = CurrencyUiModelMapper()

    @Provides
    fun provideExpenseDetailMapper(
        @CurrencyDecimalFormat currencyFormatter: NumberFormat,
        dateFormatter: DateFormatter,
        categoryProvider: ExpenseCategoryProvider
    ): ExpenseDetailUiModelMapperFactory =
        ExpenseDetailUiModelMapperFactoryImpl(currencyFormatter, dateFormatter, categoryProvider)

    @Provides
    fun provideExpenseUiModelMapperFactory(
        @CurrencyDecimalFormat currencyFormatter: NumberFormat,
        dateFormatter: DateFormatter,
        categoryProvider: ExpenseCategoryProvider
    ): ExpenseUiModelMapperFactory =
        ExpenseUiModelMapper.ExpenseUiModelMapperFactoryImpl(currencyFormatter, dateFormatter, categoryProvider)

}