package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.home.*
import com.arduia.expense.ui.mapping.CurrencyMapper
import com.arduia.expense.ui.onboarding.CurrencyVo
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat

@Module
@InstallIn(ActivityComponent::class)
object VoMapperModule {

    @Provides
    fun provideCurrencyMapper(): Mapper<CurrencyDto, CurrencyVo> = CurrencyMapper()

    @Provides
    fun provideExpenseDetailMapper(
        @CurrencyDecimalFormat currencyFormatter: NumberFormat,
        dateFormatter: DateFormatter,
        categoryProvider: ExpenseCategoryProvider
    ): ExpenseDetailMapperFactory =
        ExpenseDetailMapperFactoryImpl(currencyFormatter, dateFormatter, categoryProvider)

    @Provides
    fun provideExpenseVoMapperFactory(
        @CurrencyDecimalFormat currencyFormatter: NumberFormat,
        dateFormatter: DateFormatter,
        categoryProvider: ExpenseCategoryProvider
    ): ExpenseVoMapperFactory =
        ExpenseVoMapper.ExpenseVoMapperFactoryImpl(currencyFormatter, dateFormatter, categoryProvider)

}