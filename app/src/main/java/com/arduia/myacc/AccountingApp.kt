package com.arduia.myacc

import android.app.Application
import android.util.Log.d
import com.arduia.myacc.data.local.AccountingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountingApp: Application(){

    private val db by lazy {
        AccountingDatabase.getInstance(this)
    }
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch (Dispatchers.IO){
            d("AccountingApp", "onCreate -> ${db.transactionDao.getAllTransaction()}")
        }
    }
}
