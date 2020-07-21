package com.arduia.expense.di

import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.mapping.ExpenseMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.DecimalFormat
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(categoryProvider: ExpenseCategoryProvider,
                             @IntegerDecimal decimalFormat: DecimalFormat): ExpenseMapper
        = ExpenseMapper(categoryProvider, currencyFormatter = decimalFormat)
}
