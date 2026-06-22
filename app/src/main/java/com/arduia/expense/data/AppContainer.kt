package com.arduia.expense.data

import android.content.Context
import com.arduia.expense.storage.StorageComponent
import com.arduia.expense.storage.db.DatabaseDriverFactory

/**
 * Manual DI root. The encrypted database is opened lazily on first access so unit/screenshot tests
 * that never touch a real repository don't load SQLCipher. Repositories are surfaced as their
 * contract interfaces only — the rest of the app never sees SQLDelight (F17).
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val storage: StorageComponent by lazy {
        StorageComponent.create(
            driverFactory = DatabaseDriverFactory(appContext),
            passphrase = DatabaseKeyProvider(appContext).getOrCreateKey(),
        )
    }

    val financeRecordRepository by lazy { storage.financeRecordRepository }
    val budgetRepository by lazy { storage.budgetRepository }
    val profileRepository by lazy { storage.profileRepository }
    val securityStateReader by lazy { storage.securityStateReader }
}
