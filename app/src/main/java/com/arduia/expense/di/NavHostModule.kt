package com.arduia.expense.di

import android.app.Activity
import com.arduia.expense.ExpenseApplication_HiltComponents
import com.arduia.expense.ui.MainHost
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object NavHostModule {

    @Provides
    @ActivityScoped
    fun provideMainHost( app: Activity) = app as MainHost

}