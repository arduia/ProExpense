package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.AccRepositoryImpl
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.mapping.ExpenseMapper

class ServiceLoader private constructor (private val context: Context){

    private val accDatabase by lazy {
        AccountingDatabase.getInstance(context.applicationContext)
    }

    fun getAccountingRepository(): AccRepository =
         AccRepositoryImpl(accDatabase.expenseDao )


    fun getCategoryProvider(): ExpenseCategoryProvider
            = ExpenseCategoryProviderImpl(context.resources)

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
