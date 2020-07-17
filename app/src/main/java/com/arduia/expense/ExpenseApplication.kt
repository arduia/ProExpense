package com.arduia.expense

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.data.local.AccountingDatabase_Impl
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import timber.log.Timber

class ExpenseApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        ExpenseCategoryProviderImpl(resources).init()
    }

    override fun attachBaseContext(base: Context?) {
        val localedContext = base?.updateResource("my")
        super.attachBaseContext(localedContext)
    }

}
