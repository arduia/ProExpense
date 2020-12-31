package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.entry.ExpenseUpdateDataVto
import com.arduia.expense.ui.expenselogs.ExpenseLogVo
import com.arduia.expense.ui.expenselogs.ExpenseLogVoMapper
import com.arduia.expense.ui.entry.ExpenseUpdateDataUiModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.NumberFormat

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(
        categoryProvider: ExpenseCategoryProvider,
    ): Mapper<ExpenseEnt, ExpenseUpdateDataVto> = ExpenseUpdateDataUiModelMapper(
        categoryProvider
    )

    @Provides
    fun provideExpenseLogVoMapper(
        categoryProvider: ExpenseCategoryProvider,
        dateFormatter: DateFormatter,
        @CurrencyDecimalFormat decimalFormat: NumberFormat,
    ): Mapper<ExpenseEnt, ExpenseLogVo.Log> = ExpenseLogVoMapper(
        categoryProvider,
        dateFormatter,
        decimalFormat
    ) { "" }


}
