package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.home.ExpenseDetailMapper
import com.arduia.expense.ui.home.ExpenseVoMapper
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
        dateFormatter: DateFormat,
        categoryProvider: ExpenseCategoryProvider,
        currencyRepo: CurrencyRepository
    ): Mapper<ExpenseEnt, ExpenseDetailsVto> =
        ExpenseDetailMapper(currencyFormatter, dateFormatter, categoryProvider, currencyRepo)

    @Provides
    fun provideExpenseVoMapper(
        @CurrencyDecimalFormat currencyFormatter: NumberFormat,
        dateFormatter: DateFormat,
        categoryProvider: ExpenseCategoryProvider,
        currencyRepo: CurrencyRepository
    ): Mapper<ExpenseEnt, ExpenseVto> =
        ExpenseVoMapper(currencyFormatter, dateFormatter, categoryProvider, currencyRepo)

}