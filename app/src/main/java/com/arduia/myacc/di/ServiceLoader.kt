package com.arduia.myacc.di

import android.content.Context
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.AccRepositoryImpl
import com.arduia.myacc.data.local.AccountingDatabase
import com.arduia.myacc.data.local.Transaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ServiceLoader(private val context: Context){

    private val accDatabase by lazy {
        AccountingDatabase.getInstance(context.applicationContext)
            .apply {  }
    }

    fun getAccountingRepository(): AccRepository =
         AccRepositoryImpl(
            accDatabase.transactionDao,
            accDatabase.owePeopleDao,
            accDatabase.oweLogDao)

}
