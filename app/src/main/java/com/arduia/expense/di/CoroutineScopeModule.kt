package com.arduia.expense.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ApplicationComponent::class)
object CoroutineScopeModule {

    @Provides
    fun provideCoroutineIO(): CoroutineScope =
        CoroutineScope(Dispatchers.IO)


    @Provides
    fun provideCoroutineMain(): CoroutineScope =
        CoroutineScope(Dispatchers.Main)
}
