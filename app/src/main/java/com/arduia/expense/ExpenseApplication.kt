package com.arduia.expense

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.data.local.AccountingDatabase
import com.arduia.expense.data.local.AccountingDatabase_Impl
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

class ExpenseApplication: Application(){

    private lateinit var settings: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        when(BuildConfig.DEBUG){
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
    }

    override fun attachBaseContext(base: Context?) {
        runBlocking {
            base?.let {
                settings = SettingsRepositoryImpl(base, this)
                val selectedLanguage = settings.getSelectedLanguage().first()
                val localedContext = base.updateResource(selectedLanguage)
                super.attachBaseContext(localedContext)
            }
        }
    }

}
