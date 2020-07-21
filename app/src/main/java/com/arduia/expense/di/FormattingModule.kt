package com.arduia.expense.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.DecimalFormat
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

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class FloatingDecimal


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class IntegerDecimal
