package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.category.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.language.LanguageProvider
import com.arduia.expense.ui.common.language.LanguageProviderImpl
import com.arduia.expense.ui.home.ExpenseDayNameProvider
import com.arduia.expense.ui.home.ExpenseRateCalculator
import com.arduia.expense.ui.home.ExpenseRateCalculatorFactory
import com.arduia.graph.DayNameProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ExpenseProviderModule {

    @Provides
    @ActivityScoped
    fun provideExpenseCategory(@ActivityContext context: Context): ExpenseCategoryProvider =
        ExpenseCategoryProviderImpl(context.resources)

    @Provides
    @ActivityScoped
    fun provideLanguage(): LanguageProvider =
        LanguageProviderImpl()

    @Provides
    @ActivityScoped
    fun provideExpenseCalculator(): ExpenseRateCalculator.Factory =
        ExpenseRateCalculatorFactory()

    @Provides
    @ActivityScoped
    fun provideDataNames(@ActivityContext context: Context): DayNameProvider =
        ExpenseDayNameProvider(context)
}
