package com.arduia.expense.storage

import app.cash.sqldelight.db.SqlDriver

/**
 * iOS-readiness stub (iOS-readiness principle #2): the seam exists so iOS is additive, not
 * extracted later. Compiles as of the iOS targets in `core/storage/build.gradle.kts`, but
 * `createDriver` is not implemented yet. When the iosApp phase starts, add the SQLDelight native
 * driver dependency and implement this with SQLCipher-iOS (e.g. NativeSqliteDriver with a key
 * pragma) rather than the Android SupportOpenHelperFactory.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(passphrase: ByteArray): SqlDriver {
        TODO("iOS driver not implemented yet — add NativeSqliteDriver + SQLCipher-iOS in the iosApp phase")
    }
}
