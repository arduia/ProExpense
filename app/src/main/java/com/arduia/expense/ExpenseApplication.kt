package com.arduia.expense

import android.app.Application
import com.arduia.expense.di.appModule
import com.arduia.expense.feature.auth.di.authModule
import com.arduia.expense.feature.currency.di.currencyModule
import com.arduia.expense.feature.logging.di.loggingModule
import com.arduia.expense.storage.ProExpenseStorage
import com.arduia.expense.storage.di.storageModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

class ExpenseApplication : Application(), KoinComponent {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var started = false

    /**
     * Started lazily from the UI entry point, not [onCreate]. Starting Koin here opens the
     * SQLCipher database and touches the Android Keystore, which is unavailable on the plain JVM
     * used by Robolectric/screenshot tests — eager init there would crash every UI test.
     */
    fun ensureStarted() {
        if (started) return
        started = true
        startKoin {
            androidContext(this@ExpenseApplication)
            modules(storageModule, loggingModule, currencyModule, authModule, appModule)
        }
        appScope.launch { get<ProExpenseStorage>().seedDefaultCategories() }
    }
}
