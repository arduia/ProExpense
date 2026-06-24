package com.arduia.expense

import android.app.Application
import com.arduia.expense.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ExpenseApplication : Application() {

    /**
     * Built lazily on first access (from the UI entry point), not in [onCreate]. Constructing it
     * opens the SQLCipher database and touches the Android Keystore, which is unavailable on the
     * plain JVM used by Robolectric/screenshot tests — eager init there would crash every UI test.
     */
    val container: AppContainer by lazy { AppContainer(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var started = false

    /** Idempotently triggers the first-launch data bootstrap. Call from the UI entry point. */
    fun ensureStarted() {
        if (started) return
        started = true
        appScope.launch { container.initialize() }
    }
}
