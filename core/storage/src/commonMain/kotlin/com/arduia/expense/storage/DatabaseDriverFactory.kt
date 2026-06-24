package com.arduia.expense.storage

import app.cash.sqldelight.db.SqlDriver

/**
 * Builds the platform [SqlDriver] backing the encrypted ProExpense database. The passphrase is the
 * raw SQLCipher key; key derivation/storage is the caller's responsibility (see DatabaseKeyManager).
 *
 * Declared as an `expect` seam now so iOS becomes an additive `actual`, not a later extraction.
 */
expect class DatabaseDriverFactory {
    fun createDriver(passphrase: ByteArray): SqlDriver
}
