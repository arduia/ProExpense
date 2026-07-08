package com.arduia.expense.storage

import android.content.Context
import com.arduia.expense.storage.repository.SharedPreferencesOnboardingFlagStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Android entry point: opens the encrypted DB via Keystore + SQLCipher, then delegates to the shared builder. */
fun ProExpenseStorage.Companion.create(
    context: Context,
    keyManager: DatabaseKeyManager = AndroidDatabaseKeyManager(context.applicationContext),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): ProExpenseStorage {
    val passphrase = keyManager.getOrCreateDatabaseKey()
    val driver = DatabaseDriverFactory(context.applicationContext).createDriver(passphrase)
    val database = com.arduia.expense.storage.db.ProExpenseDatabase(driver)
    val onboardingPrefs = context.applicationContext
        .getSharedPreferences("onboarding_state", Context.MODE_PRIVATE)
    return buildProExpenseStorage(
        database = database,
        dispatcher = dispatcher,
        onboardingFlagStore = SharedPreferencesOnboardingFlagStore(onboardingPrefs),
    )
}
