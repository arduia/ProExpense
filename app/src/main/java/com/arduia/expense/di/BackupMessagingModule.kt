package com.arduia.expense.di

import android.app.Activity
import com.arduia.expense.ui.BackupMessageReceiver
import com.arduia.expense.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BackupMessagingModule{

}
