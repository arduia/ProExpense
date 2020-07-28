package com.arduia.expense.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
    fun provideDateFormat(): DateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class FloatingDecimal


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class IntegerDecimal
