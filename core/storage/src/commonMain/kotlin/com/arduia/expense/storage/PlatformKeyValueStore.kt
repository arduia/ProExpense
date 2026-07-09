package com.arduia.expense.storage

/**
 * Fast, synchronous key-value cache fronting the encrypted DB for a handful of settings that need
 * to be readable before Koin/SQLCipher are available (e.g. locale on `attachBaseContext`) or need
 * to survive a SQLCipher key-management failure. Android backs this with `SharedPreferences`, iOS
 * with `NSUserDefaults` — both are plain unencrypted platform stores; the encrypted DB stays the
 * source of truth and this is just a cache in front of it (see `AppMetaProfileRepository`/
 * `AppMetaLocaleRepository`).
 */
interface PlatformKeyValueStore {
    fun getString(key: String): String?

    fun putString(
        key: String,
        value: String,
    )

    fun getBoolean(
        key: String,
        default: Boolean,
    ): Boolean

    fun putBoolean(
        key: String,
        value: Boolean,
    )
}
