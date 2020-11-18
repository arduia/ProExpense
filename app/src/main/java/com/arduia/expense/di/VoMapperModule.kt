package com.arduia.expense.di

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.ui.mapping.CurrencyMapper
import com.arduia.expense.ui.onboarding.CurrencyVo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
object VoMapperModule {

    @Provides
    fun provideCurrencyMapper(): Mapper<CurrencyDto, CurrencyVo> = CurrencyMapper()

}