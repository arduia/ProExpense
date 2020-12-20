package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.formatter.ExpenseRecentDateFormatter
import com.arduia.expense.ui.common.formatter.StatisticDateRangeFormatter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import java.math.RoundingMode
import java.text.*
import java.util.*
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
object FormattingModule{

    @Provides
    @FloatingDecimal
    fun provideFloatingDecimalFormat() = DecimalFormat("#,##0.0")

    @Provides
    @IntegerDecimal
    fun provideDecimalDateFormat() = DecimalFormat("#,###")

    @Provides
    @CurrencyDecimalFormat
    fun provideCurrencyDecimalFormat(): NumberFormat = DecimalFormat.getInstance(Locale.ENGLISH).apply {
        maximumFractionDigits = 2
    }

    @Provides
    fun provideDateFormat(): DateFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)

    @Provides
    fun provideStatisticDateRangeFormatter(): DateRangeFormatter = StatisticDateRangeFormatter()

    @Provides
    fun provideDateFormatter(@ActivityContext context: Context): DateFormatter
    = ExpenseRecentDateFormatter(context)

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class FloatingDecimal


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class IntegerDecimal

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class CurrencyDecimalFormat