package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.common.LanguageProviderImpl
import com.arduia.expense.ui.home.ExpenseDayNameProvider
import com.arduia.expense.ui.home.ExpenseRateCalculator
import com.arduia.expense.ui.home.ExpenseRateCalculatorImpl
import com.arduia.graph.DayNameProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object ExpenseProviderModule {

    @Provides
    fun provideExpenseCategory(@ActivityContext context: Context): ExpenseCategoryProvider =
        ExpenseCategoryProviderImpl(context.resources)

    @Provides
    fun provideLanguage(): LanguageProvider =
        LanguageProviderImpl()

    @Provides
    fun provideExpenseCalculator(): ExpenseRateCalculator =
        ExpenseRateCalculatorImpl()

    @Provides
    fun provideDataNames(@ActivityContext context: Context): DayNameProvider =
        ExpenseDayNameProvider(context)
}
