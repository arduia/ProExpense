package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.ui.common.formatter.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
abstract class AbstractFormattingModule {

    @Binds
    @StatisticDateRange
    abstract fun provideStatisticDateRangeFormatter(impl: StatisticDateRangeFormatter): DateRangeFormatter

    @Binds
    @MonthlyDateRange
    abstract fun provideMonthlyDateRangeFormatter(impl: MonthDateRangeFormatter): DateRangeFormatter

    @Binds
    abstract fun provideDateFormatter(impl: ExpenseRecentDateFormatter): DateFormatter
}