package com.arduia.expense.storage.repository

import android.content.SharedPreferences

class SharedPreferencesOnboardingFlagStore(
    private val prefs: SharedPreferences,
) : OnboardingFlagStore {

    override fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)

    override fun setDisplayName(name: String) {
        prefs.edit().putString(KEY_DISPLAY_NAME, name).commit()
    }

    override fun isOnboardingComplete(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

    override fun setOnboardingComplete() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).commit()
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        const val KEY_DISPLAY_NAME = "display_name"
    }
}
