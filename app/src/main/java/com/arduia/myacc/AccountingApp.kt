package com.arduia.myacc

import android.app.Application
import android.util.Log.d
import com.arduia.myacc.data.local.AccountingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountingApp: Application(){

    override fun onCreate() {
        super.onCreate()
        when(BuildConfig.DEBUG){
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
    }
}
