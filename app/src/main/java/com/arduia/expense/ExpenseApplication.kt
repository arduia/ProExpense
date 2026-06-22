package com.arduia.expense

import android.app.Application
import com.arduia.expense.di.platformStorageModule
import com.arduia.expense.di.viewModelModule
import com.arduia.expense.storage.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class ExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Guard against double-start under Robolectric, which reuses the Koin global across tests.
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@ExpenseApplication)
                modules(platformStorageModule, repositoryModule, viewModelModule)
            }
        }
    }
}
