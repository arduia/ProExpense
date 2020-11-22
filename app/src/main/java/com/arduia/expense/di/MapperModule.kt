package com.arduia.expense.di

import android.content.Context
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.mapping.BackupVoMapper
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.mapping.ExpenseMapperImpl
import com.arduia.expense.ui.vto.BackupVto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.DateFormat
import java.text.DecimalFormat

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(
        categoryProvider: ExpenseCategoryProvider,
        dateFormatter: DateFormat,
        @CurrencyDecimalFormat decimalFormat: DecimalFormat
    ): ExpenseMapper = ExpenseMapperImpl(
        categoryProvider,
        dateFormatter = dateFormatter,
        currencyFormatter = decimalFormat
    )

    @Provides
    fun privateBackupMapper(
        @ActivityContext context: Context,
        dateFormatter: DateFormat
    ): Mapper<BackupEnt, BackupVto> = BackupVoMapper(context, dateFormatter)
}
