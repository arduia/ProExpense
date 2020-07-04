package com.arduia.myacc.di

import android.content.Context
import com.arduia.myacc.data.AccRepository
import com.arduia.myacc.data.AccRepositoryImpl
import com.arduia.myacc.data.local.AccountingDatabase
import com.arduia.myacc.ui.common.CategoryProvider
import com.arduia.myacc.ui.mapping.ExpenseMapper

class ServiceLoader private constructor (private val context: Context){

    private val accDatabase by lazy {
        AccountingDatabase.getInstance(context.applicationContext)
    }

    fun getAccountingRepository(): AccRepository =
         AccRepositoryImpl(accDatabase.expenseDao)


    fun getCategoryProvider() = CategoryProvider()

    fun getTransactionMapper() = ExpenseMapper(getCategoryProvider())

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
