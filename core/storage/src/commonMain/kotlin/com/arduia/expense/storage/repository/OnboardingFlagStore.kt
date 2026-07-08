package com.arduia.expense.storage.repository

/**
 * Fast, synchronous read/write for the handful of onboarding flags that need to survive a
 * SQLCipher key-management failure — backed by platform prefs (Android `SharedPreferences`),
 * kept separate from [AppMetaStore] so [AppMetaProfileRepository] never depends on a platform type.
 */
interface OnboardingFlagStore {
    fun getDisplayName(): String?
    fun setDisplayName(name: String)
    fun isOnboardingComplete(): Boolean
    fun setOnboardingComplete()
}
