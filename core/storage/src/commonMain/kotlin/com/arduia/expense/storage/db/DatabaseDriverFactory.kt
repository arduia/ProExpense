package com.arduia.expense.storage.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform seam (iOS-readiness principle #2): every platform builds the SQLDelight driver
 * differently — Android wraps SQLCipher + a [android.content.Context], iOS uses the native
 * driver against the app's document directory — but everything above this line is shared.
 *
 * Only the Android `actual` is implemented today; the iOS `actual` is declared as an inert
 * stub so adding the iOS target later is "fill in this driver", not "extract this seam".
 */
expect class DatabaseDriverFactory {
    /** @param passphrase raw SQLCipher key bytes; never logged, never persisted in plaintext. */
    fun createDriver(passphrase: ByteArray): SqlDriver
}
