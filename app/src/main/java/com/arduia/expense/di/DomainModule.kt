package com.arduia.expense.di

import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.statistics.CategoryAnalyzer
import com.arduia.expense.ui.statistics.CategoryAnalyzerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object DomainModule {

    @Provides
    fun provideCategoryStatisticAnalyzer(categoryProvider: ExpenseCategoryProvider): CategoryAnalyzer =
        CategoryAnalyzerImpl(categoryProvider)

}