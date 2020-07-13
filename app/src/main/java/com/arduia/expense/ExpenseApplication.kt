package com.arduia.expense

import android.app.Application
import android.view.LayoutInflater
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.data.local.AccountingDatabase_Impl
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import timber.log.Timber

class ExpenseApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        when(BuildConfig.DEBUG){
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
        ExpenseCategoryProviderImpl(resources).init()
    }

}
