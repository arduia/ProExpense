package com.arduia.expense.storage

import platform.Foundation.NSUserDefaults

class IosPlatformKeyValueStore(
    suiteName: String = "onboarding_state",
) : PlatformKeyValueStore {

    private val defaults: NSUserDefaults = NSUserDefaults(suiteName = suiteName)

    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) == null) default else defaults.boolForKey(key)

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
