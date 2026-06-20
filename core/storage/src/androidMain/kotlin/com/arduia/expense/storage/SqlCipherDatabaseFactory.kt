package com.arduia.expense.storage

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.arduia.expense.storage.db.ProExpenseDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

object SqlCipherDatabaseFactory {
    fun open(context: Context, passphrase: ByteArray): ProExpenseDatabase {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(passphrase)
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = ProExpenseDatabase.Schema,
            context = context,
            name = "pro_expense.db",
            factory = factory,
        )
        return ProExpenseDatabase(driver)
    }
}
