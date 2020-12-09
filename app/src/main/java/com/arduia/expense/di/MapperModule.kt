package com.arduia.expense.di

import android.content.Context
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.expense.ExpenseLogVo
import com.arduia.expense.ui.expense.mapper.ExpenseLogTransform
import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapper
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(
        categoryProvider: ExpenseCategoryProvider,
        dateFormatter: DateFormat,
        @CurrencyDecimalFormat decimalFormat: NumberFormat
    ): ExpenseMapper = ExpenseMapperImpl(
        categoryProvider,
        dateFormatter = dateFormatter,
        currencyFormatter = decimalFormat
    )

    @Provides
    fun provideExpenseVoMapper(
        categoryProvider: ExpenseCategoryProvider,
        dateFormatter: DateFormat,
        @CurrencyDecimalFormat decimalFormat: NumberFormat,
    ): Mapper<ExpenseEnt, ExpenseLogVo.Log> = ExpenseLogVoMapper(
        categoryProvider,
        dateFormatter,
        decimalFormat
    )

    @Provides
    fun provideExpenseLogTransform(logMapper: Mapper<ExpenseEnt, ExpenseLogVo.Log>): Mapper<List<ExpenseEnt>, List<ExpenseLogVo>> =
        ExpenseLogTransform(
            headerDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH),
            logMapper
        )

    @Provides
    fun privateBackupMapper(
        @ActivityContext context: Context,
        dateFormatter: DateFormat
    ): Mapper<BackupEnt, BackupVto> = BackupVoMapper(context, dateFormatter)
}
