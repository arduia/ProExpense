package com.arduia.expense.storage

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.arduia.expense.storage.db.ProExpenseDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(passphrase: ByteArray): SqlDriver {
        // sqlcipher-android (4.5.0+) ships the native lib but does not auto-load it.
        System.loadLibrary("sqlcipher")
        return AndroidSqliteDriver(
            schema = ProExpenseDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
            factory = SupportOpenHelperFactory(passphrase),
        )
    }

    private companion object {
        const val DATABASE_NAME = "proexpense.db"
    }
}
