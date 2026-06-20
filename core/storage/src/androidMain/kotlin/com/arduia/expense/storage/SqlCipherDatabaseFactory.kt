package com.arduia.expense.storage

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.arduia.expense.storage.db.ProExpenseDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

data class OpenedDatabase(
    val database: ProExpenseDatabase,
    val driver: SqlDriver,
)

object SqlCipherDatabaseFactory {
    fun open(context: Context, passphrase: ByteArray): OpenedDatabase {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(passphrase)
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = ProExpenseDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
            factory = factory,
        )
        return OpenedDatabase(ProExpenseDatabase(driver), driver)
    }

    fun rekey(driver: SqlDriver, newPassphrase: ByteArray) {
        val hex = newPassphrase.toSqlCipherHex()
        driver.execute(null, "PRAGMA rekey = \"x'$hex'\"", 0)
    }

    private fun ByteArray.toSqlCipherHex(): String = joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }

    private const val DATABASE_NAME = "pro_expense.db"
}
