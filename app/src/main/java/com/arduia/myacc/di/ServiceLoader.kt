package com.arduia.myacc.di

import android.app.Application
import android.content.Context
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.AccRepositoryImpl
import com.arduia.myacc.data.local.AccountingDatabase
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.ui.common.CategoryProvider
import com.arduia.myacc.ui.mapping.TransactionMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ServiceLoader private constructor (private val context: Context){

    private val accDatabase by lazy {
        AccountingDatabase.getInstance(context.applicationContext)
    }

    fun getAccountingRepository(): AccRepository =
         AccRepositoryImpl(
            accDatabase.transactionDao,
            accDatabase.owePeopleDao,
            accDatabase.oweLogDao)


    fun getCategoryProvider() = CategoryProvider()

    fun getTransactionMapper() = TransactionMapper(getCategoryProvider())

    companion object{

        @Volatile
        private  var INSTANCE: ServiceLoader? = null

        fun getInstance(context: Context): ServiceLoader{

            if(INSTANCE == null){
                synchronized(this){
                    if(INSTANCE == null){
                        INSTANCE = ServiceLoader(context)
                    }
                }
            }

            return INSTANCE!!
        }
    }

}
