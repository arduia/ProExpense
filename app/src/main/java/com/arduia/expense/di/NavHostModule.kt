package com.arduia.expense.di

import android.app.Activity
import com.arduia.expense.ExpenseApplication_HiltComponents
import com.arduia.expense.ui.MainHost
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ActivityComponent::class)
object NavHostModule {

    @Provides
    fun provideMainHost( app: Activity) = app as MainHost

}