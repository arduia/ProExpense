package com.arduia.expense.di

import android.app.Activity
import com.arduia.expense.ui.BackupMessageReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object BackupMessagingModule{

    @Provides
    fun provideBackupMsgReceiver(activity: Activity): BackupMessageReceiver =
        activity as BackupMessageReceiver
}
