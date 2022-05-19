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
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractExpenseModule {

    @Binds
    abstract fun provideExpenseCategory(impl: ExpenseCategoryProviderImpl): ExpenseCategoryProvider

    @Binds
    abstract fun provideLanguage(impl: LanguageProviderImpl): LanguageProvider

    @Binds
    abstract fun provideExpenseCalculator(impl: ExpenseRateCalculatorFactory): ExpenseRateCalculator.Factory

    @Binds
    abstract fun provideDataNames(impl: ExpenseDayNameProvider): DayNameProvider

}
