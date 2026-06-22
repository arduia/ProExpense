package com.arduia.expense.storage.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/**
 * Android driver backed by SQLCipher (A2/C6). The [passphrase] is the SQLCipher key; the file on
 * disk is encrypted at rest from the first write. AndroidSqliteDriver owns schema create/migrate.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(passphrase: ByteArray): SqlDriver {
        System.loadLibrary("sqlcipher")
        return AndroidSqliteDriver(
            schema = ProExpenseDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
            factory = SupportOpenHelperFactory(passphrase),
        )
    }

    companion object {
        const val DATABASE_NAME = "proexpense.db"
    }
}
