package com.arduia.expense.di

import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.statistics.CategoryAnalyzer
import com.arduia.expense.ui.statistics.CategoryAnalyzerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractDomainModule {

    @Binds
    abstract fun provideCategoryStatisticAnalyzer(impl: CategoryAnalyzerImpl): CategoryAnalyzer

}