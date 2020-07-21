package com.arduia.expense.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(ApplicationComponent::class)
object CoroutineScopeModule {

    @Provides
    @CoroutineIO
    fun provideCoroutineIO(): CoroutineScope =
        CoroutineScope(Dispatchers.IO)


    @Provides
    @CoroutineMain
    fun provideCoroutineMain(): CoroutineScope =
        CoroutineScope(Dispatchers.Main)
}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineIO

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineMain
