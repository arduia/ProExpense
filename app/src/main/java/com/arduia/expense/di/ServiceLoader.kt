package com.arduia.expense.di

import android.content.Context
import androidx.room.Room
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.AccRepositoryImpl
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.home.ExpenseRateCalculatorImpl
import com.arduia.expense.ui.mapping.ExpenseMapper

class ServiceLoader private constructor (private val context: Context){

    init {
        accDatabase = Room.databaseBuilder(context, AccountingDatabase::class.java, "acc.db").build()
    }

    fun getAccountingRepository(): AccRepository =
         AccRepositoryImpl(accDatabase.expenseDao )


    fun getCategoryProvider(): ExpenseCategoryProvider
            = ExpenseCategoryProviderImpl(context.resources)

    fun getExpenseMapper() = ExpenseMapper(getCategoryProvider())

    fun getExpenseRateCalculator() = ExpenseRateCalculatorImpl()

    companion object{

        private lateinit var accDatabase: AccountingDatabase

        @Volatile
        private  var INSTANCE: ServiceLoader? = null

        fun getInstance(context: Context) =  ServiceLoader(context)
    }

}
