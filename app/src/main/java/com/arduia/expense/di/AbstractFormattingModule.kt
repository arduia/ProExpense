package com.arduia.expense.di

import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.formatter.MonthDateRangeFormatter
import com.arduia.expense.ui.common.formatter.StatisticDateRangeFormatter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AbstractFormattingModule {

    @Binds
    @StatisticDateRange
    abstract fun provideStatisticDateRangeFormatter(impl: StatisticDateRangeFormatter): DateRangeFormatter

    @Binds
    @MonthlyDateRange
    abstract fun provideMonthlyDateRangeFormatter(impl: MonthDateRangeFormatter): DateRangeFormatter

}