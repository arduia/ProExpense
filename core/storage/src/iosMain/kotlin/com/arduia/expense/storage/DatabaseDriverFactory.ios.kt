package com.arduia.expense.storage

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.arduia.expense.storage.db.ProExpenseDatabase

/**
 * `NativeSqliteDriver` links against the system `sqlite3` by default — the `encryptionConfig`
 * PRAGMA set below only takes effect once the iosApp build links a SQLCipher-iOS binary in its
 * place (Phase 3 of docs/ios_compatibility_plan.md; requires Xcode/macOS to build/verify). This
 * class is otherwise final and needs no further change for that phase — only the app's linked
 * native library changes. The raw key is passed as SQLCipher's `x'<hex>'` literal-key form (no
 * PBKDF2 derivation here), mirroring Android's `SupportOpenHelperFactory(passphrase: ByteArray)`.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(passphrase: ByteArray): SqlDriver =
        NativeSqliteDriver(
            schema = ProExpenseDatabase.Schema,
            name = DATABASE_NAME,
            onConfiguration = { config ->
                config.copy(
                    encryptionConfig = DatabaseConfiguration.Encryption(key = "x'${passphrase.toHex()}'"),
                )
            },
        )

    private companion object {
        const val DATABASE_NAME = "proexpense.db"
    }
}

private fun ByteArray.toHex(): String =
    joinToString("") { byte ->
        (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
    }
