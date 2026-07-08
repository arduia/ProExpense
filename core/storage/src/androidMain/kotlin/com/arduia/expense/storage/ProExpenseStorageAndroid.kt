package com.arduia.expense.storage

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Android convenience overload: builds the driver/key-manager/key-value-store from [context]. */
fun ProExpenseStorage.Companion.create(
    context: Context,
    keyManager: DatabaseKeyManager = AndroidDatabaseKeyManager(context.applicationContext),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ProExpenseStorage {
    val appContext = context.applicationContext
    val onboardingPrefs = appContext.getSharedPreferences("onboarding_state", Context.MODE_PRIVATE)
    return ProExpenseStorage.create(
        driverFactory = DatabaseDriverFactory(appContext),
        keyManager = keyManager,
        keyValueStore = AndroidPlatformKeyValueStore(onboardingPrefs),
        dispatcher = dispatcher,
    )
}
