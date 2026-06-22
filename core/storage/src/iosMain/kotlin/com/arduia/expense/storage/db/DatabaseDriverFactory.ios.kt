package com.arduia.expense.storage.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS-readiness stub (principle #2). No iOS target is declared in this module yet, so this file is
 * not compiled today — it documents exactly where the iOS driver will live so adding the iOS
 * target later is "wire up SQLCipher's native driver here", not "find and extract the seam".
 *
 * When the iOS target is enabled, depend on a SQLCipher-enabled native driver and pass the
 * passphrase through; the commonMain repositories above need no changes.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(passphrase: ByteArray): SqlDriver =
        NativeSqliteDriver(ProExpenseDatabase.Schema, DATABASE_NAME)

    companion object {
        const val DATABASE_NAME = "proexpense.db"
    }
}
