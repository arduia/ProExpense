package com.arduia.expense.di

import android.content.Context
import android.view.LayoutInflater
import com.arduia.expense.ui.expense.ExpenseListAdapter
import com.arduia.expense.ui.home.RecentListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object AdapterModule{

    @Provides
    fun provideLayoutInflater(@ActivityContext context: Context) = LayoutInflater.from(context)

    @Provides
    fun provideExpenseAdapter(layoutInflater: LayoutInflater) =
        ExpenseListAdapter(layoutInflater)

    @Provides
    fun provideRecentListAdapter(layoutInflater: LayoutInflater) =
        RecentListAdapter(layoutInflater)

}
