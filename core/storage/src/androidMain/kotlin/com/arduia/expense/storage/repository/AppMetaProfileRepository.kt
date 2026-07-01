package com.arduia.expense.storage.repository

import android.content.SharedPreferences
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaProfileRepository(
    private val store: AppMetaLocalStore,
    private val onboardingPrefs: SharedPreferences,
) : ProfileRepository {

    override suspend fun setDisplayName(name: String): Result<Unit> = catchingResult {
        store.update { it.copy(displayName = name) }
        Unit
    }

    override suspend fun getDisplayName(): Result<String> = catchingResult {
        val snapshot = store.read()
        snapshot.displayName
    }

    override suspend fun isOnboardingComplete(): Result<Boolean> {
        // SharedPrefs is the fast, reliable path — immune to SQLCipher key failures.
        if (onboardingPrefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)) return Result.Success(true)
        // Fallback: check the encrypted DB (handles existing installs that wrote the flag there).
        return catchingResult {
            val completed = store.read().onboardingCompleted
            if (completed) {
                // Migrate to SharedPrefs so future reads don't depend on the DB.
                onboardingPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
            }
            completed
        }
    }

    override suspend fun setOnboardingComplete(): Result<Unit> = catchingResult {
        // Write to SharedPrefs synchronously first — survives SQLCipher key-management failures.
        onboardingPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).commit()
        store.update { it.copy(onboardingCompleted = true) }
        Unit
    }

    companion object {
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}
