package com.arduia.expense.storage

import android.content.SharedPreferences

class AndroidPlatformKeyValueStore(
    private val prefs: SharedPreferences,
) : PlatformKeyValueStore {
    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun putString(
        key: String,
        value: String,
    ) {
        prefs.edit().putString(key, value).commit()
    }

    override fun getBoolean(
        key: String,
        default: Boolean,
    ): Boolean = prefs.getBoolean(key, default)

    override fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        prefs.edit().putBoolean(key, value).commit()
    }
}
