package com.arduia.expense

import android.app.Application
import timber.log.Timber

class ExpenseApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        when(BuildConfig.DEBUG){
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
    }
}
