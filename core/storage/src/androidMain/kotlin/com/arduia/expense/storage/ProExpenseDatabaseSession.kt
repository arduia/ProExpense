package com.arduia.expense.storage

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ProExpenseDatabaseSession(
    val keyStorage: KeyRotationStore,
    val dataStore: SqlDelightDataStore,
    private val driver: SqlDriver,
) : DatabaseRekeyer {
    override suspend fun rekey(newPassphrase: ByteArray) = withContext(Dispatchers.IO) {
        SqlCipherDatabaseFactory.rekey(driver, newPassphrase)
    }

    fun close() {
        driver.close()
    }

    companion object {
        fun open(context: Context, keyStorage: KeyRotationStore): ProExpenseDatabaseSession = runBlocking {
            val key = keyStorage.getActiveKey() ?: run {
                val bootstrapKey = ByteArray(32)
                Random.Default.nextBytes(bootstrapKey)
                keyStorage.setRandomKey(bootstrapKey)
                bootstrapKey
            }
            val opened = SqlCipherDatabaseFactory.open(context, key)
            val dataStore = SqlDelightDataStore(opened.database).also { it.bootstrapFromMeta() }
            ProExpenseDatabaseSession(
                keyStorage = keyStorage,
                dataStore = dataStore,
                driver = opened.driver,
            )
        }
    }
}
